package com.hajaulee.mobileanime;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    private WebView webView;
    SUBTITLE subtitle;

    MyWebViewClient(WebView webView, SUBTITLE subtitle) {
        this.webView = webView;
        this.subtitle = subtitle;
    }


    // Khi bạn click vào link bên trong trình duyệt (Webview)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("MyLog", "Click on any interlink on webview that time you got url :-" + url);
        return super.shouldOverrideUrlLoading(view, url);
    }


    // Khi trang bắt đầu được tải
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.i("MyLog", "Your current url when webpage loading.." + url);
    }


    // Khi trang tải xong
    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i("MyLog", "Your current url when webpage loading.. finish" + (subtitle == SUBTITLE.JSUB?" -JSUB- ":" -VSUB- ") +  url);
        super.onPageFinished(view, url);
        if (subtitle == SUBTITLE.JSUB) {
            String script = "javascript: (function(){" +
                    "var inter = setInterval(waitMovie, 100);" +
                    "function waitMovie(){" +
                    "       Android.sendLog('Waiting...');" +
                    "var len = document.querySelectorAll('a.mini-previews').length;" +
                    "   if(len >= 8){" +
                    "       if(len >= 24)" +
                    "           clearInterval(inter);" +
                    "       Android.sendLog('Wait complete');" +
                    "       processData();" +
                    "   }" +
                    "}" +
                    Tool.processJsubDataScript +
                    "})()";
            webView.loadUrl(script);
        }else if(subtitle == SUBTITLE.VSUB){
            String script = "javascript: (function(){" +
                    "var inter1 = setInterval(waitMovie1, 100);" +
                    "var inter2 = setInterval(waitMovie2, 100);" +
                    "var inter3 = setInterval(waitMovie3, 100);" +
                    "var anime1 = [], anime2 = [], anime3 = [];" +
                    "   function waitMovie1(){" +
                    "       var movies = document.querySelectorAll('.MovieListTop .TPostMv');" +
                    "       var len = movies.length;" +
                    "       if(len > 8){" +
                    "           clearInterval(inter1);" +
                    "           Android.sendLog('Get .MovieListTop .TPostMv');" +
                    "           anime1 = processData(movies);" +
                    "           Android.sendVsubArray(anime1.concat(anime2).concat(anime3),0);" +
                    "       }" +
                    "   }" +
                    "   function waitMovie2(){" +
                    "       var movies = document.querySelectorAll('#single-home .TPostMv');" +
                    "       var len = movies.length;" +
                    "       if(len > 8){" +
                    "           clearInterval(inter2);" +
                    "           Android.sendLog('Get #single-home .TPostMv');" +
                    "           anime2 = processData(movies);" +
                    "           Android.sendVsubArray(anime1.concat(anime2).concat(anime3),0);" +
                    "       }" +
                    "   }" +
                    "   function waitMovie3(){" +
                    "       var movies =  document.querySelectorAll('#hot-home .TPostMv');" +
                    "       var len = movies.length;" +
                    "       if(len > 8){" +
                    "           clearInterval(inter3);" +
                    "           Android.sendLog('Get #hot-home .TPostMv');" +
                    "           anime3 = processData(movies);" +
                    "           Android.sendVsubArray(anime1.concat(anime2).concat(anime3),0);" +
                    "       }" +
                    "   }" +
                    Tool.processVsubDataScript +
                    "})()";
            webView.loadUrl(script);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

}