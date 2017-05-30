package com.tomgehrke.geekguardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    private DateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a z", Locale.getDefault());

    public ArticleArrayAdapter(@NonNull Context context, @NonNull ArrayList<Article> articles) {
        super(context, 0, articles);
        //mDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ArticleItemViewHolder articleItemViewHolder;

        // Inflate our Article Item layout if there wasn't one already
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false);

            // Set up ArticleItemViewHolder
            articleItemViewHolder = new ArticleItemViewHolder();

            // Find all the views
            articleItemViewHolder.title = (TextView) convertView.findViewById(R.id.title_textview);
            articleItemViewHolder.publicationDate = (TextView) convertView.findViewById(R.id.publication_date_textview);
            articleItemViewHolder.byLine = (TextView) convertView.findViewById(R.id.byline_date_textview);
            articleItemViewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_imageview);
            articleItemViewHolder.sectionName = (TextView) convertView.findViewById(R.id.section_textview);

            // Store holder with the view
            convertView.setTag(articleItemViewHolder);
        } else {
            // Just use the saved ArticleItemViewHolder. No need to do all the findViewById stuff.
            articleItemViewHolder = (ArticleItemViewHolder) convertView.getTag();
        }

        // Get current article
        final Article currentArticle = getItem(position);

        // Find and update layout views (if we've got one)
        if (currentArticle != null) {
            articleItemViewHolder.title.setText(currentArticle.getTitle());
            articleItemViewHolder.publicationDate.setText(mDateFormat.format(currentArticle.getPublicationDate()));
            articleItemViewHolder.byLine.setText(currentArticle.getByLine());
            articleItemViewHolder.thumbnail.setImageBitmap(currentArticle.getThumbnail());
            articleItemViewHolder.sectionName.setText(currentArticle.getSectionName());
        }

        return convertView;
    }
}

class ArticleItemViewHolder {
    TextView title;
    TextView byLine;
    TextView publicationDate;
    ImageView thumbnail;
    TextView sectionName;
}
