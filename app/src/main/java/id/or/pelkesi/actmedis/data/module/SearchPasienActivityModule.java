package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.search.SearchPasienActivityInterface;

@Module
public class SearchPasienActivityModule {

    private final SearchPasienActivityInterface.View mView;
    private final Context mContext;

    public SearchPasienActivityModule(SearchPasienActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    SearchPasienActivityInterface.View providesSearchPasienActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesSearchPasienActivityContext() { return mContext; }

}
