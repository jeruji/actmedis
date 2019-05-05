package id.or.pelkesi.actmedis.view.pasien.search;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.model.Patient;

public class SearchPasienPresenter implements SearchPasienActivityInterface.Presenter {

    SearchPasienActivityInterface.View view;
    FirebaseFirestore db;
    Context context;

    @Inject
    public SearchPasienPresenter(FirebaseFirestore db, SearchPasienActivityInterface.View view, Context context){

        this.db = db;
        this.view = view;
        this.context = context;
    }

    @Override
    public void searchPatient(String nama) {

        String candidates = "abcdefghijklmnopqrstuvwxyz";
        String strFront = nama.substring(0,nama.length()-1);
        String strEndCode = nama.substring(nama.length()-1, nama.length());
        String searchCompare = strFront+candidates.charAt(candidates.indexOf(strEndCode)+1);
        db.collection("data-pasien").whereGreaterThanOrEqualTo("nama", nama)
                .whereLessThan("nama",searchCompare)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Patient> patientList = queryDocumentSnapshots.toObjects(Patient.class);
                for(int index = 0; index<patientList.size(); index++){
                    patientList.get(index).setId(queryDocumentSnapshots.getDocuments().get(index).getId());
                }
                view.showProgress(true);
                view.createPatientList(patientList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }
}
