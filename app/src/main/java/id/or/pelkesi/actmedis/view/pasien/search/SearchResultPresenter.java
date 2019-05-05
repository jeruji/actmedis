package id.or.pelkesi.actmedis.view.pasien.search;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.Patient;

public class SearchResultPresenter implements SearchResultActivityInterface.Presenter {

    SearchResultActivityInterface.View view;
    FirebaseFirestore db;
    Context context;
    List<HarianGroup> harianGroups;
    List<DetailPasien> detailPasiens;

    @Inject
    public SearchResultPresenter(FirebaseFirestore db, SearchResultActivityInterface.View view, Context context){

        this.db = db;
        this.view = view;
        this.context = context;
        this.harianGroups = new ArrayList<>();
        this.detailPasiens = new ArrayList<>();
    }

    @Override
    public void getGroupAndDetailSearch(String patientId, final List<String> kabupaten) {

        db.collection("detail_pasien").whereEqualTo("pasien_id", patientId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DetailPasien> detailPasienList = queryDocumentSnapshots.toObjects(DetailPasien.class);
                getGroupHarian(detailPasienList, kabupaten);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    @Override
    public void getPatientData(String patientId) {
        db.collection("data-pasien").document(patientId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Patient patient = documentSnapshot.toObject(Patient.class);
                view.showPatientData(patient);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    public void getGroupHarian(final List<DetailPasien> detailPasienList, final List<String> kabupaten){

        for(int index = 0; index<detailPasienList.size(); index++){
            final DetailPasien detailPasien = detailPasienList.get(index);
            db.collection("laporan-harian-grouping").document(detailPasien.getGroup_id())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HarianGroup harianGroup = documentSnapshot.toObject(HarianGroup.class);
                    if(kabupaten.contains(harianGroup.getKabupaten())){
                        view.createSearchResultList(harianGroup, detailPasien);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.showError("Failed Retrieve Data");
                }
            });
        }

    }
}
