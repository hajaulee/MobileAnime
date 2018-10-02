package com.hajaulee.mobileanime;

import static com.hajaulee.mobileanime.Tool.SEPARATOR;

public class Javascript {
    public static String jSubLoadDescriptionAndEpisodeScript = "javascript: (function(){" +
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
            "           '" + SEPARATOR + "'+" +
            "           epList[i].querySelector('img').src+" +
            "           '" + SEPARATOR + "'+" +
            "           epList[i].querySelector('h5').innerHTML);" +
            "       }" +
            "       Android.sendLog('Complete waitEpisode...');" +
            "       Android.setEpisodeList(epListData);" +
            "       clearInterval(inter1);" +
            "   }" +
            "}" +
            "})()";

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
    public static final String jsubAutoPlayScript = "javascript: (function(){" +
            "var inter = setInterval(waitVideo, 100);" +
            "function waitVideo(){" +
            "   Android.sendLog('Waiting waitVideo...');" +
            "   var video = document.querySelector('video');" +
            "   if(video.readyState === 4){" +
            "       video.autoplay = true;" +
            "       video.load();" +
            "       video.play();" +
            "       clearInterval(inter);" +
            "   }" +
            "}" +
            "})()";;
}
