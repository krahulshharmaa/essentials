package com.sameerasw.essentials.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.sameerasw.essentials.shizuku.ShizukuPermissionHelper
import org.lsposed.hiddenapibypass.HiddenApiBypass

object OmniTriggerUtil {
    private const val TAG = "OmniTriggerUtil"

    @SuppressLint("PrivateApi")
    fun trigger(context: Context): Boolean {
        val bundle = Bundle().apply {
            putLong("invocation_time_ms", SystemClock.elapsedRealtime())
            putInt("omni.entry_point", 1) // Entry point for home long press
            putBoolean("micts_trigger", true)
        }

        // 1. Try Shizuku approach first if available and permitted
        val shizukuHelper = ShizukuPermissionHelper(context)
        if (shizukuHelper.isReady() && shizukuHelper.hasPermission()) {
            val result = runCatching {
                val vis = ShizukuUtils.getSystemBinder("voiceinteraction")
                if (vis != null) {
                    val iVimsClass = Class.forName("com.android.internal.app.IVoiceInteractionManagerService")
                    val vims = Class.forName("com.android.internal.app.IVoiceInteractionManagerService\$Stub")
                        .getMethod("asInterface", IBinder::class.java)
                        .invoke(null, vis)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        HiddenApiBypass.invoke(iVimsClass, vims, "showSessionFromSession", null, bundle, 7, "hyperOS_home") as Boolean
                    } else {
                        HiddenApiBypass.invoke(iVimsClass, vims, "showSessionFromSession", null, bundle, 7) as Boolean
                    }
                } else {
                    false
                }
            }.getOrDefault(false)

            if (result) {
                Log.d(TAG, "Triggered via Shizuku successfully")
                return true
            }
        }

        // 2. Fallback to Non-Root Reflection
        return runCatching {
            val vis = Class.forName("android.os.ServiceManager")
                .getMethod("getService", String::class.java)
                .invoke(null, "voiceinteraction") as IBinder?
            
            if (vis != null) {
                val iVimsClass = Class.forName("com.android.internal.app.IVoiceInteractionManagerService")
                val vims = Class.forName("com.android.internal.app.IVoiceInteractionManagerService\$Stub")
                    .getMethod("asInterface", IBinder::class.java)
                    .invoke(null, vis)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HiddenApiBypass.invoke(iVimsClass, vims, "showSessionFromSession", null, bundle, 7, "hyperOS_home") as Boolean
                } else {
                    HiddenApiBypass.invoke(iVimsClass, vims, "showSessionFromSession", null, bundle, 7) as Boolean
                }
            } else {
                false
            }
        }.onFailure { e ->
            Log.e(TAG, "Trigger failed", e)
        }.getOrDefault(false)
    }
}
