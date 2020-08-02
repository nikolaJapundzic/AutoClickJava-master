package com.github.nestorm001.autoclicker;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import com.github.nestorm001.autoclicker.service.AutoClickService;
import com.github.nestorm001.autoclicker.service.FloatingClickService;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent serviceIntent = null;
    private final int PERMISSION_CODE = 110;

    /**
     * Upon creation, it starts permission check activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                        || Settings.canDrawOverlays(getApplicationContext())) {
                    serviceIntent = new Intent(getApplicationContext(),
                        FloatingClickService.class);
                    startService(serviceIntent);
                    onBackPressed();
                } else {
                    askPermission();
                    Toasts.shortToast(getApplicationContext(), "You need System Alert Window Permission to do this");
                }
            }
        });
    }

    /**
     * Checks if this application has access to "Accessibility" features.
     * Those features enable us to use "gestures", which we use to simulate clicks.
     * @return
     */
    private boolean checkAccess() {
        final String string = getString(R.string.accessibility_service_id);
        final AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        final List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo id : list) {
            if (string.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks permissions. If accessibility permissions are given, nothing happens.
     * If not, it asks for permissions just like onCreate method.
     */
    @Override
    public void onResume() {
        super.onResume();
        final boolean hasPermission = checkAccess();
        Extensions.logd(String.format("has access? %b", hasPermission));
        if (!hasPermission) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
    }

    /**
     * Asks "Overlay Permission Settings" for the floating on off button.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void askPermission() {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:"+getPackageName()));
        startActivityForResult(intent, PERMISSION_CODE);
    }

    /**
     * Disables services.
     */
    @Override
    public void onDestroy() {
        if (serviceIntent != null) {
            Extensions.logd("stop floating click service");
            stopService(serviceIntent);
        }
        if (AutoClickService.autoClickService != null) {
            Extensions.logd("stop auto click service");
            AutoClickService.autoClickService.stopSelf();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                AutoClickService.autoClickService.disableSelf();
            else
                AutoClickService.autoClickService = null;
        }
        super.onDestroy();
    }

    /**
     * Takes the application to the background.
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
