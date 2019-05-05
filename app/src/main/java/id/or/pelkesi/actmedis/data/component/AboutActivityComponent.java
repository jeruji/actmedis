package id.or.pelkesi.actmedis.data.component;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.AboutActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.about.AboutActivity;

@CustomScope
@Component(dependencies = NetComponent.class, modules = AboutActivityModule.class)
public interface AboutActivityComponent {
    void inject(AboutActivity aboutActivity);
}
