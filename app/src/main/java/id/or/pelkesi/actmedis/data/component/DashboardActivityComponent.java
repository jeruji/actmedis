package id.or.pelkesi.actmedis.data.component;
import dagger.Component;
import id.or.pelkesi.actmedis.data.module.DashboardActivityModule;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.dashboard.DashboardActivity;


@CustomScope
@Component(dependencies = NetComponent.class, modules = DashboardActivityModule.class)
public interface DashboardActivityComponent {
    void inject(DashboardActivity dashboardActivity);
}
