package com.tomgehrke.geekguardiannews;

import android.graphics.Bitmap;

import java.util.Date;

public class Article {

    private String mTitle = "";
    private String mSectionName = "";
    private Date mPublicationDate;
    private String mUrl = "";
    private String mByLine = "";
    private Bitmap mThumbnail;

    public Article(String title, String sectionName, Date publicationDate, String url, String byLine, Bitmap thumbnail) {
        if (title != null) {
            this.mTitle = title;
        }

        if (sectionName != null) {
            this.mSectionName = sectionName;
        }

        this.mPublicationDate = publicationDate;

        if (url != null) {
            this.mUrl = url;
        }

        if (byLine != null) {
            this.mByLine = byLine;
        }

        this.mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public Date getPublicationDate() {
        return mPublicationDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getByLine() {
        return mByLine;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

}
