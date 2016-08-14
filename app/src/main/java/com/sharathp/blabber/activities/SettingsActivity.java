package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivitySettingsBinding;
import com.sharathp.blabber.repositories.LocalPreferencesDAO;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.rest.TwitterClient;

import javax.inject.Inject;

// for now this will suffice instead of PreferenceActivity & PreferenceFragment etc
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding mBinding;

    @Inject
    LocalPreferencesDAO mLocalPreferencesDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    TwitterDAO mTwitterDAO;

    public static Intent createIntent(final Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlabberApplication.from(this).getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle(R.string.title_settings);

        mBinding.tvAccountName.setText("@" + mLocalPreferencesDAO.getUserScreenName());
        mBinding.tvLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sign_out)
                    .setMessage(R.string.sign_out_warning)
                    .setNegativeButton(R.string.sign_out_cancel, (dialogInterface, i) -> {
                        // no-op
                    })
                    .setPositiveButton(R.string.sign_out_ok, (dialogInterface, i) -> {
                        logout();
                    })
                    .show();
        });
    }

    private void logout() {
        mTwitterClient.clearAccessToken();
        mLocalPreferencesDAO.clearAll();
        mTwitterDAO.deleteDatabase();
        startActivity(LoginActivity.createIntent(this));
        finish();
    }
}
