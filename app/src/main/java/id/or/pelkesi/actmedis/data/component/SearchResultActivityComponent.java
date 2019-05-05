package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.SearchResultActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.search.SearchResultActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = SearchResultActivityModule.class)
public interface SearchResultActivityComponent {
    void inject(SearchResultActivity searchResultActivity);
}
