package com.tutorescolar.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DailyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {

            // Re-programar el despertador 24h al encender la tablet
            DailyScheduler.scheduleDailyLockAlarm(context, 0, 0);

            // Iniciar inmediatamente el Tutor Escolar para tomar control al reiniciar
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(launchIntent);
        }
    }
}
