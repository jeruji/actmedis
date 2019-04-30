package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.list.ListPasienActivityInterface;

@Module
public class ListPasienActivityModule {
    private final ListPasienActivityInterface.View mView;
    private final Context mContext;


    public ListPasienActivityModule(ListPasienActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    ListPasienActivityInterface.View providesListPasienActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesListPasienActivityContext() { return mContext; }
}
