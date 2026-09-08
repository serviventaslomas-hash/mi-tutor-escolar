package com.tutorescolar.app;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;
    private DevicePolicyManager dpm;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantener la pantalla encendida y visible por encima del bloqueo de pantalla
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        hideSystemUI();

        // Inicializar Gestor de Políticas de Dispositivo (Device Owner / Admin)
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AdminReceiver.class);

        // Programar el despertador automático diario (cada 24h a las 00:00 o 08:00)
        DailyScheduler.scheduleDailyLockAlarm(this, 0, 0);

        // Inicializar WebView con aceleración por hardware y soporte offline
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Carga la app local o la URL del Tutor Escolar
        webView.loadUrl("https://ais-pre-itrhgizm57lkhtg7sdncmw-166816932854.us-east5.run.app");

        // Activar el bloqueo nativo de pantalla (startLockTask)
        activarModoKioscoNativo();
    }

    private void activarModoKioscoNativo() {
        try {
            if (dpm != null && dpm.isDeviceOwnerApp(getPackageName())) {
                // Modo Empresa / Device Owner: Bloquea Home, Recientes y Notificaciones al 100%
                String[] packages = { getPackageName() };
                dpm.setLockTaskPackages(adminComponent, packages);
            }
            startLockTask();
            Toast.makeText(this, "Tutor Escolar Activo: Modo Kiosco Bloqueado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // Deshabilitar botón de retroceso durante la sesión escolar
        // La tablet no retrocede hasta que el tutor certifique la finalización
        Toast.makeText(this, "Completa la tarea escolar para desbloquear la tablet.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
