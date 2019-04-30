package id.or.pelkesi.actmedis.view.pasien.add;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.Patient;
import id.or.pelkesi.actmedis.model.User;

public class AddPasienPresenter implements AddPasienActivityInterface.Presenter {

    AddPasienActivityInterface.View view;
    FirebaseFirestore db;
    Context context;
    String groupId = "";
    String patientId = "";

    @Inject
    public AddPasienPresenter(FirebaseFirestore db, AddPasienActivityInterface.View view, Context context){

        this.db = db;
        this.view = view;
        this.context = context;
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
    public void setPatientSelectedId(Patient patient) {
        this.patientId = patient.getId();
        view.showExistingPatientData(patient);
    }

    @Override
    public void submitImagePatient(File image, String imgName) {
        StorageReference fileRef = null;
        Bitmap bmp;

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://actmedis-admin.appspot.com");
        StorageReference storageRef = storage.getReference();

        fileRef = storageRef.child(imgName + ".jpg");

        bmp = BitmapFactory.decodeFile(image.getAbsolutePath());
        bmp = checkOrientation(bmp, image);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Upload Image File");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

    @Override
    public String[] getPatientAndGroupId(){
        if(!patientId.equals("")||!groupId.equals("")){
            String[] patientAndGroupId = new String[2];
            patientAndGroupId[0] = patientId;
            patientAndGroupId[1] = groupId;

            return patientAndGroupId;
        }

        return null;
    }

    @Override
    public void submitPatientData(Map<String, Object> detailPasien){
        db.collection("detail_pasien")
                .add(detailPasien)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        view.toListPatient();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    @Override
    public void submitPatientData(final Map<String, Object> detailPasien, Map<String, Object> harianGroup){
        db.collection("laporan-harian-grouping")
                .add(harianGroup)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submitPatientDataByDetailPatient(documentReference.getId(), detailPasien);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    public void submitPatientDataByDetailPatient(String harianGroupId, Map<String, Object> detailPasien){
        detailPasien.put("group_id", harianGroupId);
        db.collection("detail_pasien")
                .add(detailPasien)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        view.toListPatient();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    @Override
    public void submitPatientDataByGroup(final Map<String, Object> detailPasien, Map<String, Object> patient){
        submitImagePatient(view.getImageFile(), patient.get("nama").toString().replaceAll(" ", "_"));
        patient.put("foto","gs://actmedis-admin.appspot.com/"+patient.get("nama").toString().replaceAll(" ", "_")+".jpg");
        db.collection("data-pasien")
                .add(patient)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submitPatientDataByGroup(documentReference.getId(), detailPasien);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    public void submitPatientDataByGroup(String patientId, Map<String, Object> detailPasien){
        detailPasien.put("pasien_id", patientId);
        db.collection("detail_pasien")
                .add(detailPasien)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        view.toListPatient();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    @Override
    public void submitPatientData(final Map<String, Object> detailPasien, final Map<String, Object> harianGroup, Map<String, Object> patient){
        submitImagePatient(view.getImageFile(), patient.get("nama").toString().replaceAll(" ", "_"));
        patient.put("foto","gs://actmedis-admin.appspot.com/"+patient.get("nama").toString().replaceAll(" ", "_")+".jpg");
        db.collection("data-pasien")
                .add(patient)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submitPatientData(documentReference.getId(), detailPasien, harianGroup);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    public void submitPatientData(final String patientId, final Map<String, Object> detailPasien, Map<String, Object> harianGroup){
        db.collection("laporan-harian-grouping")
                .add(harianGroup)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submitPatientData(patientId, documentReference.getId(), detailPasien);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    public void submitPatientData(String patientId, String groupId, Map<String, Object> detailPasien){
        detailPasien.put("pasien_id", patientId);
        detailPasien.put("group_id", groupId);
        db.collection("detail_pasien")
                .add(detailPasien)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        view.toListPatient();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Submit Data");
                    }
                });
    }

    @Override
    public void getKabupatenList() {
        User user = App.getInstance().getPrefManager().getUser();
        ArrayList<String> kabupatenList = new ArrayList<>();
        kabupatenList.add("-- Pilih Kabupaten --");
        kabupatenList.add(user.getRegion());

        view.constructKabupaten(kabupatenList);
    }

    public Bitmap checkOrientation(Bitmap bmp, File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            // rotating bitmap
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        } catch (Exception e) {

        }

        return bmp;
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

    public void constructKecamatanList(List<HarianGroup> harianGroupList) {
        ArrayList<String> kecamatanList = new ArrayList<>();
        kecamatanList.add("-- Pilih Kecamatan --");
        for (int index = 0; index < harianGroupList.size(); index++) {
            HarianGroup harianGroup = harianGroupList.get(index);
            if (!kecamatanList.contains(harianGroup.getKecamatan())) {
                kecamatanList.add(harianGroup.getKecamatan());
            }
        }
        kecamatanList.add("-- Tambah Kecamatan --");
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
        desaList.add("-- Tambah Desa --");
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
        dusunList.add("-- Tambah Dusun --");
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
        puskesmasList.add("-- Tambah Puskesmas --");
        view.constructPuskesmas(puskesmasList);
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

                for(int index=0;index<queryDocumentSnapshots.size();index++){
                    groupId = queryDocumentSnapshots.getDocuments().get(index).getId();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });

    }
}
