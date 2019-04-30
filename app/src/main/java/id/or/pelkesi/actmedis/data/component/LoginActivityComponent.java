package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.LoginActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.login.LoginActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = LoginActivityModule.class)
public interface LoginActivityComponent {
    void inject(LoginActivity loginActivity);
}
