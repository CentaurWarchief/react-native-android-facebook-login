package com.centaurwarchief.facebooklogin;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {
    private Promise mPromise;

    public void respondsToPromise(final Promise promise) {
        mPromise = promise;
    }

    public boolean isAwaitingToRespond() {
        return mPromise != null;
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        if (mPromise == null) {
            return;
        }

        WritableMap result = new WritableNativeMap();

        result.putBoolean("isCancelled", false);

        result.putString("tokenString", loginResult.getAccessToken().getToken());

        result.putArray(
            "declinedPermissions",
            FacebookLoginUtils.translatePermissionsSet(loginResult.getRecentlyDeniedPermissions())
        );

        result.putArray(
            "grantedPermissions",
            FacebookLoginUtils.translatePermissionsSet(loginResult.getRecentlyGrantedPermissions())
        );

        mPromise.resolve(result);
        mPromise = null;
    }

    @Override
    public void onCancel() {
        if (mPromise == null) {
            return;
        }

        WritableMap result = new WritableNativeMap();

        result.putBoolean("isCancelled", true);

        mPromise.resolve(result);
        mPromise = null;
    }

    @Override
    public void onError(FacebookException exception) {
        if (mPromise != null) {
            mPromise.reject(exception);
            mPromise = null;
        }
    }
}
