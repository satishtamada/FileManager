package com.droids.tamada.filemanager.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.droids.tamada.filemanager.Animations.AVLoadingIndicatorView;
import com.droids.tamada.filemanager.app.AppController;
import com.example.satish.filemanager.R;


/**
 * Created by satish on 14/10/16.
 */
public class AboutActivity extends AppCompatActivity {
    private AVLoadingIndicatorView progressBar;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("About us");
        }
        WebView webView = (WebView) findViewById(R.id.webView);
        progressBar= (AVLoadingIndicatorView)findViewById(R.id.progressBar);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.canGoBack();
        webView.goBack();
        webView.loadUrl("http://www.androidhive.info/");
        progressBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().trackScreenView("About Screen");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
