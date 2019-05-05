package id.or.pelkesi.actmedis.view.pasien.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.adapter.SearchResultAdapter;
import id.or.pelkesi.actmedis.data.component.DaggerSearchResultActivityComponent;
import id.or.pelkesi.actmedis.data.module.SearchResultActivityModule;
import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.Patient;
import id.or.pelkesi.actmedis.model.User;

public class SearchResultActivity extends AppCompatActivity implements SearchResultActivityInterface.View {

    @BindView(R.id.toolbar)
    Toolbar toolbarList;
    @BindView(R.id.imageSearchPasien)
    ImageView searchImagePatient;
    @BindView(R.id.edSearchName)
    EditText searchName;
    @BindView(R.id.edSearchTanggalLahir)
    EditText searchTanggalLahir;
    @BindView(R.id.spinnerSearchGender)
    Spinner searchGender;
    @BindView(R.id.edSearchUmur)
    EditText searchUmur;

    @BindView(R.id.recycler_view_detail_pasien)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;

    @Inject
    SearchResultAdapter pasienAdapter;

    @Inject
    SearchResultPresenter pasienPresenter;

    List<HarianGroup> harianGroupList;

    List<DetailPasien> detailPasienList;

    String patientId;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarList);
        getSupportActionBar().setTitle("Pasien");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerSearchResultActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .searchResultActivityModule(new SearchResultActivityModule(this, this))
                .build().inject(this);

        harianGroupList = new ArrayList<>();
        detailPasienList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(pasienAdapter);

        getPatientId();
        getPatienData();
        getSearchResultDetail();
    }

    @Override
    public void showPatientData(Patient patient) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://actmedis-admin.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.getRoot().child(patient.getFoto().substring(patient.getFoto().lastIndexOf("/")+1)).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(searchImagePatient);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError("Failed Retrieve Picture");
                    }
                });

        searchName.setText(patient.getNama());
        searchTanggalLahir.setText(patient.getTanggal_lahir());

        if(patient.getGender().equals("Laki-Laki"))
        {
            searchGender.setSelection(0);
        }
        else
        {
            searchGender.setSelection(1);
        }

        searchUmur.setText(patient.getUmur().toString());

    }

    @Override
    public void showSearchResultList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        pasienAdapter.setResult(harianGroupList, detailPasienList);
        pasienAdapter.notifyDataSetChanged();
    }

    @Override
    public void createSearchResultList(HarianGroup harianGroup, DetailPasien detailPasien){
        harianGroupList.add(harianGroup);
        detailPasienList.add(detailPasien);
        showSearchResultList();
    }

    public void getSearchResultDetail() {

        User user = App.getInstance().getPrefManager().getUser();

        pasienPresenter.getGroupAndDetailSearch(patientId, user.getRegion());
    }

    public void getPatienData() {
        pasienPresenter.getPatientData(patientId);
    }

    public void getPatientId(){
        Intent intent=getIntent();
        this.patientId = intent.getStringExtra("pasienId");
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

}
