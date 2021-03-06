package com.jtech.imaging.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.jtech.imaging.R;
import com.jtech.imaging.cache.OauthCache;
import com.jtech.imaging.contract.WelcomeContract;
import com.jtech.imaging.model.OauthModel;
import com.jtech.imaging.presenter.WelcomePresenter;
import com.jtechlib.view.activity.BaseActivity;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 欢迎页，首屏
 * Created by jianghan on 2016/9/20.
 */
public class WelcomeActivity extends BaseActivity implements WelcomeContract.View {

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.imageview_welcome)
    ImageView imageView;

    private WelcomeContract.Presenter presenter;

    @Override
    protected void initVariables(Bundle bundle) {
        //绑定VP类
        presenter = new WelcomePresenter(this);
        //暂时使用已存在数据，不经过授权登陆
        if (!OauthCache.hasOauthModel()) {
            OauthModel oauthModel = new OauthModel();
            oauthModel.setAccessToken("da20b124d815a82ef0cb79226e991559e6e4c9cdf411fcef4e51acc718c0e44a");
            oauthModel.setCreatedAt(1473643598);
            oauthModel.setScope("public read_user write_user read_photos write_photos write_likes read_collections write_collections");
            oauthModel.setTokenType("bearer");
            OauthCache.get().setOauthModel(oauthModel);
        }
    }

    @Override
    protected void initViews(Bundle bundle) {
        setContentView(R.layout.activity_welcome);
        //设置fab的点击事件
        RxView.clicks(floatingActionButton)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new FabClick());
    }

    @Override
    protected void loadData() {

    }

    /**
     * fab点击事件
     */
    private class FabClick implements Action1<Void> {
        @Override
        public void call(Void aVoid) {
            if (OauthCache.hasOauthModel()) {
                //跳转到主页
                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                floatingActionButton, getString(R.string.fab));
                ActivityCompat.startActivity(getActivity(), new Intent(getActivity(),
                        MainActivity.class), activityOptionsCompat.toBundle());
            } else {
                //跳转到授权登陆页
                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                floatingActionButton, getString(R.string.fab));
                ActivityCompat.startActivity(getActivity(), new Intent(getActivity(),
                        OauthActivity.class), activityOptionsCompat.toBundle());
            }
            //一秒后关闭，等我想到更自然的方法再说把
            Observable.just(1000)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Integer, Object>() {
                        @Override
                        public Object call(Integer integer) {
                            try {
                                Thread.sleep(integer);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            keyBack();
                        }
                    });
        }
    }
}