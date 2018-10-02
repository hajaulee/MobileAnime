package com.hajaulee.mobileanime;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
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
                if (url.contains("animelon.com/series"))
                    webView.loadUrl(Javascript.jSubLoadDescriptionAndEpisodeScript);
                else if (url.contains("animelon.com/video")) {
                    webView.loadUrl(Javascript.jsubAutoPlayScript);
                }
            }
        });
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // chromium, enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @SuppressWarnings("unchecked")
    public void setupEpisodeList() {
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

        BaseAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.mylist, from, to) {
            @Override
            public View getView(final int position, View view, ViewGroup parent) {
                View row = view;
                if (view == null) {
                    row = ((LayoutInflater) getApplicationContext().
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                            inflate(R.layout.mylist, null);
                }
                final ImageView imageView = row.findViewById(R.id.item_icon);
                final TextView textView = row.findViewById(R.id.item_text);
                final Map<String, String> episodeInfo = (Map<String, String>) getItem(position);
                textView.setText(episodeInfo.get("item_text"));
                String imageLink = episodeInfo.get("item_icon_link");
                Bitmap bmp = Tool.getImageFromMapOrInternal(DetailActivity.this, imageLink);
                if (bmp != null)
                    imageView.setImageBitmap(bmp);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < episodeListView.getChildCount(); i++) {
                            episodeListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        webView.loadUrl(episodeInfo.get("item_link"));
                    }
                });
                return row;
            }
        };
        episodeListView.setAdapter(simpleAdapter);
        setListViewHeightBasedOnChildren(episodeListView);
    }

    private void setupForShowHideDescription() {
        final TextView descriptionTextView = findViewById(R.id.description);
        Button showHideDescription = findViewById(R.id.anime_name);
        final TextView arrow = findViewById(R.id.arrow);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        View.OnClickListener showHideDescriptionListen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrow.getText().equals(getString(R.string.down_arrow))) {
                    arrow.setText(R.string.up_arrow);
                    descriptionTextView.setVisibility(View.GONE);
                } else {
                    arrow.setText(R.string.down_arrow);
                    descriptionTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        showHideDescription.setOnClickListener(showHideDescriptionListen);
        arrow.setOnClickListener(showHideDescriptionListen);

    }

    private void setupUIFromAnimeData() {
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}