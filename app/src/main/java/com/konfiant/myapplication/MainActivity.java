package com.konfiant.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.konfiant.datenly.Datenly;

import java.security.SecureRandom;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private DatenlyModeReceier datenlyModeReceier = new DatenlyModeReceier();
    private class DatenlyModeReceier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "onReceive: "+intent.getStringExtra("origin"));
            Log.d("onReceive", "onReceive: "+intent.getStringExtra("action"));
            Log.d("onReceive", "onReceive: "+intent.getStringExtra("data"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction("datenly_BroadcastReceiver");
        registerReceiver(datenlyModeReceier, filter);
        Datenly datenly=new Datenly(this,"pb_5KTNe0NfAvGEJjFZIHExCOc8KOxbyP8ISU");
        datenly.commerzdigit();
        datenly.commerzdata("pb_sM9hQMsMAsltA7QdrR2JQ");
        datenly.commerzdata_start();

    }

}