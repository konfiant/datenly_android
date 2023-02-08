package com.konfiant.datenly;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class wifi_permission extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_permission);
        Datenly datenly=Datenly.INSTANCE;

        LinearLayout diplay_one = findViewById(R.id.datenly_commerzDigit_diplay_one);
        LinearLayout diplay_two = findViewById(R.id.datenly_commerzDigit_diplay_two);
        LinearLayout diplay_three = findViewById(R.id.datenly_commerzDigit_diplay_three);
        Button btn_next = findViewById(R.id.datenly_commerzDigit_btn1);
        Button btn_settings_device = findViewById(R.id.datenly_commerzDigit_btn2);
        Button btn_finish_activity = findViewById(R.id.datenly_commerzDigit_btn3);
        Button btn_close = findViewById(R.id.datenly_commerzDigit_close);
        LinearLayout require_wifi_api29 = findViewById(R.id.require_wifi_api29);
        ImageView require_wifi_api30 = findViewById(R.id.require_wifi_api30);
        LinearLayout datenly_commerzDigit_copy = findViewById(R.id.datenly_commerzDigit_copy);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            require_wifi_api29.setVisibility(View.VISIBLE);
        }else {
            require_wifi_api30.setVisibility(View.VISIBLE);
        }

        datenly_commerzDigit_copy.setOnClickListener(view -> copy_clipboard());
        TextView permission_5 = findViewById(R.id.permission_5);
        permission_5.setText(permission_5.getText().toString().replace("appName",datenly.context.getApplicationInfo().loadLabel(datenly.context.getPackageManager()).toString()));

        btn_next.setOnClickListener(view -> {

            btn_next.setVisibility(View.GONE);
            diplay_one.setVisibility(View.GONE);
            diplay_two.setVisibility(View.VISIBLE);
            btn_settings_device.setVisibility(View.VISIBLE);
        });
        btn_settings_device.setOnClickListener(view -> {
            btn_settings_device.setVisibility(View.GONE);
            diplay_two.setVisibility(View.GONE);
            diplay_three.setVisibility(View.VISIBLE);
            btn_finish_activity.setVisibility(View.VISIBLE);
        });

        btn_finish_activity.setOnClickListener(view -> {
            btn_finish_activity.setVisibility(View.GONE);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    datenly.commerzdigit_setup();
                }
            };
            thread.start();
            try {Thread.sleep(1000);} catch (InterruptedException ignored) { }
            finish();
        });
        btn_close.setOnClickListener(view -> {
            finish();
        });
    }

    private void copy_clipboard(){
        Context context=getBaseContext();
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(getString(R.string.wifi_controle_select));
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", getString(R.string.wifi_controle_select));
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, getString(R.string.wifi_controle_select)+" Copied", Toast.LENGTH_LONG).show();
    }

}