package com.hajaulee.mobileanime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class Tool {
    public static final String TAG = "Tool";
    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_CACHE.cache";
    public static final String LOADED_VSUB_ANIME = "LOADED_VSUB_ANIME.cache";
    public static final String LOADED_JSUB_ANIME = "LOADED_JSUB_ANIME.cache";
    public static final String SEPARATOR = ">>><<<";
    public static final int PORTRAIT_COL_COUNT = 3;
    public static final int LANDSCAPE_COL_COUNT = 4;
    static private boolean debug = false;

    public static RecyclerView.OnScrollListener getScrollListener(final MainActivity activity) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == SCROLL_STATE_IDLE) {
                    if (activity.getCurrentSection() == 1 && !activity.isLoadingMore) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.getFloatButton().performClick();
                                activity.isLoadingMore = true;
                            }
                        });
                    }
                }
            }
        };
    }

    public static final String processJsubDataScript = "function processData(){" +
            "   var movies = document.querySelectorAll('a.mini-previews');" +
            "   Android.sendLog(movies.length +'-length');" +
            "   var animeList = [];" +
            "   for(var i = 0; i < movies.length; i++){" +
            "                  animeList.push(movies[i].href + '" + SEPARATOR + "' + " +
            "                  movies[i].firstElementChild.firstElementChild.children[0].src + '" + SEPARATOR + "' +" +
            "                  movies[i].firstElementChild.firstElementChild.children[1].innerText);" +
            "   }" +
            "   if(movies.length >= 24)" +
            "       Android.sendJsubArray(animeList, 2);" +
            "   else if(movies.length >= 16)" +
            "       Android.sendJsubArray(animeList, 1);" +
            "   else if(movies.length >= 8)" +
            "       Android.sendJsubArray(animeList, 0);" +
            "}";
    public static final String processVsubDataScript = "function processData(movies){" +
            "   var animeList = [];" +
            "   for(var i = 0; i < movies.length; i++){" +
            "       animeList.push(movies[i].querySelector('a').href + '" + SEPARATOR + "' + " +
            "       movies[i].querySelector('img').src + '" + SEPARATOR + "' + " +
            "       movies[i].querySelector('.Title').innerHTML);" +
            "   }" +
            "   return animeList;" +
            "}";

    public static void saveObject(Context context, String fname, Object content) {
        File file = new File(context.getApplicationInfo().dataDir + File.separator + fname);
        try {
            ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(file));
            oss.writeObject(content);
            oss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void getLoadedBitmap(final Context context) {
        Map<String, byte[]> map = (ConcurrentHashMap<String, byte[]>) Tool.loadSavedObject(context, Tool.LOADED_IMAGE_PATH);
        if (map != null) {
            for (Map.Entry<String, byte[]> i : map.entrySet()) {
                Bitmap value = BitmapFactory.decodeByteArray(i.getValue(), 0, i.getValue().length);
                if (debug)
                    Log.d(TAG, "Load " + (value == null ? "failed :" : "success :") + i.getKey());
                if (value != null)
                    AnimeCardData.LOADED_BITMAP.put(i.getKey(), value);
            }
        }
    }

    public static void saveLoadedAnime(final MainActivity mainActivity) {
        saveObject(mainActivity, LOADED_JSUB_ANIME, mainActivity.getTotalJsubAnimeList());
        saveObject(mainActivity, LOADED_VSUB_ANIME, mainActivity.getTotalVsubAnimeList());
    }

    @SuppressWarnings("unchecked")
    public static void getLoadedAnime(final MainActivity mainActivity) {
        List<AnimeCardData> jsubCardDataList = (ArrayList<AnimeCardData>) loadSavedObject(mainActivity, LOADED_JSUB_ANIME);
        List<AnimeCardData> vsubCardDataList = (ArrayList<AnimeCardData>) loadSavedObject(mainActivity, LOADED_VSUB_ANIME);
        if (jsubCardDataList != null)
            mainActivity.getTotalJsubAnimeList().addAll(jsubCardDataList);
        if (vsubCardDataList != null)
            mainActivity.getTotalVsubAnimeList().addAll(vsubCardDataList);
        AndroidAPI androidAPI = new AndroidAPI(mainActivity);
        androidAPI.refreshUI();
    }

    public static void saveLoadedBitmap(final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, byte[]> map = new ConcurrentHashMap<>();
                if (debug) Log.i("Before save:", "Size:" + AnimeCardData.LOADED_BITMAP.size());
                Iterator<Map.Entry<String, Bitmap>> animeList = AnimeCardData.LOADED_BITMAP.entrySet().iterator();
                while (animeList.hasNext()) {
                    Map.Entry<String, Bitmap> entry = animeList.next();
                    String key = entry.getKey();
                    Bitmap value = entry.getValue();
                    if (value != null) {
                        if (debug)
                            Log.d("BITMAP:success-", key + ": in Tool.java saveLoadedBitmap");
                        map.put(key, Tool.bitmapToBytes(value));
                    } else {
                        if (debug)
                            Log.d("BITMAP:failed-", key + ": NUll in Tool.java saveLoadedBitmap");
                        animeList.remove();
                    }
                }
                if (debug) Log.i("After save:", "Size:" + map.size());
                Tool.saveObject(context, Tool.LOADED_IMAGE_PATH, map);
            }

        });
    }

    public static Object loadSavedObject(Context context, String fname) {
        File file = new File(context.getApplicationInfo().dataDir, File.separator + fname);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object x = ois.readObject();
            ois.close();
            return x;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmapToBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static byte[] streamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    static public Bitmap bitmapFromUrl(String url, String TAG) {
        if (AnimeCardData.LOADED_BITMAP.containsKey(url) && AnimeCardData.LOADED_BITMAP.get(url) != null) {
            if (debug) Log.d(TAG, "Loaded from cache: " + url);
            return AnimeCardData.LOADED_BITMAP.get(url);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");

            connection.connect();
            InputStream input = connection.getInputStream();
//            byte[] bytes = streamToByteArray(input);
//            int i = bytes.length;
//            if(debug)Log.d(TAG, "Bitmap: " + i);
//            return bytes;
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
