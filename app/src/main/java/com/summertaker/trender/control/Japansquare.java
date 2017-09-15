package com.summertaker.trender.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.trender.R;
import com.summertaker.trender.common.BaseApplication;
import com.summertaker.trender.common.BaseFragment;
import com.summertaker.trender.common.Config;
import com.summertaker.trender.model.Article;
import com.summertaker.trender.parser.JapansquareParser;
import com.summertaker.trender.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Japansquare extends BaseFragment implements ArticleInterface {

    private Context mContext;
    private Activity mActivity;

    private ProgressBar mPbLoading;
    private LinearLayout mLoLoadMore;

    private ArrayList<Article> mArticles;
    private ArrayList<Article> mFetchArticles;
    private ArticleAdapter mAdapter;
    private ListView mListView;

    private int mCurrentPage = 1;
    private int mMaxPage = 15;
    private boolean mIsLoading = false;
    private int mDetailLoadCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_japansquare, container, false);

        View rootView = inflater.inflate(R.layout.fragment_japansquare, container, false);

        mContext = container.getContext();
        mActivity = (Activity) mContext;

        mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);

        mLoLoadMore = (LinearLayout) rootView.findViewById(R.id.loLoadMore);
        ProgressBar pbLoadMore = (ProgressBar) rootView.findViewById(R.id.pbLoadMore);

        mArticles = new ArrayList<>();

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new ArticleAdapter(mContext, mArticles, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onContentClick(position);
            }
        });
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //Log.e(TAG, "onLoadMore().page: " + page + " / " + mMaxPage);
                if (mMaxPage == 0 || mCurrentPage <= mMaxPage) {
                    loadData(null, true);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else {
                    return false;
                }
            }
        });

        loadData(null, true);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
    }

    private void loadData(String url, boolean isReal) {
        //if (mIsLoading) {
        //    return;
        //}
        //mIsLoading = true;

        if (!isReal) {
            parseData(url, "");
        } else {
            if (url == null) {
                url = "http://theqoo.net/index.php?mid=japan&filter_mode=normal&category=26063";
                if (mCurrentPage > 1) {
                    url += "?page=" + mCurrentPage;
                    mLoLoadMore.setVisibility(View.VISIBLE);
                }

            }
            final String fetchUrl = url;
            //Log.e(TAG, fetchUrl);

            StringRequest request = new StringRequest(Request.Method.GET, fetchUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.e(TAG, response);
                            parseData(fetchUrl, response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    //headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", Config.USER_AGENT_WEB);
                    return headers;
                }
            };

            // Adding request to request queue
            BaseApplication.getInstance().addToRequestQueue(request);
        }
    }

    private void parseData(String url, String response) {

        if (url.contains("document_srl=")) {
            // 상세 페이지
            JapansquareParser japansquareParser = new JapansquareParser();
            ArrayList<String> results = japansquareParser.parseDetail(response);
            mFetchArticles.get(mDetailLoadCount).setThumbnails(results);

            mDetailLoadCount++;
            if (mDetailLoadCount < mFetchArticles.size()) {
                Article article = mFetchArticles.get(mDetailLoadCount);
                loadData(article.getUrl(), article.isHasImage());
            } else {
                mArticles.addAll(mFetchArticles);
                renderData();
            }
        } else {
            // 목록 페이지
            mFetchArticles = new ArrayList<>();
            JapansquareParser japansquareParser = new JapansquareParser();
            japansquareParser.parseList(response, mFetchArticles);

            mArticles.addAll(mFetchArticles);
            renderData();

            //mDetailLoadCount = 0;
            //Article article = mFetchArticles.get(mDetailLoadCount);
            //loadData(article.getUrl(), article.isHasImage());
        }
    }

    private void renderData() {
        if (mCurrentPage == 1) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mLoLoadMore.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();

        //mBaseToolbar.setTitle(mTitle + " ( " + mCurrentPage + " / " + mMaxPage + " )");

        mIsLoading = false;
        mCurrentPage++;
    }

    @Override
    public void onImageClick(int position, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
            startActivityForResult(intent, 100);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onContentClick(int position) {
        Article article = (Article) mAdapter.getItem(position);
        String url = article.getUrl();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivityForResult(intent, 100);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
