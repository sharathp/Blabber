package com.sharathp.blabber.di;

import com.sharathp.blabber.di.modules.ApplicationModule;
import com.sharathp.blabber.di.modules.DatabaseModule;

import dagger.Component;

@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

}
