package com.jtech.imaging.view.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.jtech.imaging.R;
import com.jtech.imaging.contract.LoadingContract;
import com.jtech.imaging.realm.OauthRealm;
import com.jtech.imaging.view.fragment.base.BaseFragment;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * 加载页视图
 * Created by jianghan on 2016/9/6.
 */
public class LoadingFragment extends BaseFragment<LoadingContract.Presenter> implements LoadingContract.View {

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.imageview_loading)
    ImageView imageView;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    public static LoadingFragment newInstance() {
        Bundle args = new Bundle();
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void init(Bundle bundle) {
        RxView.clicks(floatingActionButton)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new FabClick());
    }

    /**
     * fab点击事件
     */
    private class FabClick implements Action1<Void> {
        @Override
        public void call(Void aVoid) {
            if (OauthRealm.hasOauthModel()) {
                getPresenter().jumpToMainPage(getActivity().getSupportFragmentManager()
                        , floatingActionButton
                        , getString(R.string.fab));
            } else {
                getPresenter().jumpToOauthPage(getActivity().getSupportFragmentManager()
                        , floatingActionButton
                        , getString(R.string.fab));
            }
        }
    }
}