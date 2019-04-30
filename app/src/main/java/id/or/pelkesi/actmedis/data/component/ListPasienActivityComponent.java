package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.ListPasienActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.list.ListPasienActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = ListPasienActivityModule.class)
public interface ListPasienActivityComponent {
    void inject(ListPasienActivity listPasienActivity);
}
