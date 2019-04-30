package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.add.AddPasienActivityInterface;

@Module
public class AddPasienActivityModule {

    private final AddPasienActivityInterface.View mView;
    private final Context mContext;

    public AddPasienActivityModule(AddPasienActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    AddPasienActivityInterface.View providesAddPasienActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesAddPasienActivityContext() { return mContext; }

}
