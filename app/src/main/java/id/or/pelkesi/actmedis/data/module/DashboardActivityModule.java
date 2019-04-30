package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.dashboard.DashboardActivityInterface;

@Module
public class DashboardActivityModule {
    private final DashboardActivityInterface.View mView;
    private final Context mContext;

    public DashboardActivityModule(DashboardActivityInterface.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Provides
    @CustomScope
    DashboardActivityInterface.View providesDashboardActivityInterfaceView() {
        return mView;
    }

    @Provides
    @CustomScope
    Context providesDashboardActivityContext() {
        return mContext;
    }
}
