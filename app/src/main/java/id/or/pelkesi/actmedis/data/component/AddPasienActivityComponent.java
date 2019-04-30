package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.AddPasienActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.add.AddPasienActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = AddPasienActivityModule.class)
public interface AddPasienActivityComponent {
    void inject(AddPasienActivity addPasienActivity);
}
