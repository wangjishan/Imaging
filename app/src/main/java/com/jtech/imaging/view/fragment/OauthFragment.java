package com.jtech.imaging.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jtech.imaging.R;
import com.jtech.imaging.contract.MainContract;
import com.jtech.imaging.contract.OauthContract;
import com.jtech.imaging.view.adapter.ScopesAdapter;
import com.jtech.imaging.view.fragment.base.BaseFragment;
import com.jtech.listener.OnItemClickListener;
import com.jtech.view.JRecyclerView;
import com.jtech.view.RecyclerHolder;

import butterknife.Bind;

/**
 * 授权认证fragment
 * Created by wuxubaiyang on 16/4/16.
 */
public class OauthFragment extends BaseFragment<OauthContract.Presenter> implements MainContract.View, OnItemClickListener {

    @Bind(R.id.jrecyclerview_oauth)
    JRecyclerView jRecyclerView;
    @Bind(R.id.webview_oauth)
    WebView webView;
    @Bind(R.id.contentloading_oauth)
    ContentLoadingProgressBar contentLoadingProgressBar;

    private ScopesAdapter scopesAdapter;

    /**
     * 创建视图的方法
     *
     * @param inflater
     * @param container
     * @return
     */
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_oauth, container, false);
    }

    /**
     * 入口方法
     *
     * @param bundle
     */
    @Override
    public void init(Bundle bundle) {
        //设置列表
        jRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        scopesAdapter = new ScopesAdapter(getActivity());
        jRecyclerView.setAdapter(scopesAdapter);
        jRecyclerView.setOnItemClickListener(this);
        //设置浏览器
        webView.setWebViewClient(new mWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new mWebChromeClient());
        //设置数据
        scopesAdapter.setDatas(getPresenter().getScopeList());
    }

    /**
     * 处理浏览器进度
     */
    private class mWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                contentLoadingProgressBar.hide();
            } else {
                contentLoadingProgressBar.show();
            }
        }
    }

    /**
     * 处理浏览器url
     */
    private class mWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            int codeIndex = url.lastIndexOf("code");
            if (codeIndex > 0) {
                //授权成功
                String code = url.substring(codeIndex);
                Log.d("", code);
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    @Override
    public void onItemClick(RecyclerHolder recyclerHolder, View view, int position) {
        if (position == scopesAdapter.getItemCount() - 1) {
            //隐藏授权列表，显示浏览器
            contentLoadingProgressBar.show();
            webView.setVisibility(View.VISIBLE);
            jRecyclerView.setVisibility(View.GONE);
            //得到授权认证的url
            String oauthUrl = getPresenter().getOauthUrl(scopesAdapter.getCheckedScope());
            webView.loadUrl(oauthUrl);
        }
    }
}