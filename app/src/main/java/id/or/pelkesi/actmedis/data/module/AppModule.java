package id.or.pelkesi.actmedis.data.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    Application application;

    public AppModule(Application mApplication) {
        this.application = mApplication;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }
}
