package id.or.pelkesi.actmedis.view.pasien.list;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.PasienAndDetail;
import id.or.pelkesi.actmedis.model.Patient;
import id.or.pelkesi.actmedis.model.User;

public class ListPasienPresenter implements ListPasienActivityInterface.Presenter {

    ListPasienActivityInterface.View view;
    FirebaseFirestore db;
    Context context;

    @Inject
    public ListPasienPresenter(FirebaseFirestore db, ListPasienActivityInterface.View view, Context context) {

        this.db = db;
        this.view = view;
        this.context = context;

    }

    @Override
    public void getGroupingList(String puskesmas, String dusun, String desa, String kecamatan, String kabupaten) {
        Date selectedDate = view.getSelectedDate();
        Date untilDate = view.getSelectedDatePlusOne(selectedDate);

        db.collection("laporan-harian-grouping").whereGreaterThan("tanggal", selectedDate)
            .whereLessThan("tanggal", untilDate).whereEqualTo("kabupaten", kabupaten)
            .whereEqualTo("kecamatan", kecamatan).whereEqualTo("desa", desa)
            .whereEqualTo("dusun", dusun).whereEqualTo("puskesmas", puskesmas)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> groupId = new ArrayList<>();
                        for(int index=0;index<queryDocumentSnapshots.size();index++){
                            groupId.add(queryDocumentSnapshots.getDocuments().get(index).getId());
                        }
                        constructGroupingList(groupId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Retrieve Data");
                    }
                });

    }

    public void constructGroupingList(List<String> harianGroupList){
        for(int index = 0; index<harianGroupList.size(); index++){

            db.collection("detail_pasien").whereEqualTo("group_id", harianGroupList.get(index))
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DetailPasien> detailPasienList = queryDocumentSnapshots.toObjects(DetailPasien.class);
                    getPasienList(detailPasienList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.showError("Failed Retrieve Data");
                }
            });
        }
    }

    public void getPasienList(List<DetailPasien> detailPasienList){
        for(int index = 0; index<detailPasienList.size(); index++){
            final DetailPasien detailPasien = detailPasienList.get(index);
            final String pasienId = detailPasien.getPasien_id();

            db.collection("data-pasien").document(pasienId)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    patient.setId(pasienId);

                    PasienAndDetail pasienAndDetail = new PasienAndDetail();
                    pasienAndDetail.setId(pasienId);
                    pasienAndDetail.setNama(patient.getNama());
                    pasienAndDetail.setUmur(patient.getUmur());
                    pasienAndDetail.setGender(patient.getGender());
                    pasienAndDetail.setTanggal_lahir(patient.getTanggal_lahir());
                    pasienAndDetail.setFoto(patient.getFoto());
                    pasienAndDetail.setDatecreated(patient.getDatecreated());
                    pasienAndDetail.setDiagnosa(detailPasien.getDiagnosa());
                    pasienAndDetail.setGds(detailPasien.getGds());
                    pasienAndDetail.setGroup_id(detailPasien.getGroup_id());
                    pasienAndDetail.setKeluhan(detailPasien.getKeluhan());
                    pasienAndDetail.setKolesterol(detailPasien.getKolesterol());
                    pasienAndDetail.setRujukan(detailPasien.getRujukan());
                    pasienAndDetail.setStatus_penyakit(detailPasien.getStatus_penyakit());
                    pasienAndDetail.setSuhu(detailPasien.getSuhu());
                    pasienAndDetail.setTb_bb(detailPasien.getTb_bb());
                    pasienAndDetail.setTekanan_darah(detailPasien.getTekanan_darah());
                    pasienAndDetail.setTindakan(detailPasien.getTindakan());
                    pasienAndDetail.setUric_acid(detailPasien.getUric_acid());

                    view.createPatientList(pasienAndDetail);
                    view.showProgress(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.showError("Failed Retrieve Data");
                }
            });
        }
    }

    @Override
    public void getPasienDetail(Patient patient) {

    }

    @Override
    public int[] getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{year, month, day};
    }

    @Override
    public void getKabupatenList() {
        User user = App.getInstance().getPrefManager().getUser();
        ArrayList<String> kabupatenList = new ArrayList<>();
        kabupatenList.add("-- Pilih Kabupaten --");

        for(int index = 0; index < user.getRegion().size(); index++){
            kabupatenList.add(user.getRegion().get(index));
        }

        view.constructKabupaten(kabupatenList);
    }

    @Override
    public void getKecamatanList(String kabupaten) {
        Date selectedDate = view.getSelectedDate();
        Date untilDate = view.getSelectedDatePlusOne(selectedDate);

        db.collection("laporan-harian-grouping").whereGreaterThan("tanggal", selectedDate).whereLessThan("tanggal", untilDate).whereEqualTo("kabupaten", kabupaten).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<HarianGroup> harianGroupList = queryDocumentSnapshots.toObjects(HarianGroup.class);
                constructKecamatanList(harianGroupList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    public void constructKecamatanList(List<HarianGroup> harianGroupList){
        ArrayList<String> kecamatanList = new ArrayList<>();
        kecamatanList.add("-- Pilih Kecamatan --");
        for(int index = 0; index<harianGroupList.size(); index++){
            HarianGroup harianGroup = harianGroupList.get(index);
            if(!kecamatanList.contains(harianGroup.getKecamatan())){
                kecamatanList.add(harianGroup.getKecamatan());
            }
        }
        view.constructKecamatan(kecamatanList);
    }

    @Override
    public void getDesaList(String kecamatan, String kabupaten){
        Date selectedDate = view.getSelectedDate();
        Date untilDate = view.getSelectedDatePlusOne(selectedDate);

        db.collection("laporan-harian-grouping").whereGreaterThan("tanggal", selectedDate)
                .whereLessThan("tanggal", untilDate).whereEqualTo("kabupaten", kabupaten)
                .whereEqualTo("kecamatan", kecamatan).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<HarianGroup> harianGroupList = queryDocumentSnapshots.toObjects(HarianGroup.class);
                constructDesaList(harianGroupList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    public void constructDesaList(List<HarianGroup> harianGroupList){
        ArrayList<String> desaList = new ArrayList<>();
        desaList.add("-- Pilih Desa --");
        for(int index = 0; index<harianGroupList.size(); index++){
            HarianGroup harianGroup = harianGroupList.get(index);
            if(!desaList.contains(harianGroup.getDesa())){
                desaList.add(harianGroup.getDesa());
            }
        }
        view.constructDesa(desaList);
    }

    @Override
    public void getDusunList(String desa, String kecamatan, String kabupaten){
        Date selectedDate = view.getSelectedDate();
        Date untilDate = view.getSelectedDatePlusOne(selectedDate);

        db.collection("laporan-harian-grouping").whereGreaterThan("tanggal", selectedDate)
                .whereLessThan("tanggal", untilDate).whereEqualTo("kabupaten", kabupaten)
                .whereEqualTo("kecamatan", kecamatan).whereEqualTo("desa", desa)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<HarianGroup> harianGroupList = queryDocumentSnapshots.toObjects(HarianGroup.class);
                constructDusunList(harianGroupList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    public void constructDusunList(List<HarianGroup> harianGroupList){
        ArrayList<String> dusunList = new ArrayList<>();
        dusunList.add("-- Pilih Dusun --");
        for(int index = 0; index<harianGroupList.size(); index++){
            HarianGroup harianGroup = harianGroupList.get(index);
            if(!dusunList.contains(harianGroup.getDusun())){
                dusunList.add(harianGroup.getDusun());
            }
        }
        view.constructDusun(dusunList);
    }

    @Override
    public void getPuskesmasList(String dusun, String desa, String kecamatan, String kabupaten){
        Date selectedDate = view.getSelectedDate();
        Date untilDate = view.getSelectedDatePlusOne(selectedDate);

        db.collection("laporan-harian-grouping").whereGreaterThan("tanggal", selectedDate)
                .whereLessThan("tanggal", untilDate).whereEqualTo("kabupaten", kabupaten)
                .whereEqualTo("kecamatan", kecamatan).whereEqualTo("desa", desa)
                .whereEqualTo("dusun", dusun)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<HarianGroup> harianGroupList = queryDocumentSnapshots.toObjects(HarianGroup.class);
                constructPuskesmasList(harianGroupList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    public void constructPuskesmasList(List<HarianGroup> harianGroupList){
        ArrayList<String> puskesmasList = new ArrayList<>();
        puskesmasList.add("-- Pilih Puskesmas --");
        for(int index = 0; index<harianGroupList.size(); index++){
            HarianGroup harianGroup = harianGroupList.get(index);
            if(!puskesmasList.contains(harianGroup.getPuskesmas())){
                puskesmasList.add(harianGroup.getPuskesmas());
            }
        }
        view.constructPuskesmas(puskesmasList);
    }
}
