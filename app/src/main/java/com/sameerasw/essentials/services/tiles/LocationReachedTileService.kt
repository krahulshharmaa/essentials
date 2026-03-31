package com.sameerasw.essentials.services.tiles

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import androidx.annotation.RequiresApi
import com.sameerasw.essentials.FeatureSettingsActivity
import com.sameerasw.essentials.R
import com.sameerasw.essentials.data.repository.LocationReachedRepository
import com.sameerasw.essentials.services.LocationReachedService
import com.sameerasw.essentials.utils.PermissionUtils

@RequiresApi(Build.VERSION_CODES.N)
class LocationReachedTileService : BaseTileService() {
    private lateinit var repository: LocationReachedRepository

    override fun onCreate() {
        super.onCreate()
        repository = LocationReachedRepository(this)
    }

    override fun onTileClick() {
        val activeId = LocationReachedRepository.activeAlarmId.value
        val alarms = repository.getAlarms()
        
        if (activeId != null) {
            // Stop tracking
            repository.saveActiveAlarmId(null)
            LocationReachedService.stop(this)
        } else {
            // Start tracking for the last trip or the first alarm
            val lastTrip = repository.getLastTrip()
            val targetAlarm = lastTrip ?: alarms.firstOrNull()
            
            if (targetAlarm != null) {
                repository.saveActiveAlarmId(targetAlarm.id)
                LocationReachedService.start(this)
            } else {
                // No alarms to start, open settings
                val intent = Intent(this, FeatureSettingsActivity::class.java).apply {
                    putExtra("feature", "Location reached")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
        }
    }

    override fun getTileLabel(): String = getString(R.string.tile_location_reached)

    override fun getTileSubtitle(): String {
        val activeId = LocationReachedRepository.activeAlarmId.value
        return if (activeId != null) {
            repository.getAlarms().find { it.id == activeId }?.name ?: "Tracking"
        } else {
            "Idle"
        }
    }

    override fun hasFeaturePermission(): Boolean {
        return PermissionUtils.hasLocationPermission(this) && 
               PermissionUtils.hasBackgroundLocationPermission(this)
    }

    override fun getTileState(): Int {
        return if (LocationReachedRepository.activeAlarmId.value != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
    }
}
