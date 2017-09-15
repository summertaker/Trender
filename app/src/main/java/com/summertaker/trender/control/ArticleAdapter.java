package com.summertaker.trender.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.trender.R;
import com.summertaker.trender.model.Article;
import com.summertaker.trender.util.ProportionalImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Article> articles;
    private ArticleInterface mCallback;

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    public ArticleAdapter(Context context, ArrayList<Article> Articles, ArticleInterface articleInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.articles = Articles;
        this.mCallback = articleInterface;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (272 * density);
        int margin = (int) (1 * density);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParams.setMargins(0, margin, 0, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Article article = articles.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);

            holder.loPicture = (LinearLayout) convertView.findViewById(R.id.loPicture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (article.getThumbnails() == null || article.getThumbnails().size() == 0) {
            holder.loPicture.setVisibility(View.GONE);
        } else {
            holder.loPicture.removeAllViews();
            holder.loPicture.setVisibility(View.VISIBLE);

            for (int i = 0; i < article.getThumbnails().size(); i++) {
                //Log.e(TAG, "url[" + i + "]: " + imageArray[i]);
                final String thumbnailUrl = article.getThumbnails().get(i);
                if (thumbnailUrl.isEmpty()) {
                    continue;
                }

                final ProportionalImageView iv = new ProportionalImageView(mContext);
                //if (i == imageArray.length - 1) {
                if (i == 0) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                //iv.setAdjustViewBounds(true);
                //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.loPicture.addView(iv);

                //int placeholder = R.drawable.placeholder_green;
                //if (thumbnailUrl.contains("nogizaka46")) {
                //    placeholder = R.drawable.placeholder_purple;
                //}

                Picasso.with(mContext).load(thumbnailUrl).into(iv);
                /*Picasso.with(mContext).load(url).into(iv, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loPicture.addView(iv);
                    }

                    @Override
                    public void onError() {

                    }
                });*/

                //final String imageUrl = article.getImageUrls().get(i);
                /*iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.e(">>", imageUrl);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }
                });*/
                //iv.setOnClickListener(new View.OnClickListener() {
                //    @Override
                //    public void onClick(View v) {
                //        mCallback.onImageClick(position, imageUrl);
                //    }
                //});
            }
        }

        // 제목
        String title = article.getTitle();
        //Log.e(TAG, "title: " + title);
        if (title == null || title.isEmpty()) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            title = title.replace("  ", " ");
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(title);
        }

        // 날짜
        String pubDate = article.getDate();
        if (pubDate == null || pubDate.isEmpty()) {
            holder.tvDate.setVisibility(View.GONE);
        } else {
            //Log.e(TAG, "date: " + date);
            holder.tvDate.setVisibility(View.VISIBLE);

            Date date = null;
            try {
                DateFormat sdf = null;
                if (pubDate.contains("+")) {
                    sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("/")) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("-")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                } else if (pubDate.contains(".") && pubDate.length() <= 10) {
                    sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDate.setText(pubDate);
        }

        // 내용
        String content = article.getContent();
        if (content == null || content.isEmpty()) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(content);
        }
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onContentClick(position);
            }
        });
        /*final String url = Article.getUrl();
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });*/

        return convertView;
    }

    static class ViewHolder {
        LinearLayout loPicture;
        TextView tvTitle;
        TextView tvDate;
        TextView tvContent;
    }
}
