package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.search.SearchResultActivityInterface;

@Module
public class SearchResultActivityModule {

    private final SearchResultActivityInterface.View mView;
    private final Context mContext;

    public SearchResultActivityModule(SearchResultActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    SearchResultActivityInterface.View providesSearchPasienActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesSearchResultActivityContext() { return mContext; }

}
