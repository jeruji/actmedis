package id.or.pelkesi.actmedis.view.pasien.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.adapter.SearchAdapter;
import id.or.pelkesi.actmedis.data.component.DaggerSearchPasienActivityComponent;
import id.or.pelkesi.actmedis.data.module.SearchPasienActivityModule;
import id.or.pelkesi.actmedis.model.Patient;


public class SearchPasienActivity extends AppCompatActivity implements SearchPasienActivityInterface.View, SearchAdapter.SearchAdapterCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbarList;
    @BindView(R.id.edTextName)
    EditText nameEd;
    @BindView(R.id.recycler_search_pasien)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_search)
    ProgressBar progressBar;

    @Inject
    SearchPasienPresenter pasienPresenter;

    @Inject
    SearchAdapter pasienAdapter;

    List<Patient> patientList;

    private LinearLayoutManager mLinearLayoutManager;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarList);
        getSupportActionBar().setTitle("Pasien");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerSearchPasienActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .searchPasienActivityModule(new SearchPasienActivityModule(this, this))
                .build().inject(this);

        pasienAdapter.setCallback(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(pasienAdapter);
    }

    @OnClick(R.id.btnSubmitSearchPasien)
    public void searchPatient(){
        String nama = nameEd.getText().toString();
        pasienPresenter.searchPatient(nama);
    }

    @Override
    public void showError(String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Failed");
        dlgAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    @Override
    public void showProgress(boolean show){
        if (show && pasienAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showPatient() {
        mRecyclerView.setVisibility(View.VISIBLE);
        pasienAdapter.setPasien(patientList);
        pasienAdapter.notifyDataSetChanged();
        showProgress(false);
    }

    @Override
    public void createPatientList(List<Patient> patientListToBeAdd) {
        patientList = new ArrayList<>();
        patientList.addAll(patientListToBeAdd);
        showPatient();
    }

    @Override
    public void onPasienClicked(Patient patient) {
        Intent intent = new Intent();
        intent.putExtra("pasienId", patient.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.search.SearchResultActivity");
        startActivity(intent);
    }
}
