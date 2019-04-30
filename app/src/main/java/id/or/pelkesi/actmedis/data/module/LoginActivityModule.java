package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.login.LoginActivityInterface;

@Module
public class LoginActivityModule {
    private final LoginActivityInterface.View mView;
    private final Context mContext;

    public LoginActivityModule(LoginActivityInterface.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Provides
    @CustomScope
    LoginActivityInterface.View providesLoginActivityInterfaceView() {
        return mView;
    }

    @Provides
    @CustomScope
    Context providesLoginActivityContext() {
        return mContext;
    }
}
