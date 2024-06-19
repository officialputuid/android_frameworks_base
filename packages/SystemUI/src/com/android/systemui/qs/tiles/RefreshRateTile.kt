/*
 * Copyright (C) 2020 The Android Open Source Project
 *               2021 AOSP-Krypton Project
 *               2023-2024 the RisingOS android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.qs.tiles

import android.content.ComponentName
import android.content.Intent
import android.database.ContentObserver
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings.System.MIN_REFRESH_RATE
import android.provider.Settings.System.PEAK_REFRESH_RATE
import android.service.quicksettings.Tile
import android.util.Log
import android.view.Display
import android.view.View
import com.android.internal.logging.nano.MetricsProto.MetricsEvent
import com.android.internal.logging.MetricsLogger
import com.android.systemui.res.R
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.dagger.qualifiers.Main
import com.android.systemui.plugins.qs.QSTile.Icon
import com.android.systemui.plugins.qs.QSTile.State
import com.android.systemui.plugins.statusbar.StatusBarStateController
import com.android.systemui.plugins.ActivityStarter
import com.android.systemui.plugins.FalsingManager
import com.android.systemui.qs.logging.QSLogger
import com.android.systemui.qs.tileimpl.QSTileImpl
import com.android.systemui.qs.QsEventLogger
import com.android.systemui.qs.QSHost
import com.android.systemui.util.settings.SystemSettings
import javax.inject.Inject

class RefreshRateTile @Inject constructor(
    host: QSHost,
    uiEventLogger: QsEventLogger,
    @Background backgroundLooper: Looper,
    @Main private val mainHandler: Handler,
    falsingManager: FalsingManager,
    metricsLogger: MetricsLogger,
    statusBarStateController: StatusBarStateController,
    activityStarter: ActivityStarter,
    qsLogger: QSLogger,
    private val systemSettings: SystemSettings,
): QSTileImpl<State>(
    host,
    uiEventLogger,
    backgroundLooper,
    mainHandler,
    falsingManager,
    metricsLogger,
    statusBarStateController,
    activityStarter,
    qsLogger,
) {
    private val settingsObserver: SettingsObserver
    private val tileLabel: String = mContext.resources.getString(R.string.refresh_rate_tile_label)
    private val autoModeLabel: String = mContext.resources.getString(R.string.auto_mode_label)
    private var defaultMinRefreshRate: Float = DEFAULT_REFRESH_RATE
    private var defaultPeakRefreshRate: Float = DEFAULT_REFRESH_RATE
    private var uniqueRefreshRatesList: List<Float> = listOf(DEFAULT_REFRESH_RATE)
    private var currentRateIndex: Int = 0
    private var inAutoMode: Boolean = true
    private var ignoreSettingsChange: Boolean = false
    private var refreshRateMode = Mode.AUTO

    init {
        val displayManager = mContext.getSystemService(DisplayManager::class.java)
        val defaultDisplay = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        defaultDisplay?.let { display ->
            val supportedModes = display.supportedModes
            supportedModes?.let {
                defaultMinRefreshRate = it.minOfOrNull { mode -> mode.refreshRate } ?: DEFAULT_REFRESH_RATE
                defaultPeakRefreshRate = it.maxOfOrNull { mode -> mode.refreshRate } ?: DEFAULT_REFRESH_RATE
                uniqueRefreshRatesList = supportedModes.map { mode -> mode.refreshRate }.sorted()
            }
        }
        settingsObserver = SettingsObserver()
    }

    override fun newTileState() = State().apply {
        icon = ResourceIcon.get(R.drawable.ic_refresh_rate)
        state = Tile.STATE_ACTIVE
    }

    override fun getLongClickIntent() = Companion.displaySettingsIntent

    override fun isAvailable() = uniqueRefreshRatesList.size > 1

    override fun getTileLabel(): CharSequence = tileLabel

    override protected fun handleInitialize() {
        settingsObserver.observe()
        updateMode()
    }

    override fun handleClick(view: View?) {
        if (refreshRateMode == Mode.AUTO) {
            refreshRateMode = Mode.CUSTOM
            currentRateIndex = 0
        } else {
            currentRateIndex++
            if (currentRateIndex >= uniqueRefreshRatesList.size) {
                refreshRateMode = Mode.AUTO
                currentRateIndex = 0
            }
        }
        updateRefreshRate()
        refreshState()
    }

    override protected fun handleUpdateState(state: State?, arg: Any?) {
        state?.label = tileLabel
        state?.contentDescription = tileLabel
        state?.secondaryLabel = getTitleForMode(refreshRateMode)
    }

    private fun getTitleForMode(mode: Mode) =
        when (mode) {
            Mode.AUTO -> autoModeLabel
            Mode.CUSTOM -> "${uniqueRefreshRatesList[currentRateIndex].toInt()}Hz"
        }

    override fun getMetricsCategory(): Int = MetricsEvent.AWAKEN

    override fun destroy() {
        settingsObserver.unobserve()
        super.destroy()
    }

    private fun updateMode() {
        val minRate = systemSettings.getFloat(MIN_REFRESH_RATE, defaultMinRefreshRate)
        val maxRate = systemSettings.getFloat(PEAK_REFRESH_RATE, defaultPeakRefreshRate)
        if (minRate == maxRate) {
            currentRateIndex = uniqueRefreshRatesList.indexOfFirst { rate ->
                rate in minRate..maxRate
            }
            if (currentRateIndex == -1) {
                currentRateIndex = 0
            }
        }
        refreshRateMode = when {
            minRate == maxRate && minRate == defaultMinRefreshRate -> Mode.CUSTOM
            minRate == maxRate && minRate != defaultMinRefreshRate -> Mode.CUSTOM
            else -> Mode.AUTO
        }
    }
    
    private fun updateRefreshRate() {
        val (minRate, maxRate) = when (refreshRateMode) {
            Mode.AUTO -> defaultMinRefreshRate to defaultPeakRefreshRate
            Mode.CUSTOM -> uniqueRefreshRatesList[currentRateIndex] to uniqueRefreshRatesList[currentRateIndex]
        }
        ignoreSettingsChange = true
        systemSettings.putFloat(MIN_REFRESH_RATE, minRate)
        systemSettings.putFloat(PEAK_REFRESH_RATE, maxRate)
        ignoreSettingsChange = false
    }

    private inner class SettingsObserver : ContentObserver(mainHandler) {
        private var isObserving = false

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (!ignoreSettingsChange) {
                updateMode()
                refreshState()
            }
        }

        fun observe() {
            if (isObserving) return
            isObserving = true
            systemSettings.registerContentObserver(MIN_REFRESH_RATE, this)
            systemSettings.registerContentObserver(PEAK_REFRESH_RATE, this)
        }

        fun unobserve() {
            if (!isObserving) return
            isObserving = false
            systemSettings.unregisterContentObserver(this)
        }
    }

    companion object {
        const val TILE_SPEC = "refresh_rate"
        private const val TAG = "RefreshRateTile"
        private const val DEBUG = false
        private const val DEFAULT_REFRESH_RATE = 60f
        private val displaySettingsIntent =
            Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$DisplaySettingsActivity"))
    }
    
    private enum class Mode {
        CUSTOM,
        AUTO,
    }
}
