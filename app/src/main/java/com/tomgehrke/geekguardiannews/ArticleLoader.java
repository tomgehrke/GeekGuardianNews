package com.tomgehrke.geekguardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class ArticleLoader extends AsyncTaskLoader<ArrayList<Article>> {

    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        if (mUrl == null || mUrl.isEmpty()) {
            return null;
        }

        // Make the request, parse the response and pull a list of books
        return QueryUtils.fetchArticleData(mUrl);
    }
}
