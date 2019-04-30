package id.or.pelkesi.actmedis;

import android.app.Application;
import android.os.StrictMode;

import id.or.pelkesi.actmedis.data.component.DaggerNetComponent;
import id.or.pelkesi.actmedis.data.component.NetComponent;
import id.or.pelkesi.actmedis.data.module.AppModule;
import id.or.pelkesi.actmedis.data.module.NetModule;
import id.or.pelkesi.actmedis.util.MyPreferenceManager;


public class App extends Application {
    private static App mInstance;
    private NetComponent mNetComponent;
    private MyPreferenceManager pref;

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                    .netModule(new NetModule("gs://actmedis-admin.appspot.com/"))
                .build();
        mInstance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }


    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
}
