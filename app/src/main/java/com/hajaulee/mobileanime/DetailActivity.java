package com.hajaulee.mobileanime;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    static final String TAG = "DetailActivity";
    TextView mDescription;
    WebView webView;
    AnimeCardData mAnime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().hide();
        mDescription = findViewById(R.id.description);
        webViewSetup();
        setupUIFromAnimeData();
        setupForShowHideDescription();
//        setupEpisodeList();
    }

    public TextView getmDescription() {
        return mDescription;
    }

    private void webViewSetup() {
        webView = findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient() {
            // Khi trang tải xong
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript: (function(){" +
                        "var inter = setInterval(waitDescription, 100);" +
                        "function waitDescription(){" +
                        "   Android.sendLog('Waiting description...');" +
                        "   var len = document.querySelector('.resizable-description').innerHTML.length;" +
                        "   if(len >= 8){" +
                        "       Android.setDescription(document.querySelector('.resizable-description').innerHTML);" +
                        "       clearInterval(inter);" +
                        "   }" +
                        "}" +
                        "var inter1 = setInterval(waitEpisode, 100);" +
                        "function waitEpisode(){" +
                        "   Android.sendLog('Waiting waitEpisode...');" +
                        "   var epList = document.querySelectorAll('.episode-container');" +
                        "   if(epList.length > 0 && epList[0].querySelector('h5').innerHTML.length > 0){" +
                        "       var epListData = [];" +
                        "       for (var i = 0 ; i < epList.length; i++){" +
                        "           epListData.push(epList[i].href+" +
                        "           '"+Tool.SEPARATOR+"'+" +
                        "           epList[i].querySelector('img').src+" +
                        "           '"+Tool.SEPARATOR+"'+" +
                        "           epList[i].querySelector('h5').innerHTML);" +
                        "       }" +
                        "       Android.sendLog('Complete waitEpisode...');" +
                        "       Android.setEpisodeList(epListData);" +
                        "       clearInterval(inter1);" +
                        "   }" +
                        "}" +
                        "})()");
            }
        });
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @SuppressWarnings("unchecked")
    public void setupEpisodeList(){
        final ListView episodeListView = findViewById(R.id.episode_list);
        List<HashMap<String, String>> aList = new ArrayList<>();
        List<EpisodeInfo> epList = mAnime.getEpisodeList().get(0);
        for (int i = 0; i < epList.size(); i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("item_text", epList.get(i).epiName);
            hm.put("item_icon", Integer.toString(R.mipmap.icon));
            hm.put("item_icon_link", epList.get(i).epiImage);
            hm.put("item_link", epList.get(i).epiLink);
            aList.add(hm);
        }
        String[] from = {"item_text", "item_icon"};
        int[] to = {R.id.item_text, R.id.item_icon};

        BaseAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.mylist, from, to){
            @Override
            public View getView(final int position, View view, ViewGroup parent) {
                View row = view;
                if(view == null){
                    row = ((LayoutInflater)getApplicationContext().
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                            inflate(R.layout.mylist, null);
                }
                final ImageView imageView = row.findViewById(R.id.item_icon);
                final TextView textView = row.findViewById(R.id.item_text);
                final Map<String, String> episodeInfo = (Map<String, String>) getItem(position);
                textView.setText(episodeInfo.get("item_text"));
                String imageLink = episodeInfo.get("item_icon_link");
                if(AnimeCardData.LOADED_BITMAP.get(imageLink) != null)
                        imageView.setImageBitmap(AnimeCardData.LOADED_BITMAP.get(imageLink));
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        episodeListView.setSelection(position);
                        webView.loadUrl(episodeInfo.get("item_link"));
                    }
                });
                return row;
            }
        };
        episodeListView.setAdapter(simpleAdapter);
    }
    private void setupForShowHideDescription(){
        final TextView descriptionTextView = findViewById(R.id.description);
        Button showHideDescription = findViewById(R.id.anime_name);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        showHideDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView arrow = findViewById(R.id.arrow);
                if (arrow.getText().equals(getString(R.string.down_arrow))) {
                    arrow.setText(R.string.up_arrow);
                    descriptionTextView.setVisibility(View.GONE);
                } else {
                    arrow.setText(R.string.down_arrow);
                    descriptionTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupUIFromAnimeData(){
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mAnime = (AnimeCardData) mBundle.getSerializable("anime");
            if (mAnime != null) {
                webView.addJavascriptInterface(new AndroidAPI(this, mAnime), "Android");
                ((Button) findViewById(R.id.anime_name)).setText(mAnime.getAnimeCardName());
                mDescription.setText(mAnime.getAnimeCardDescription());
                webView.loadUrl(mAnime.getAnimeCardLink());
            } else {
                Log.e(TAG, "Anime: null");
            }
        }
    }
}