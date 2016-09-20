package com.jtech.imaging.net;

import com.jtech.imaging.common.Constants;
import com.jtech.imaging.model.OauthModel;
import com.jtech.imaging.realm.OauthRealm;
import com.jtechlib.net.BaseApi;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口的实现类
 * Created by wuxubaiyang on 16/4/17.
 */
public class API extends BaseApi {
    /**
     * 持有演示用api对象
     */
    private UnsplashApi unsplashApi;

    private static API api;

    public static API get() {
        if (null == api) {
            api = new API();
        }
        return api;
    }


    /**
     * 获取unsplash接口对象
     *
     * @return
     */
    public UnsplashApi.OauthApi oauthApi() {
        //创建retrofit
        return createRxApi(Constants.BASE_UNSPLASH_OAUTH_URL, UnsplashApi.OauthApi.class);
    }

    /**
     * 获取unsplash接口对象
     *
     * @return
     */
    public UnsplashApi unsplashApi() {
        if (null == unsplashApi) {
            //获取token
            String authToken = "";
            if (OauthRealm.hasOauthModel()) {
                OauthModel oauthModel = OauthRealm.getInstance().getOauthModel();
                authToken = oauthModel.getTokenType() + " " + oauthModel.getAccessToken();
            }
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", authToken);
            //创建retrofit
            unsplashApi = createRxApi(headerMap, Constants.BASE_UNSPLASH_URL, UnsplashApi.class);
        }
        return unsplashApi;
    }
}