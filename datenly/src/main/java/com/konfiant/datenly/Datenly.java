package com.konfiant.datenly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Datenly {

    private Boolean wifi_requested=false;
    private JSONObject settings_shared = new JSONObject();
    public Context context;
    private String public_key="";
    private String commerzdata_public_key="";
    private String eap_id="";
    private String audiance_email="";
    private String audiance_phone="";
    private String audiance_internal_id="";
    private String audiance_first_name="";
    private String audiance_last_name="";
    private String audiance_birth_day="";
    private String audiance_internal_group="";
    private String commerzdigit_ssid="comerzbb";
    private String api_host="https://sdk.datenlyapis.com";
    public static  Datenly INSTANCE;

    public Datenly(Context c, String public_key) {
        this.context=c;
        this.public_key=public_key;
        this.INSTANCE=this;
        datenly_init();
    }
    private void datenly_init()  {
        SharedPreferences Shared_settings = context.getSharedPreferences("datenly", 0);
        String settings= Shared_settings.getString("commerzdigit_settings","");
        SharedPreferences.Editor editor = Shared_settings.edit();
        editor.putString("datenly_public_key", public_key);
        if (settings.equals("")){
            eap_id=String.valueOf (new Random().nextInt(800) + 100)+String.valueOf(System.currentTimeMillis()).substring(3);
            String settings_temp = "{'privacy_read':false,'privacy_read_time':0,'uuid':'','eap_id':'"+eap_id+"','eap_status':false,'wifi_access':true,'wifi_last_request':0," +
                    "'wifi_request_delay_time':6}";
            try { settings_shared = new JSONObject(settings_temp);}catch (Exception ignored) {}
            editor.putString("commerzdigit_settings", settings_temp);
        }else {
            try {
                settings_shared = new JSONObject(settings);
                settings_shared.remove("app_id");
                eap_id=settings_shared.getString("eap_id");
                editor.putString("commerzdigit_settings", settings_shared.toString());
            } catch (JSONException e) {}
        }
        editor.apply();
        app_eap_internal(true);
    }

    public static void calback(String origin,String action, String data){
        Intent intent = new Intent("datenly_BroadcastReceiver");
        intent.putExtra("origin", origin);
        intent.putExtra("action", action);
        intent.putExtra("data", data);
        intent.setComponent(null);
        INSTANCE.context.sendBroadcast(intent);
    }

    public void audiance(String key,String value){
        if (key.equals("email"))audiance_email=value;
        else if (key.equals("phone"))audiance_phone=value;
        else if (key.equals("first_name"))audiance_first_name=value;
        else if (key.equals("last_name"))audiance_last_name=value;
        else if (key.equals("birth_day"))audiance_birth_day=value;
        else if (key.equals("internal_id"))audiance_internal_id=value;
        else if (key.equals("group"))audiance_internal_group=value;
    }

    public JSONObject get_audiance(){
        JSONObject audiance=new JSONObject();
        try {
            audiance.put("email",audiance_email);
            audiance.put("phone",audiance_phone);
            audiance.put("first_name",audiance_first_name);
            audiance.put("last_name",audiance_last_name);
            audiance.put("birth_day",audiance_birth_day);
            audiance.put("internal_id",audiance_internal_id);
            audiance.put("group",audiance_internal_group);
        } catch (JSONException e) {}
        return audiance;
    }

    public void commerzdata(String public_key)  {
        commerzdata_public_key=public_key;
    }

    public void commerzdata_start()  {
        if (commerzdata_public_key.equals(""))return;
        Intent intent = new Intent(context, commerzdata.class);
        intent.putExtra("public_key",commerzdata_public_key);
        intent.putExtra("internal_id",audiance_internal_id);
        intent.putExtra("group",audiance_internal_group);
        context.startActivity(intent);
    }

    public void commerzdigit()  {
        Intent intent;
        try {
            int timestamp = Integer.parseInt(Long.toString(System.currentTimeMillis()/1000));
            if (settings_shared.getInt("wifi_last_request")>0 && settings_shared.getBoolean("wifi_access") && !settings_shared.getBoolean("eap_status")){
                setWifi();
            }else if (timestamp-settings_shared.getInt("wifi_last_request") > settings_shared.getInt("wifi_request_delay_time") ){
                if (!settings_shared.getBoolean("wifi_access")){
                    intent = new Intent(context, wifi_permission.class);
                    context.startActivity(intent);
                }else if (!settings_shared.getBoolean("eap_status")){
                    intent = new Intent(context, explain.class);
                    intent.putExtra("settings_shared",settings_shared.toString());
                    context.startActivity(intent);
                }
            }
        } catch (JSONException e) {}
    }

    public void commerzdigit_setup(){setWifi();}

    private void setWifi(){
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            final WifiEnterpriseConfig wifiConf = new WifiEnterpriseConfig();
            wifiConf.setIdentity(eap_id);
            wifiConf.setPassword(eap_id);
            wifiConf.setEapMethod(WifiEnterpriseConfig.Eap.PWD);
            final WifiNetworkSuggestion suggestion1 = new WifiNetworkSuggestion.Builder().setWpa2EnterpriseConfig(wifiConf).setSsid(commerzdigit_ssid).setIsHiddenSsid(true).build();
            final List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
            suggestionsList.add(suggestion1);
            final int status = wifiManager.addNetworkSuggestions(suggestionsList);
            settings_shared.remove("wifi_access");
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                try { settings_shared.put("wifi_access",false);} catch (JSONException e) {e.printStackTrace();}
            }else {
                try { settings_shared.put("wifi_access",true);} catch (JSONException e) {e.printStackTrace();}
            }
        }else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WifiEnterpriseConfig enterpriseConfig;
            enterpriseConfig = new WifiEnterpriseConfig();
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = commerzdigit_ssid;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            enterpriseConfig.setIdentity(eap_id);
            enterpriseConfig.setPassword(eap_id);
            enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PWD);
            wifiConfig.enterpriseConfig = enterpriseConfig;
            wifiManager.addNetwork(wifiConfig);
            settings_shared.remove("eap_status");
            try {settings_shared.put("eap_status",true);} catch (JSONException e) {e.printStackTrace();}
        }
        int timestamp = Integer.parseInt(Long.toString(System.currentTimeMillis()/1000));
        SharedPreferences Shared_settings = context.getSharedPreferences("datenly", 0);
        settings_shared.remove("wifi_last_request");
        try {settings_shared.put("wifi_last_request",timestamp);} catch (JSONException e) {e.printStackTrace();}
        SharedPreferences.Editor editor = Shared_settings.edit();
        editor.putString("commerzdigit_settings", settings_shared.toString());
        editor.apply();
        app_eap_internal(wifi_requested);
        try {Thread.sleep(4000);} catch (InterruptedException ignored) { }
        consent_auth();
    }


    private void consent_auth(){
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        SharedPreferences Shared_settings = context.getSharedPreferences("datenly", 0);
        boolean eap_status= false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            List<WifiNetworkSuggestion> result = wifiManager.getNetworkSuggestions();
            for (int i=0; i<result.size(); i++)if (result.get(i).getSsid().equals(commerzdigit_ssid)) eap_status=true;
        }else  {
            eap_status=true;
        }
        settings_shared.remove("eap_status");
        try {settings_shared.put("eap_status",eap_status);} catch (JSONException e) {}
        SharedPreferences.Editor editor = Shared_settings.edit();
        editor.putString("commerzdigit_settings", settings_shared.toString());
        editor.apply();
        if (!wifi_requested){
            wifi_requested=true;
            try {Thread.sleep(5000);} catch (InterruptedException ignored) { }
            setWifi();
            try {
                if (!settings_shared.getString("uuid").equals("") && settings_shared.getInt("wifi_last_request")>0 && settings_shared.getBoolean("wifi_access") && settings_shared.getBoolean("eap_status")){
                    calback("commerzdigit","setup",settings_shared.getString("uuid"));
                }
            } catch (JSONException e) {}
        }
    }

    private void app_eap_internal(Boolean ping){
        String uuid="";
        String eap_status="";
        try {
            uuid=settings_shared.getString("uuid");
            eap_status=String.valueOf(settings_shared.getBoolean("eap_status"));
        } catch (JSONException e) {}
        String url_eap =api_host+"/android/commerzdigit/beacon/eap?pub_key="+public_key+"&eap="+eap_id+"&uuid="+uuid+"&email="+audiance_email+"&phone="+audiance_phone+"&first_name="+audiance_first_name+"&last_name="+audiance_last_name+"&birth_day="+audiance_birth_day+"&internal_id="+audiance_internal_id;
        if (ping) url_eap+="&ping=true&eap_status="+eap_status;
        final String url_target=url_eap;
        new Thread(() -> {
            URL url = null;
            try {url = new URL(url_target);} catch (MalformedURLException e) {e.printStackTrace(); }
            HttpURLConnection urlConnection;
            try {
                assert url != null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(in));
                try {
                    JSONObject sdk_settings=new JSONObject();
                    String line;
                    while ((line = reader.readLine()) != null) sdk_settings = new JSONObject(line);
                    try {
                        if (sdk_settings.getBoolean("status")){
                            settings_shared.remove("uuid");
                            settings_shared.put("uuid",sdk_settings.getString("uuid"));
                            SharedPreferences Shared_settings = context.getSharedPreferences("datenly", 0);
                            SharedPreferences.Editor editor = Shared_settings.edit();
                            editor.putString("commerzdigit_settings", settings_shared.toString());
                            editor.apply();
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                } catch  (JSONException e) {e.printStackTrace();}
                finally {
                    try {reader.close();}
                    catch (IOException e) {e.printStackTrace();}
                    urlConnection.disconnect();}
            } catch (IOException e) {e.printStackTrace();}
        }).start();
    }

}


