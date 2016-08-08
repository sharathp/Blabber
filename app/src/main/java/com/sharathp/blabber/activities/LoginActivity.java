package com.sharathp.blabber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.repositories.rest.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BlabberApplication.from(this).getComponent().inject(this);
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        final Intent i = new Intent(LoginActivity.this, TweetsActivity.class);
        startActivity(i);
        Toast.makeText(LoginActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(final Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(final View view) {
        getClient().connect();
    }
}
