package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.about.AboutActivityInterface;

@Module
public class AboutActivityModule {

    private final AboutActivityInterface.View mView;
    private final Context mContext;

    public AboutActivityModule(AboutActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    AboutActivityInterface.View providesAboutActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesAboutActivityContext() { return mContext; }

}
