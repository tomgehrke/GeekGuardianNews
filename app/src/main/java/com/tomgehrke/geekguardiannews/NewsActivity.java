package com.tomgehrke.geekguardiannews;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {

    // Log tag to be used across the application
    public static final String LOG_TAG = NewsActivity.class.getName();

    // Constants
    private static final int GUARDIAN_BOOKS_LOADER_ID = 1;
    private static final String GUARDIAN_BOOKS_API_URL = "https://content.guardianapis.com/search?order-by=newest&page-size=50&section=science|technology|film&show-fields=byline,thumbnail&from-date=2017-01-01&api-key=20795c24-f7ab-45da-a7fb-7193a26d5f2f";

    // Objects to keep in memory
    private ArticleArrayAdapter mArticleArrayAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;
    private Boolean mFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Get the target ListVIew
        ListView articleListView = (ListView) findViewById(R.id.article_listview);

        // Set up the ArticleArrayAdapter
        mArticleArrayAdapter = new ArticleArrayAdapter(this, new ArrayList<Article>());

        // Set the ListView's adapter
        articleListView.setAdapter(mArticleArrayAdapter);

        // Get the TextView that is displayed when there is nothing in the list
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_textview);

        // Set the ListView's empty state view
        articleListView.setEmptyView(mEmptyStateTextView);

        // Get the loading indicator
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingIndicator.setVisibility(View.GONE);

        Button searchButton = (Button) findViewById(R.id.refresh_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArticleListView();
            }
        });

        // Set item listener
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article currentArticle = mArticleArrayAdapter.getItem(position);

                if (!currentArticle.getUrl().isEmpty()) {
                    try {
                        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.getUrl()));
                        if (navigationIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(navigationIntent);
                        }
                    } catch (NullPointerException e) {
                        Log.e(LOG_TAG, "Problem parsing article URL", e);
                    }

                }
            }
        });


        // Need to update the list view even if this is the first time through in order to
        // initialize the Loader. This allows it to survive configuration (e.g. orientation)
        // changes.
        updateArticleListView();
    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(this, GUARDIAN_BOOKS_API_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {

        // Change text of the empty state view to reflect that no articles were found
        mEmptyStateTextView.setText(getString(R.string.no_news));

        // Clear the adapter
        mArticleArrayAdapter.clear();

        // If there are articles to be shown, add them to the adapter
        if (articles != null && !articles.isEmpty()) {
            mArticleArrayAdapter.addAll(articles);
        }

        // All done so hide the progress bar
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        // Loader was reset so no need to keep data
        mArticleArrayAdapter.clear();
    }

    // Execute on Refresh button click
    private void updateArticleListView() {

        mArticleArrayAdapter.clear();

        // Check for network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) { // If there is a connection...

            // Inform user that we're searching and clear the list in preparation
            mEmptyStateTextView.setText("");
            mArticleArrayAdapter.clear();

            // Show progress indicator
            mLoadingIndicator.setVisibility(View.VISIBLE);

            // Get the LoaderManager and initialize it
            LoaderManager loaderManager = getLoaderManager();

            if (mFirstTime) {
                loaderManager.initLoader(GUARDIAN_BOOKS_LOADER_ID, null, this);
                mFirstTime = false;
            } else {
                loaderManager.restartLoader(GUARDIAN_BOOKS_LOADER_ID, null, this);
            }

        } else { // If there is no connection
            // Change text of the empty state view to reflect lack of network connectivity and clear the list
            mEmptyStateTextView.setText(getString(R.string.no_network));
            mArticleArrayAdapter.clear();
        }

    }
}
