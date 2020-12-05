package com.example.quickrentals.Vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.quickrentals.R;

public class TermsAndConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        WebView myWebView = findViewById(R.id.webview);

        myWebView.loadUrl("https://www.termsandcondiitionssample.com/live.php?token=x4CYDi0xnpClutIQ2pZeh3XFzqSB4Gij");
        myWebView.setBackgroundResource(R.drawable.customgrad1);
        myWebView.setBackgroundColor(Color.TRANSPARENT);
        myWebView.getSettings().setJavaScriptEnabled(true);
    }
}