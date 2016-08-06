package com.sharathp.blabber.di;

import com.sharathp.blabber.di.modules.ApplicationModule;
import com.sharathp.blabber.di.modules.DatabaseModule;
import com.sharathp.blabber.di.modules.RestModule;
import com.sharathp.blabber.fragments.TimelineFragment;
import com.sharathp.blabber.service.UpdateTimelineService;

import dagger.Component;

@Component(modules = {ApplicationModule.class, DatabaseModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(UpdateTimelineService updateTimelineService);

    void inject(TimelineFragment timelineFragment);
}
