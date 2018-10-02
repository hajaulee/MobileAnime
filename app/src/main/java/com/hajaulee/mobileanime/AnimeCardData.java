package com.hajaulee.mobileanime;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnimeCardData implements Serializable {
    private static final String TAG = "AnimeCardData";
    static final long serialVersionUID = 7597667082460298093L;
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


    public Bitmap getBitmapImage(Context context) {
        return Tool.getImageFromMapOrInternal(context, animeCardImageUrl);
    }

    public boolean isImageLoaded(Context context) {
        return getBitmapImage(context) == null;
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
