package com.hajaulee.mobileanime;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class Tool {
    public static final String TAG = "Tool";
    static public ConcurrentHashMap<String, Bitmap> LOADED_IMAGE = new ConcurrentHashMap<>();
    public static final String LOADED_IMAGE_PATH = "LOADED_IMAGE_CACHE";
    public static final String LOADED_VSUB_ANIME_PATH = "LOADED_VSUB_ANIME_PATH.cache";
    public static final String LOADED_JSUB_ANIME_PATH = "LOADED_JSUB_ANIME_PATH.cache";
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


    public static void saveObject(final Context context, final String fname, final Object content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(context.getApplicationInfo().dataDir + File.separator + fname);
                ObjectOutputStream oss = null;
                try {
                    synchronized (content) {
                        oss = new ObjectOutputStream(new FileOutputStream(file));
                        Log.e(TAG, "" + fname);
                        List<AnimeCardData> copyOf = null;
                        if (content instanceof List) {
                            copyOf = new ArrayList<AnimeCardData>((List) content);
                        }
                        oss.writeObject(copyOf == null ? content : copyOf);
                        Log.d(TAG, "Save Object: successfully>>>" + fname);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (oss != null) {
                            oss.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static Bitmap getImageFromMapOrInternal(Context context, String name) {
        Bitmap bmp = LOADED_IMAGE.get(name);
        if (bmp != null) {
            Log.d(TAG, "GET from map");
            return bmp;
        }
        return Tool.getBitmapFormInternal(context, name);
    }

//    @SuppressWarnings("unchecked")
//    public static void getLoadedBitmap(final Context context) {
//        Map<String, byte[]> map = (ConcurrentHashMap<String, byte[]>) Tool.loadSavedObject(context, Tool.LOADED_IMAGE_PATH);
//        if (map != null) {
//            for (Map.Entry<String, byte[]> i : map.entrySet()) {
//                Bitmap value = BitmapFactory.decodeByteArray(i.getValue(), 0, i.getValue().length);
//                if (debug)
//                    Log.d(TAG, "Load " + (value == null ? "failed :" : "success :") + i.getKey());
//                if (value != null)
//                    AnimeCardData.LOADED_BITMAP.put(i.getKey(), value);
//            }
//            System.gc();
//        }
//    }

    public static void saveLoadedAnime(final MainActivity mainActivity) {
        saveObject(mainActivity, LOADED_JSUB_ANIME_PATH, mainActivity.getTotalJsubAnimeList());
        saveObject(mainActivity, LOADED_VSUB_ANIME_PATH, mainActivity.getTotalVsubAnimeList());
    }

    public static void firstSave(final MainActivity mainActivity, SUBTITLE subtitle) {
        boolean jSubFileExist = new File(mainActivity.getApplicationInfo().dataDir + File.separator + LOADED_JSUB_ANIME_PATH).exists();
        boolean vSubFileExist = new File(mainActivity.getApplicationInfo().dataDir + File.separator + LOADED_VSUB_ANIME_PATH).exists();
        switch (subtitle) {
            case JSUB:
                if (!jSubFileExist)
                    saveObject(mainActivity, LOADED_JSUB_ANIME_PATH, mainActivity.getTotalJsubAnimeList());
                break;
            case VSUB:
                if (!vSubFileExist)
                    saveObject(mainActivity, LOADED_VSUB_ANIME_PATH, mainActivity.getTotalVsubAnimeList());
                break;
        }
    }

    public static void uiToast(final Activity context, final String s) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void getLoadedAnime(final MainActivity mainActivity) {
        final List<AnimeCardData> jsubCardDataList = (ArrayList<AnimeCardData>) loadSavedObject(mainActivity, LOADED_JSUB_ANIME_PATH);
        final List<AnimeCardData> vsubCardDataList = (ArrayList<AnimeCardData>) loadSavedObject(mainActivity, LOADED_VSUB_ANIME_PATH);
        if (jsubCardDataList != null) {
            uiToast(mainActivity, "jsubCardDataList:" + jsubCardDataList.size());
            mainActivity.getTotalJsubAnimeList().addAll(jsubCardDataList);
            for (AnimeCardData cardData : jsubCardDataList) {
                String imageLink = cardData.getAnimeCardImageUrl();
                Bitmap bmp = getBitmapFormInternal(mainActivity, imageLink);
                if (bmp != null && !LOADED_IMAGE.containsKey(imageLink))
                    LOADED_IMAGE.put(imageLink, bmp);
            }
        } else {
            uiToast(mainActivity, "jsubCardDataList:null");
        }

        if (vsubCardDataList != null) {
            uiToast(mainActivity, "vsubCardDataList:" + vsubCardDataList.size());
            mainActivity.getTotalVsubAnimeList().addAll(vsubCardDataList);
            for (AnimeCardData cardData : vsubCardDataList) {
                String imageLink = cardData.getAnimeCardImageUrl();
                Bitmap bmp = getBitmapFormInternal(mainActivity, imageLink);
                if (bmp != null && !LOADED_IMAGE.containsKey(imageLink))
                    LOADED_IMAGE.put(imageLink, bmp);
            }
        } else {
            uiToast(mainActivity, "vsubCardDataList:null");
        }
        if (jsubCardDataList != null && vsubCardDataList != null && jsubCardDataList.size() > 24 && vsubCardDataList.size() > 24) {
            AndroidAPI androidAPI = new AndroidAPI(mainActivity);
            androidAPI.refreshUI();
        }
    }

//    public static void saveLoadedBitmap(final Context context) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.gc();
//                Map<String, byte[]> map = new ConcurrentHashMap<>();
//                if (debug) Log.i("Before save:", "Size:" + AnimeCardData.LOADED_BITMAP.size());
//                Iterator<Map.Entry<String, Bitmap>> animeList = AnimeCardData.LOADED_BITMAP.entrySet().iterator();
//                while (animeList.hasNext()) {
//                    Map.Entry<String, Bitmap> entry = animeList.next();
//                    String key = entry.getKey();
//                    Bitmap value = entry.getValue();
//                    if (value != null) {
//                        if (debug)
//                            Log.d("BITMAP:success-", key + ": in Tool.java saveLoadedBitmap");
//                        map.put(key, Tool.bitmapToBytes(value));
//                    } else {
//                        if (debug)
//                            Log.d("BITMAP:failed-", key + ": NUll in Tool.java saveLoadedBitmap");
//                        animeList.remove();
//                    }
//                }
//                if (debug) Log.i("After save:", "Size:" + map.size());
//                Tool.saveObject(context, Tool.LOADED_IMAGE_PATH, map);
//            }
//
//        }).start();
//    }

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
        byte[] byteArray = new byte[0];
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byteArray = stream.toByteArray();
            bmp.recycle();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "Memory error");
        }
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
        buffer.close();
        return buffer.toByteArray();
    }

    public static Bitmap getBitmapFormInternal(Context context, String name) {
        try {
            FileInputStream inputStream = new FileInputStream(context.getApplicationInfo().dataDir +
                    File.separator +
                    LOADED_IMAGE_PATH +
                    File.separator +
                    hashName(name));
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            Log.d(TAG, "Got image:" + name + " by name " + hashName(name) + "from internal, " + (bmp != null));
            if (bmp != null)
                LOADED_IMAGE.put(name, bmp);
            return bmp;
        } catch (IOException e) {
            Log.d(TAG, "Got image: FileNotFoundException:" + name + " by name " + hashName(name));
            return null;
        }
    }

    private static void saveBitmapImageToInternalSync(final Context context, final String name, final Bitmap bitmap) {

        Log.d(TAG, "Try saving image:" + name + " by name " + hashName(name) + "to internal");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(context.getApplicationInfo().dataDir +
                    File.separator +
                    LOADED_IMAGE_PATH +
                    File.separator +
                    hashName(name));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
            Log.d(TAG, "Saved image:" + name + " by name " + hashName(name) + "to internal");
        } catch (IOException e) {
            Log.d(TAG, "FileNotOpen:" + hashName(name) + name);
            Log.d(TAG, "Try creating folder:");
            File folder = new File(context.getApplicationInfo().dataDir +
                    File.separator + LOADED_IMAGE_PATH);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                Log.d(TAG, "create folder successfully");
            } else {
                Log.d(TAG, "create folder failed");
            }
        }
    }

    public static void saveBitmapImageToInternal(final Context context, final String name, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveBitmapImageToInternal(context, name, bitmap);
            }
        }).start();
    }

    public static String hashName(String s) {
        long hash = 7;
        final long hahaha = 829167520594445022L;
        for (int i = 0; i < s.length(); i++) {
            hash = (hash * 31 + s.charAt(i)) % hahaha;
        }
        return String.valueOf(Math.abs(hash));
    }

    static public Bitmap bitmapFromUrl(Context context, String url, String TAG) {
        Bitmap bitmap = getImageFromMapOrInternal(context, url);
        if (bitmap != null)
            return bitmap;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            if (bitmap != null) {
                Log.d(TAG, "Get image from net successful!: " + url);
                if (!LOADED_IMAGE.containsKey(url))
                    LOADED_IMAGE.put(url, bitmap);
                saveBitmapImageToInternalSync(context, url, bitmap);
            } else
                Log.d(TAG, "Get image from net failed!: " + url);
            input.close();
            System.gc();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
