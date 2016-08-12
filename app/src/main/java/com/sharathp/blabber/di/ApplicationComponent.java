package com.sharathp.blabber.di;

import com.sharathp.blabber.activities.HomeActivity;
import com.sharathp.blabber.activities.LoginActivity;
import com.sharathp.blabber.activities.TweetDetailActivity;
import com.sharathp.blabber.di.modules.ApplicationModule;
import com.sharathp.blabber.di.modules.DatabaseModule;
import com.sharathp.blabber.di.modules.RestModule;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.fragments.HomeTimelineFragment;
import com.sharathp.blabber.fragments.MentionsFragment;
import com.sharathp.blabber.fragments.UserTimelineFragment;
import com.sharathp.blabber.service.UpdateTimelineService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(UpdateTimelineService updateTimelineService);

    void inject(HomeTimelineFragment homeTimelineFragment);

    void inject(ComposeFragment composeFragment);

    void inject(HomeActivity tweetsActivity);

    void inject(TweetDetailActivity tweetDetailActivity);

    void inject(LoginActivity loginActivity);

    void inject(MentionsFragment mentionsFragment);

    void inject(UserTimelineFragment userTimelineFragment);
}
