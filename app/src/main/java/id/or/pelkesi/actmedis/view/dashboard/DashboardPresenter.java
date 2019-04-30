package id.or.pelkesi.actmedis.view.dashboard;

import android.content.Context;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.App;

public class DashboardPresenter implements DashboardActivityInterface.Presenter {

    DashboardActivityInterface.View view;

    @Inject
    public DashboardPresenter(DashboardActivityInterface.View view, Context context) {

        this.view = view;

    }


    @Override
    public void signOutUser() {
        App.getInstance().getPrefManager().clear();
        view.toLoginPage();
    }
}
