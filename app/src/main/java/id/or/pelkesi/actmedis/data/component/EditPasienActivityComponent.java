package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.EditPasienActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.edit.EditPasienActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = EditPasienActivityModule.class)
public interface EditPasienActivityComponent {
    void inject(EditPasienActivity editPasienActivity);
}
