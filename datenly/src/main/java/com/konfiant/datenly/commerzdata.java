package com.konfiant.datenly;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class commerzdata extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String public_key="";
        String internal_id="";
        String group="";
        Intent intent = getIntent();
        if (null != intent) {
            public_key=intent.getStringExtra("public_key");
            internal_id=intent.getStringExtra("internal_id");
            group=intent.getStringExtra("group");
            if(public_key==null)public_key="";
            if(internal_id==null)internal_id="";
            if(group==null)group="";
        }
        if (public_key.equals(""))finish();
        setContentView(R.layout.activity_commerzdata);
        WebView webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.5359.128 Mobile Safari/537.36");
        if (Build.VERSION.SDK_INT >= 21) {CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);}
        else {CookieManager.getInstance().setAcceptCookie(true);}

        String url = "https://api.datenly.com/commerzdata/connector/"+public_key+"/markets?device=android&callback=message&sync=offline&shared_id="+internal_id+"&group="+group+"&market=&first_name=anonymous&last_name=anonymous&email=anonymous@anonymous.com";
        webview.loadUrl(url);
        webview.addJavascriptInterface(this, "Android");
    }

    @JavascriptInterface
    public void postmessage(String action, String data) {
        Datenly.calback("commerzdata",action,data);
        finish();
    }

}