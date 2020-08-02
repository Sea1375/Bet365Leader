package com.e.bet365leader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    String mOverridejsContent;
    PayloadRecorder mRecorder;
    //RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(getString(R.string.default_url));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        AssetManager am = this.getAssets();
        try {
            InputStream inputStream = am.open("override.js");
            int filesize = inputStream.available();

            byte[] data =new byte[filesize];
            int nResult = inputStream.read(data);
            if(nResult == 0) {
                Log.e("assets", "read 0 byte from override.js");
            }
            this.mOverridejsContent = new String(data);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.evaluateJavascript(MainActivity.this.mOverridejsContent, null);
                super.onPageStarted(view, url, favicon);
            }
        });

        mRecorder = new PayloadRecorder();
        webView.addJavascriptInterface(mRecorder, "recorder");

        //this.mRequestQueue = Volley.newRequest

    }
}
class PayloadRecorder {
    @JavascriptInterface
    public void recordPayload(String method, String url, String payload) {
        Log.e("Record", String.format("%s %s %s", method, url, payload));
    }
}
