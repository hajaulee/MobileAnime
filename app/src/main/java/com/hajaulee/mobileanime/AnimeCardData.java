package com.hajaulee.mobileanime;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimeCardData implements Serializable {
    public static Map<String, Bitmap> LOADED_BITMAP;
    private static final String TAG = "AnimeCardData";
    private String animeCardName;
    private String animeCardImageUrl;
    private String animeCardDescription;
    private int animeCardImage;
    private String animeCardLink;
    private List<List<EpisodeInfo>> EpisodeList = new ArrayList<>(); // List cấp 1 servers, list cấp 2 episodes

    private AnimeCardData() {
        EpisodeList.add(new ArrayList<EpisodeInfo>());
    }

    public AnimeCardData(String animeCardName, String animeCardDescription, int animeCardImage) {
        this();
        this.animeCardName = animeCardName;
        this.animeCardDescription = animeCardDescription;
        this.animeCardImage = animeCardImage;
    }

    public AnimeCardData(String animeCardName, String animeCardLink, String animeCardImageUrl) {
        this();
        this.animeCardName = animeCardName;
        this.animeCardLink = animeCardLink;
        this.animeCardImageUrl = animeCardImageUrl;
    }

    AnimeCardData(String dataString, int num) {
        this();
        if (num == 3) {
            String[] info = dataString.split(Tool.SEPARATOR);
            this.animeCardName = info[2];
            this.animeCardLink = info[0];
            this.animeCardImageUrl = info[1];
            this.animeCardImage = R.drawable.series97;
        }
    }

    public String getAnimeCardName() {
        return animeCardName;
    }

    public Bitmap getBitmapImage() {
        return LOADED_BITMAP.get(this.animeCardImageUrl);
    }

    public boolean isImageLoaded() {
        return LOADED_BITMAP.containsKey(this.animeCardImageUrl);
    }

    public String getAnimeCardDescription() {
        return animeCardDescription;
    }

    public int getAnimeCardImage() {
        return animeCardImage;
    }


    public String getAnimeCardLink() {
        return animeCardLink;
    }

    public void setAnimeCardLink(String animeCardLink) {
        this.animeCardLink = animeCardLink;
    }

    public String getAnimeCardImageUrl() {
        return animeCardImageUrl;
    }

    public void setAnimeCardImageUrl(String animeCardImageUrl) {
        this.animeCardImageUrl = animeCardImageUrl;
    }

    public void setAnimeCardDescription(String animeCardDescription) {
        this.animeCardDescription = animeCardDescription;
    }

    public List<List<EpisodeInfo>> getEpisodeList() {
        return EpisodeList;
    }

    public static EpisodeInfo createEpisodeInfo(String epiLink, String epiImage, String epiName) {
        return new EpisodeInfo(epiLink, epiImage, epiName);
    }
}
