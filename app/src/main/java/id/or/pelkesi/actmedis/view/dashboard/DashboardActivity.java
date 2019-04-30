package id.or.pelkesi.actmedis.view.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.data.component.DaggerDashboardActivityComponent;
import id.or.pelkesi.actmedis.data.module.DashboardActivityModule;
import id.or.pelkesi.actmedis.model.User;
import id.or.pelkesi.actmedis.services.DeleteTokenService;

public class DashboardActivity extends AppCompatActivity implements DashboardActivityInterface.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbarDashboard;

    @BindView(R.id.nav_view)
    NavigationView navigationViewDashboard;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayoutDashboard;

    @BindView(R.id.name)
    TextView tvName;

    @BindView(R.id.email)
    TextView tvEmail;

    @Inject
    DashboardPresenter dashboardPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarDashboard);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerDashboardActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .dashboardActivityModule((new DashboardActivityModule(this, this)))
                .build().inject(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayoutDashboard, toolbarDashboard, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayoutDashboard.addDrawerListener(toggle);

        toggle.syncState();

        navigationViewDashboard.setNavigationItemSelectedListener(this);
        showProfile();
    }

    @Override
    public void showProfile() {
        User user = App.getInstance().getPrefManager().getUser();
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
    }

    @Override
    @OnClick(R.id.listPasienBtn)
    public void toPasienPage() {
        Intent intent = new Intent();
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.list.ListPasienActivity");
        //App.getInstance().getPrefManager().storeUser(user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    @OnClick(R.id.inputPasienBtn)
    public void toFormPasien() {
        Intent intent = new Intent();
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.add.AddPasienActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void toLoginPage() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.login.LoginActivity");
        startActivity(intent);
    }

    @Override
    public void toAboutPage() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_pasien_list) {
            toPasienPage();
        } else if (id == R.id.nav_add_pasien) {
            toFormPasien();
        } else if (id == R.id.nav_about) {
            toAboutPage();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, DeleteTokenService.class);
            startService(intent);
            dashboardPresenter.signOutUser();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayoutDashboard.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutDashboard.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit Application")
                    .setMessage("Are you sure you want to exit from Rekam Medis Pelkesi?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}
