package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.SearchPasienActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.search.SearchPasienActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = SearchPasienActivityModule.class)
public interface SearchPasienActivityComponent {
    void inject(SearchPasienActivity searchPasienActivity);
}
