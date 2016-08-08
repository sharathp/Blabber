package com.sharathp.blabber.di;

import com.sharathp.blabber.activities.LoginActivity;
import com.sharathp.blabber.activities.TweetDetailActivity;
import com.sharathp.blabber.activities.TweetsActivity;
import com.sharathp.blabber.di.modules.ApplicationModule;
import com.sharathp.blabber.di.modules.DatabaseModule;
import com.sharathp.blabber.di.modules.RestModule;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.fragments.TimelineFragment;
import com.sharathp.blabber.service.UpdateTimelineService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(UpdateTimelineService updateTimelineService);

    void inject(TimelineFragment timelineFragment);

    void inject(ComposeFragment composeFragment);

    void inject(TweetsActivity tweetsActivity);

    void inject(TweetDetailActivity tweetDetailActivity);

    void inject(LoginActivity loginActivity);
}
