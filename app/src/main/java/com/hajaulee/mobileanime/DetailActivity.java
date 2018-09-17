package com.hajaulee.mobileanime;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    static final String TAG = "DetailActivity";
    Toolbar mToolbar;
    ImageView mAnimeImage;
    TextView mDescription;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = findViewById(R.id.detail_toolbar);
        mAnimeImage = findViewById(R.id.ivImage);
        mDescription = findViewById(R.id.tvDescription);
        webView = findViewById(R.id.web);
//        webView.setWebViewClient(new MyWebViewClient(webView, SUBTITLE.JSUB));
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.addJavascriptInterface(new AndroidAPI(), "Android");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            AnimeCardData mAnime = (AnimeCardData) mBundle.getSerializable("anime");
            if (mAnime != null){
                mToolbar.setTitle(mAnime.getAnimeCardName());
                mAnimeImage.setImageBitmap(mAnime.getBitmapImage());
                mDescription.setText(mAnime.getAnimeCardDescription());
                webView.loadUrl(mAnime.getAnimeCardLink());
            }else{
                Log.e(TAG, "Anime: null");
            }
        }
    }
}