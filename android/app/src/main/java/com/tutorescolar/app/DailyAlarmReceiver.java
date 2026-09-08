package com.tutorescolar.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class DailyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Despertar la pantalla de la tablet y mantener CPU encendida
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        if (pm != null) {
            wakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE,
                    "tutorescolar:daily_alarm_wake"
            );
            wakeLock.acquire(15000); // 15 segundos de wakelock para asegurar lanzamiento
        }

        // 2. Abrir MainActivity en primer plano
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        );
        context.startActivity(activityIntent);

        // 3. Re-programar la alarma para las próximas 24 horas
        DailyScheduler.scheduleDailyLockAlarm(context, 0, 0);

        if (wakeLock != null && wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (Exception ignored) {}
        }
    }
}
