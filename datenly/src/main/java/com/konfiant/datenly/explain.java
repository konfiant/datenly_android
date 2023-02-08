package com.konfiant.datenly;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class explain extends Activity {
    private JSONObject settings_shared = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        Datenly datenly=Datenly.INSTANCE;
        TextView datenly_commerzDigit_text_3 = findViewById(R.id.datenly_commerzDigit_text_3);
        TextView datenly_commerzDigit_text_4 = findViewById(R.id.datenly_commerzDigit_text_4);
        LinearLayout require_wifi_api29 = findViewById(R.id.require_wifi_api29);
        ImageView require_wifi_api30 = findViewById(R.id.require_wifi_api30);
        Button btn_require_wifi = findViewById(R.id.datenly_commerzDigit_btn_require_wifi);
        LinearLayout require_wifi_step1 =findViewById(R.id.require_wifi_step1);
        LinearLayout require_wifi_step2 =findViewById(R.id.require_wifi_step2);
        Button button = findViewById(R.id.datenly_commerzDigit_btn);
        Button btn_close_activity = findViewById(R.id.datenly_commerzDigit_close);

        datenly_commerzDigit_text_3.setText(datenly_commerzDigit_text_3.getText().toString().replace("appName",datenly.context.getApplicationInfo().loadLabel(datenly.context.getPackageManager()).toString()));
        datenly_commerzDigit_text_4.setText(datenly_commerzDigit_text_4.getText().toString().replace("appName",datenly.context.getApplicationInfo().loadLabel(datenly.context.getPackageManager()).toString()));
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
            require_wifi_api29.setVisibility(View.VISIBLE);
        }else {
            require_wifi_api30.setVisibility(View.VISIBLE);
        }
        btn_require_wifi.setOnClickListener(view -> {
            btn_require_wifi.setVisibility(View.GONE);
            require_wifi_step1.setVisibility(View.GONE);
            require_wifi_step2.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        });

        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            try {
                settings_shared=new JSONObject(intent.getStringExtra("settings_shared"));
            } catch (JSONException e) {
                settings_shared = new JSONObject();
            }
        }


        button.setOnClickListener(view -> {
            settings_shared.remove("privacy_read");
            settings_shared.remove("privacy_read_time");
            long tsLong;
            tsLong = System.currentTimeMillis()/1000;
            int timestamp = Integer.parseInt(Long.toString(tsLong));
            try {
                settings_shared.put("privacy_read",true);
                settings_shared.put("privacy_read_time",timestamp);
                SharedPreferences Shared_settings = explain.this.getSharedPreferences("datenly", 0);
                SharedPreferences.Editor editor = Shared_settings.edit();
                editor.putString("commerzdigit_settings", settings_shared.toString());
                editor.apply();
            } catch (JSONException e) {e.printStackTrace();}
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

        btn_close_activity.setOnClickListener(view -> finish());


    }
}