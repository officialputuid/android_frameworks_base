/*
 * Copyright (C) 2023 The LineageOS Project
 * Copyright (C) 2024 The AwakenOS Project
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
 * limitations under the License.
 */

package com.android.systemui.awaken

import com.android.systemui.qs.tileimpl.QSTileImpl
import com.android.systemui.qs.tiles.AmbientDisplayTile
import com.android.systemui.qs.tiles.AODTile
import com.android.systemui.qs.tiles.CaffeineTile
import com.android.systemui.qs.tiles.CellularTile
import com.android.systemui.qs.tiles.CompassTile
import com.android.systemui.qs.tiles.CPUInfoTile
import com.android.systemui.qs.tiles.DataSwitchTile
import com.android.systemui.qs.tiles.DcDimmingTile;
import com.android.systemui.qs.tiles.HeadsUpTile
import com.android.systemui.qs.tiles.MonoToggleTile
import com.android.systemui.qs.tiles.ScreenshotTile
import com.android.systemui.qs.tiles.SoundTile
import com.android.systemui.qs.tiles.SyncTile
import com.android.systemui.qs.tiles.UsbTetherTile
import com.android.systemui.qs.tiles.VpnTile
import com.android.systemui.qs.tiles.WifiTile
import com.android.systemui.qs.tiles.RefreshRateTile

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface AwakenModule {
    /** Inject AmbientDisplayTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(AmbientDisplayTile.TILE_SPEC)
    fun bindAmbientDisplayTile(ambientDisplayTile: AmbientDisplayTile): QSTileImpl<*>

    /** Inject AODTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(AODTile.TILE_SPEC)
    fun bindAODTile(aodTile: AODTile): QSTileImpl<*>

    /** Inject CaffeineTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CaffeineTile.TILE_SPEC)
    fun bindCaffeineTile(caffeineTile: CaffeineTile): QSTileImpl<*>

    /** Inject CellularTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CellularTile.TILE_SPEC)
    fun bindCellularTile(cellularTile: CellularTile): QSTileImpl<*>

    /** Inject CompassTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CompassTile.TILE_SPEC)
    fun bindCompassTile(compassTile: CompassTile): QSTileImpl<*>

    /** Inject CPUInfoTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CPUInfoTile.TILE_SPEC)
    fun bindCPUInfoTile(cpuInfoTile: CPUInfoTile): QSTileImpl<*>

    /** Inject DataSwitchTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(DataSwitchTile.TILE_SPEC)
    fun bindDataSwitchTile(dataSwitchTile: DataSwitchTile): QSTileImpl<*>

    /** Inject DcDimmingTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(DcDimmingTile.TILE_SPEC)
    fun bindDcDimmingTile(dcDimmingTile: DcDimmingTile): QSTileImpl<*>

    /** Inject HeadsUpTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(HeadsUpTile.TILE_SPEC)
    fun bindHeadsUpTile(headsUpTile: HeadsUpTile): QSTileImpl<*>

    /** Inject MonoToggleTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(MonoToggleTile.TILE_SPEC)
    fun bindMonoToggleTile(monoToggleTile: MonoToggleTile): QSTileImpl<*>

    /** Inject ScreenshotTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(ScreenshotTile.TILE_SPEC)
    fun bindScreenshotTile(screenshotTile: ScreenshotTile): QSTileImpl<*>

    /** Inject SoundTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(SoundTile.TILE_SPEC)
    fun bindSoundTile(soundTile: SoundTile): QSTileImpl<*>

    /** Inject SyncTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(SyncTile.TILE_SPEC)
    fun bindSyncTile(syncTile: SyncTile): QSTileImpl<*>

    /** Inject UsbTetherTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(UsbTetherTile.TILE_SPEC)
    fun bindUsbTetherTile(usbTetherTile: UsbTetherTile): QSTileImpl<*>

    /** Inject VpnTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(VpnTile.TILE_SPEC)
    fun bindVpnTile(vpnTile: VpnTile): QSTileImpl<*>

    /** Inject WifiTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(WifiTile.TILE_SPEC)
    fun bindWifiTile(wifiTile: WifiTile): QSTileImpl<*>

    /** Inject RefreshRateTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(RefreshRateTile.TILE_SPEC)
    fun bindRefreshRateTile(refreshRateTile: RefreshRateTile): QSTileImpl<*>
}
