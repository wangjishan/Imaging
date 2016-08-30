package com.jtech.imaging.view.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.jtech.imaging.net.RequestManager;
import com.jtech.imaging.net.call.JCall;
import com.jtech.imaging.presenter.base.BasePresenter;
import com.jtech.imaging.util.ActivityUtils;
import com.jtech.imaging.util.ToastUtils;
import com.jtech.imaging.view.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * activity基类
 * Created by wuxubaiyang on 16/4/16.
 */
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    private RequestManager requestManager;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        //绑定注解
        ButterKnife.bind(getActivity());
        //将当前activity添加到activity管理中
        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 获取activity对象
     *
     * @return
     */
    public BaseActivity getActivity() {
        return this;
    }

    /**
     * 获取传递进来的参数
     *
     * @return
     */
    public Bundle getBundle() {
        Intent intent = getIntent();
        if (null != intent) {
            return intent.getExtras();
        }
        return null;
    }

    /**
     * 添加视图
     *
     * @param presenter
     * @param resId
     * @param <T>
     */
    public <T extends BasePresenter<?>> void addFragmentToActivity(T presenter, int resId) {
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), presenter.getViewImplAsFragment(), resId);
    }

    /**
     * 实例化一个fragment对象
     *
     * @param classType
     * @param <R>
     * @return
     */
    public <R extends BaseFragment> R newFragmentInstance(Class<R> classType) {
        return ActivityUtils.newFragmentInstance(classType, getBundle());
    }

    /**
     * 添加一堆视图
     *
     * @param presenters
     * @param resId
     * @param <T>
     */
    public <T extends BasePresenter<?>> void addFragmentListToActivity(List<T> presenters, int resId) {
        List<Fragment> fragments = new ArrayList<>();
        for (BasePresenter presenter : presenters) {
            fragments.add(presenter.getViewImplAsFragment());
        }
        ActivityUtils.addFragmentListToActivity(getSupportFragmentManager(), fragments, resId);
    }

    /**
     * 权限检查
     */
    public void checkPermission(String[] permissions, PermissionsResultAction permissionsResultAction) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), permissions, permissionsResultAction);
    }

    /**
     * 显示toast
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showShort(getActivity(), message);
    }

    /**
     * 获取请求管理对象
     *
     * @return
     */
    public RequestManager getRequestManager() {
        if (null == requestManager) {
            //实例化请求管理对象
            requestManager = new RequestManager();
        }
        return requestManager;
    }

    /**
     * 添加一个请求
     *
     * @param jCall
     * @return
     */
    public RequestManager addCall(JCall jCall) {
        return getRequestManager().addCall(jCall);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除当前activity对象
        ActivityManager.getInstance().removeActivity(this);
        //取消全部请求
        if (null != requestManager) {
            requestManager.clearAllCall();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消全部请求
        if (null != requestManager) {
            requestManager.clearAllCall();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean onKey = true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                keyBack();
                break;
            default:
                onKey = super.onKeyDown(keyCode, event);
                break;
        }
        return onKey;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //处理权限请求回调
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    /**
     * 响应后退按键
     */
    public void keyBack() {
        finish();
    }
}