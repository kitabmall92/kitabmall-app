package com.kitabmall.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout offlineLayout;
    private static final String HOME_URL = "https://kitabmall.com";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.parseColor("#C0392B"));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(-1, 8);
        progressBar.setLayoutParams(pbParams);
        progressBar.setMax(100);
        main.addView(progressBar);

        webView = new WebView(this);
        LinearLayout.LayoutParams wvParams = new LinearLayout.LayoutParams(-1, 0, 1f);
        webView.setLayoutParams(wvParams);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String url = r.getUrl().toString();
                if (url.startsWith("https://kitabmall.com") || url.startsWith("http://kitabmall.com")) return false;
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("whatsapp:")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception e) {}
                    return true;
                }
                return false;
            }
            @Override
            public void onPageFinished(WebView v, String url) {
                progressBar.setVisibility(View.GONE);
                if (offlineLayout != null) offlineLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) showOffline();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView v, int p) {
                progressBar.setVisibility(p < 100 ? View.VISIBLE : View.GONE);
                progressBar.setProgress(p);
            }
        });

        main.addView(webView);

        offlineLayout = new LinearLayout(this);
        offlineLayout.setOrientation(LinearLayout.VERTICAL);
        offlineLayout.setGravity(Gravity.CENTER);
        offlineLayout.setBackgroundColor(Color.WHITE);
        offlineLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1f));
        offlineLayout.setVisibility(View.GONE);

        TextView icon = new TextView(this);
        icon.setText("\uD83D\uDCF5");
        icon.setTextSize(50);
        icon.setGravity(Gravity.CENTER);

        TextView msg = new TextView(this);
        msg.setText("Internet nahi hai. Dobara try karein.");
        msg.setTextSize(16);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(20, 10, 20, 20);

        Button btn = new Button(this);
        btn.setText("Retry");
        btn.setBackgroundColor(Color.parseColor("#C0392B"));
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(v -> { if (isConnected()) { offlineLayout.setVisibility(View.GONE); webView.setVisibility(View.VISIBLE); webView.reload(); }});

        offlineLayout.addView(icon);
        offlineLayout.addView(msg);
        offlineLayout.addView(btn);
        main.addView(offlineLayout);

        setContentView(main);
        if (isConnected()) webView.loadUrl(HOME_URL);
        else showOffline();
    }

    private void showOffline() {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.VISIBLE);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    @Override
    public boolean onKeyDown(int k, android.view.KeyEvent e) {
        if (k == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { webView.goBack(); return true; }
        return super.onKeyDown(k, e);
    }
}