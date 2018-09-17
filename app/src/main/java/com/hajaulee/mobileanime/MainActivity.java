package com.hajaulee.mobileanime;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.hajaulee.mobileanime.SUBTITLE.JSUB;
import static com.hajaulee.mobileanime.SUBTITLE.VSUB;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter jsubSectionsPagerAdapter;
    private SectionsPagerAdapter vsubSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public boolean isLoadingMore = false;
    private boolean vSubLoaded = false;
    private boolean jSubLoaded = false;
    private ViewPager jsubViewPager;
    private ViewPager vsubViewPager;
    private Button vSub;
    private Button jSub;
    private int currentLanguageSubtitle = 0;
    private int currentSection = 0;
    private SearchView searchView;
    private int TIME_TO_BACK = 2000;
    private WebView jsubWebView;
    private WebView vsubWebView;
    private FloatingActionButton fab;
    private List<AnimeCardData> totalJsubAnimeList = new ArrayList<>();
    private List<AnimeCardData> totalVsubAnimeList = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private JsubContentFragment[] jsubContentFragments = new JsubContentFragment[4];
    private VsubContentFragment[] vsubContentFragments = new VsubContentFragment[4];
    private DialogSet dialogSet;
    @Override
    @SuppressWarnings(value = "unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dialogSet = new DialogSet(this);
        dialogSet.showHelloDialog();
        AnimeCardData.LOADED_BITMAP = new ConcurrentHashMap<>();
        Tool.getLoadedBitmap(getApplicationContext());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        jsubSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), JSUB);
        vsubSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), VSUB);
        // Set up the ViewPager with the sections adapter.
        jsubViewPager = findViewById(R.id.jsub_container);
        jsubViewPager.setAdapter(jsubSectionsPagerAdapter);
        vsubViewPager = findViewById(R.id.vsub_container);
        vsubViewPager.setAdapter(vsubSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager.OnPageChangeListener onPageChangeListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                Log.d("Posi:", position + "/" + positionOffsetPixels + "/" + positionOffset + "/");
            }

            @Override
            public void onPageSelected(int position) {
//                Log.d("CurrentPosi:", position + "/");
                vsubViewPager.setCurrentItem(position);
                jsubViewPager.setCurrentItem(position);
                currentSection = position;
//                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                if (position == 1) {
                    fab.setImageResource(android.R.drawable.ic_input_add);
                } else {
                    fab.setImageResource(android.R.drawable.ic_search_category_default);
                }
            }
        };
        jsubViewPager.addOnPageChangeListener(onPageChangeListener);
        vsubViewPager.addOnPageChangeListener(onPageChangeListener);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(jsubViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vsubViewPager));
        vSub = findViewById(R.id.vsub);
        jSub = findViewById(R.id.jsub);

        vSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonBackGround(1);
            }
        });
        jSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonBackGround(0);
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSection != 1) {
                    searchView.setIconified(false);
                } else {
                    Snackbar.make(view, R.string.load_more, Snackbar.LENGTH_SHORT).show();
                    if (currentLanguageSubtitle == 0) {
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorDark)));
                        String script = "javascript: (function(){" +
                                "window.scrollTo(0,document.body.scrollHeight);" +
                                "setTimeout(processData, 1500);" +
                                Tool.processJsubDataScript +
                                "})()";
                        jsubWebView.loadUrl(script);
                    }
                }
            }
        });
        loadAnimeLonHome();
        loadAnimeVSubHome();
    }

    private void changeButtonBackGround(int button) {
        if (button == 0) {
            jSub.setBackground(getDrawable(R.drawable.left_button_ac));
            vSub.setBackground(getDrawable(R.drawable.right_button));
            jSub.setTextColor(getResources().getColor(R.color.white));
            vSub.setTextColor(getResources().getColor(R.color.colorPrimary));
            jsubViewPager.setVisibility(View.VISIBLE);
            vsubViewPager.setVisibility(View.GONE);
        } else {
            jSub.setBackground(getDrawable(R.drawable.left_button));
            vSub.setBackground(getDrawable(R.drawable.right_button_ac));
            jSub.setTextColor(getResources().getColor(R.color.colorPrimary));
            vSub.setTextColor(getResources().getColor(R.color.white));
            jsubViewPager.setVisibility(View.GONE);
            vsubViewPager.setVisibility(View.VISIBLE);
        }
        currentLanguageSubtitle = button;
    }

    void loadAnimeLonHome() {
        jsubWebView = new WebView(this);
        jsubWebView.setWebViewClient(new MyWebViewClient(jsubWebView, JSUB));
        jsubWebView.getSettings().setBlockNetworkImage(true);
        jsubWebView.getSettings().setLoadsImagesAutomatically(false);
        jsubWebView.getSettings().setJavaScriptEnabled(true);
        jsubWebView.addJavascriptInterface(new AndroidAPI(this), "Android");
        jsubWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        jsubWebView.loadUrl("https://animelon.com");
    }

    void loadAnimeVSubHome() {
        vsubWebView = new WebView(this);
        vsubWebView.setWebViewClient(new MyWebViewClient(vsubWebView, VSUB));
        vsubWebView.getSettings().setBlockNetworkImage(true);
        vsubWebView.getSettings().setLoadsImagesAutomatically(false);
        vsubWebView.getSettings().setJavaScriptEnabled(true);
        vsubWebView.addJavascriptInterface(new AndroidAPI(this), "Android");
        vsubWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        vsubWebView.loadUrl("http://animevsub.tv");
    }

    public FloatingActionButton getFloatButton() {
        return fab;
    }

    public void setLoadedPage(SUBTITLE subtitle) {
        jSubLoaded = subtitle.equals(JSUB) || jSubLoaded;
        vSubLoaded = subtitle.equals(VSUB) || vSubLoaded;
        Log.i("LoadedPage:", "jSubLoaded:"+ jSubLoaded + "|vSubLoaded:" + vSubLoaded);
    }

    public boolean isContentLoadedComplete() {
        return jSubLoaded && vSubLoaded;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, TIME_TO_BACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "Tét", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                return true;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public JsubContentFragment[] getJsubContentFragments() {
        return jsubContentFragments;
    }

    public List<AnimeCardData> getTotalJsubAnimeList() {
        return totalJsubAnimeList;
    }

    public List<AnimeCardData> getTotalVsubAnimeList() {
        return totalVsubAnimeList;
    }

    public VsubContentFragment[] getVsubContentFragments() {
        return vsubContentFragments;
    }

    public int getCurrentSection() {
        return currentSection;
    }

    public DialogSet getDialogSet() {
        return dialogSet;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SUBTITLE sub;

        private SectionsPagerAdapter(FragmentManager fm, SUBTITLE sub) {
            super(fm);
            this.sub = sub;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            Toast.makeText(MainActivity.this, "Main:" + position, Toast.LENGTH_SHORT).show();
            if (sub == JSUB) {
                JsubContentFragment contentFragment = JsubContentFragment.newInstance(position + 1);
                jsubContentFragments[position] = contentFragment;
                return contentFragment;
            } else {
                VsubContentFragment contentFragment = VsubContentFragment.newInstance(position + 1);
                vsubContentFragments[position] = contentFragment;
                return contentFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

    }
}
