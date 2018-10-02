package com.hajaulee.mobileanime;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AndroidAPI {
    private final static String TAG = "AndroidAPI";

    private MainActivity mainActivity;
    private DetailActivity detailActivity;
    private AnimeCardData animeData;

    public AndroidAPI(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    AndroidAPI(DetailActivity detailActivity, AnimeCardData animeData) {
        this.detailActivity = detailActivity;
        this.animeData = animeData;
    }

    @JavascriptInterface
    public void sendLog(String s) {
        Log.i(TAG, s);
    }

    private void handleAnimeList(List<AnimeCardData> list, String[] s, final int loadedSection) {
        list.clear();
        Log.d(TAG, "Loaded:Vsub" + loadedSection);
        for (String animeInfo : s) {
            String[] info = animeInfo.split(Tool.SEPARATOR);
            Bitmap image = Tool.bitmapFromUrl(mainActivity, info[1], TAG);
            if (image == null)
                Log.e(TAG, "NullImageError:" + Arrays.toString(info));
            Log.d(TAG, "Anime name: " + info[2]);
            list.add(new AnimeCardData(animeInfo, 3));
        }
    }

    private void refreshJsubUI(final int loadedSection) {
        final List<AnimeCardData> mList = mainActivity.getTotalJsubAnimeList();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JsubContentFragment[] fragments = mainActivity.getJsubContentFragments();
//                Toast.makeText(mainActivity, "Loaded: " + (loadedSection + 1) + " category.", Toast.LENGTH_SHORT).show();

                if (loadedSection >= 0 && fragments[0] != null) {
                    fragments[0].mAnimeCards.clear();
                    fragments[0].mAnimeCards.addAll(mList.subList(0, 8));
                    fragments[0].myAdapter.notifyDataSetChanged();
                }
                if (loadedSection >= 1 && fragments[1] != null) {
                    fragments[1].mAnimeCards.clear();
                    fragments[1].mAnimeCards.addAll(mList.subList(16, mList.size()));
                    fragments[1].myAdapter.notifyDataSetChanged();
                }
                if (loadedSection >= 2 && fragments[2] != null) {
                    fragments[2].mAnimeCards.clear();
                    fragments[2].mAnimeCards.addAll(mList.subList(8, 16));
                    fragments[2].myAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void refreshVsubUI(final int loadedSection) {
        final List<AnimeCardData> mList = mainActivity.getTotalVsubAnimeList();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VsubContentFragment[] fragments = mainActivity.getVsubContentFragments();
//                Toast.makeText(mainActivity, "Loaded: " + (loadedSection + 1) + " category.", Toast.LENGTH_SHORT).show();

                if (loadedSection >= 0 && fragments[0] != null) {
                    fragments[0].mAnimeCards.clear();
                    fragments[0].mAnimeCards.addAll(mList.subList(0, 10));
                    fragments[0].myAdapter.notifyDataSetChanged();
                }
                if (loadedSection >= 1 && fragments[1] != null) {
                    fragments[1].mAnimeCards.clear();
                    fragments[1].mAnimeCards.addAll(mList.subList(20, mList.size()));
                    fragments[1].myAdapter.notifyDataSetChanged();
                }
                if (loadedSection >= 2 && fragments[2] != null) {
                    fragments[2].mAnimeCards.clear();
                    fragments[2].mAnimeCards.addAll(mList.subList(10, 20));
                    fragments[2].myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void handleAfterLoadContent(SUBTITLE subtitle) {
        mainActivity.isLoadingMore = false;
        mainActivity.setLoadedPage(subtitle);
        Tool.firstSave(mainActivity, subtitle);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getFloatButton().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainActivity.getBaseContext(), R.color.colorPrimary)));
                if (mainActivity.isContentLoadedComplete()) {
                    Snackbar.make(mainActivity.getFloatButton(), R.string.refresh_success, Snackbar.LENGTH_SHORT).show();
                    mainActivity.getDialogSet().hideHelloDialog();
                }
            }
        });
    }

    public void refreshUI() {
        refreshVsubUI(2);
        refreshJsubUI(2);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainActivity.getDialogSet() != null)
                            mainActivity.getDialogSet().hideHelloDialog();
                    }
                });
            }
        }, 2000);
    }

    @JavascriptInterface
    public void sendVsubArray(String[] s, final int loadedSection) {
        handleAnimeList(mainActivity.getTotalVsubAnimeList(), s, loadedSection);
        refreshVsubUI(loadedSection);
        handleAfterLoadContent(SUBTITLE.VSUB);
    }

    @JavascriptInterface
    public void sendJsubArray(String[] s, final int loadedSection) {
        handleAnimeList(mainActivity.getTotalJsubAnimeList(), s, loadedSection);
        refreshJsubUI(loadedSection);
        handleAfterLoadContent(SUBTITLE.JSUB);
    }

    @JavascriptInterface
    public void setEpisodeList(String[] s) {
        animeData.getEpisodeList().get(0).clear();
        Log.i(TAG, Arrays.toString(s));
        for (String value : s) {
            String[] info = value.split(Tool.SEPARATOR);
            Log.i(TAG, Arrays.toString(info));
            if (info.length != 3)
                continue;
            Tool.bitmapFromUrl(detailActivity, info[1], TAG);
            animeData.getEpisodeList().get(0).add(AnimeCardData.createEpisodeInfo(info[0], info[1], info[2]));
        }
        Log.i(TAG, "Episode size: " + s.length);
        detailActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                detailActivity.setupEpisodeList();
            }
        });
    }

    @JavascriptInterface
    public void setDescription(String s) {
        animeData.setAnimeCardDescription(s);
        detailActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                detailActivity.getmDescription().setText(animeData.getAnimeCardDescription());
            }
        });
    }

    @JavascriptInterface
    public void sendMoreVsubArray(String[] s) {
        List<AnimeCardData> list = mainActivity.getTotalVsubAnimeList();
        if (list.size() == 30) {
            List<AnimeCardData> subList = new ArrayList<>(list.subList(20, list.size()));
            list.removeAll(subList);
        }
        Log.d(TAG, "AnimeVsubSize:" + list.size());
        for (String animeInfo : s) {
            String[] info = animeInfo.split(Tool.SEPARATOR);
            Tool.bitmapFromUrl(mainActivity, info[1], TAG);
            Log.d(TAG, "Anime name: " + info[2]);
            list.add(new AnimeCardData(animeInfo, 3));
        }
        refreshVsubUI(2);
        handleAfterLoadContent(SUBTITLE.VSUB);
    }
}
