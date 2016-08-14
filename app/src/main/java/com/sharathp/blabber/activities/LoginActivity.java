package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityLoginBinding;
import com.sharathp.blabber.repositories.rest.TwitterClient;

import javax.inject.Inject;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
    private static String TAG = LoginActivity.class.getSimpleName();

    @Inject
    TwitterClient mTwitterClient;

    private ActivityLoginBinding mBinding;

    public static Intent createIntent(final Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        BlabberApplication.from(this).getComponent().inject(this);
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        mBinding.pbLoading.setVisibility(View.VISIBLE);
        launchHomeActivityAfterLogin();
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(final Exception e) {
        e.printStackTrace();
    }

    private void launchHomeActivityAfterLogin() {
        mTwitterClient.setAccessToken(getClient().getAccessToken());
        final Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
        Toast.makeText(LoginActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(final View view) {
        getClient().connect();
    }
}
