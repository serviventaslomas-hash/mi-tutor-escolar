package com.tutorescolar.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class DailyScheduler {

    public static final String ACTION_DAILY_LOCK = "com.tutorescolar.app.ACTION_DAILY_LOCK";

    public static void scheduleDailyLockAlarm(Context context, int targetHour, int targetMinute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        intent.setAction(ACTION_DAILY_LOCK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, flags);

        // Configurar la hora de activación (por defecto 00:00 hrs todos los días)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, targetHour);
        calendar.set(Calendar.MINUTE, targetMinute);
        calendar.set(Calendar.SECOND, 0);

        // Si ya pasó la hora de hoy, programar para mañana
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Programar alarma exacta que despierta la CPU incluso en reposo (Doze Mode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}
