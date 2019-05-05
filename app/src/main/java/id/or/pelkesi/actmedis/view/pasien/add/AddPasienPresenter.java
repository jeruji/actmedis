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
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.App;
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
                        setTotalDataPasien();
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

    public void setTotalDataPasien(){
        db.collection("total_data_pasien").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int current = 0;
                String currentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                current = Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).get("total").toString())+1;

                Map<String, Object> totalPasienObj = new HashMap<>();
                totalPasienObj.put("total", current);

                db.collection("total_data_pasien").document(currentId).set(totalPasienObj)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                view.showError("Failed to Edit Total Patient");
                            }
                        });
            }
        });
    }

    @Override
    public void submitPatientData(final Map<String, Object> detailPasien, final Map<String, Object> harianGroup){
        db.collection("laporan-harian-grouping")
                .add(harianGroup)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submitGroupingKabupaten(harianGroup);
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
                        setTotalDataPasien();
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
                        setTotalDataPasien();
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
                        submitGroupingKabupaten(harianGroup);
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

    public void submitGroupingKabupaten(final Map<String, Object> harianGroup){

        db.collection("kabupaten_grouping").whereEqualTo("nama", harianGroup.get("kabupaten"))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String kabupatenId = queryDocumentSnapshots.getDocuments().get(0).getId();
                Map<String, Object> kecamatanMap = new HashMap<>();
                kecamatanMap.put("kabupaten_id", kabupatenId);
                kecamatanMap.put("nama", harianGroup.get("kecamatan"));
                submitGroupingKecamatan(harianGroup, kecamatanMap);
            }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.showError("Failed Submit Data");
                }
            });

    }

    public void submitGroupingKecamatan(final Map<String, Object> harianGroup, final Map<String, Object> kecamatanGroup){

        db.collection("kecamatan_grouping").whereEqualTo("kabupaten_id", kecamatanGroup.get("kabupaten_id"))
                .whereEqualTo("nama", kecamatanGroup.get("nama")).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            db.collection("kecamatan_grouping").add(kecamatanGroup)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            String kecamatanId = documentReference.getId();
                                            Map<String, Object> desaMap = new HashMap<>();
                                            desaMap.put("kecamatan_id", kecamatanId);
                                            desaMap.put("nama", harianGroup.get("desa"));
                                            submitGroupingDesa(harianGroup, desaMap);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            view.showError("Failed Submit Data");
                                        }
                                    });
                        }
                        else{
                            String kecamatanId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Map<String, Object> desaMap = new HashMap<>();
                            desaMap.put("kecamatan_id", kecamatanId);
                            desaMap.put("nama", harianGroup.get("desa"));
                            submitGroupingDesa(harianGroup, desaMap);
                        }
                    }
                });
    }

    public void submitGroupingDesa(final Map<String, Object> harianGroup, final Map<String, Object> desaGroup){

        db.collection("desa_grouping").whereEqualTo("kecamatan_id", desaGroup.get("kecamatan_id"))
                .whereEqualTo("nama", desaGroup.get("nama")).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            db.collection("desa_grouping").add(desaGroup)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            String desaId = documentReference.getId();
                                            Map<String, Object> dusunMap = new HashMap<>();
                                            dusunMap.put("desa_id", desaId);
                                            dusunMap.put("nama", harianGroup.get("dusun"));
                                            submitGroupingDusun(harianGroup, dusunMap);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            view.showError("Failed Submit Data");
                                        }
                                    });
                        }
                        else{
                            String desaId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Map<String, Object> dusunMap = new HashMap<>();
                            dusunMap.put("desa_id", desaId);
                            dusunMap.put("nama", harianGroup.get("dusun"));
                            submitGroupingDusun(harianGroup, dusunMap);
                        }
                    }
                });
    }

    public void submitGroupingDusun(final Map<String, Object> harianGroup, final Map<String, Object> dusunGroup){

        db.collection("dusun_grouping").whereEqualTo("desa_id", dusunGroup.get("desa_id"))
                .whereEqualTo("nama", dusunGroup.get("nama")).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            db.collection("dusun_grouping").add(dusunGroup)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            String dusunId = documentReference.getId();
                                            Map<String, Object> puskesmasMap = new HashMap<>();
                                            puskesmasMap.put("dusun_id", dusunId);
                                            puskesmasMap.put("nama", harianGroup.get("puskesmas"));
                                            submitGroupingPuskesmas(harianGroup, puskesmasMap);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            view.showError("Failed Submit Data");
                                        }
                                    });
                        }
                        else{
                            String dusunId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Map<String, Object> puskesmasMap = new HashMap<>();
                            puskesmasMap.put("dusun_id", dusunId);
                            puskesmasMap.put("nama", harianGroup.get("puskesmas"));
                            submitGroupingPuskesmas(harianGroup, puskesmasMap);
                        }
                    }
                });
    }

    public void submitGroupingPuskesmas(final Map<String, Object> harianGroup, final Map<String, Object> puskesmasGroup){

        db.collection("puskesmas_grouping").whereEqualTo("dusun_id", puskesmasGroup.get("dusun_id"))
                .whereEqualTo("nama", puskesmasGroup.get("nama")).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            db.collection("puskesmas_grouping").add(puskesmasGroup)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            view.showError("Failed Submit Data");
                                        }
                                    });
                        }
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
                        setTotalDataPasien();
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
        List<String> userKabupatenList = user.getRegion();

        for(int index = 0; index < userKabupatenList.size(); index++){
            kabupatenList.add(userKabupatenList.get(index));
        }

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

        db.collection("kabupaten_grouping").whereEqualTo("nama", kabupaten)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                db.collection("kecamatan_grouping").whereEqualTo("kabupaten_id",queryDocumentSnapshots.getDocuments().get(0).getId())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> kecamatanList = new ArrayList<>();
                        kecamatanList.add("-- Pilih Kecamatan --");
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for(int index = 0; index<documentSnapshots.size(); index++){
                            kecamatanList.add(documentSnapshots.get(index).get("nama").toString());
                        }
                        kecamatanList.add("-- Tambah Kecamatan --");
                        view.constructKecamatan(kecamatanList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Retrieve Data");
                    }
                });
            }
        });
    }

    @Override
    public void getDesaList(String kecamatan){

        db.collection("kecamatan_grouping").whereEqualTo("nama", kecamatan)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                db.collection("desa_grouping").whereEqualTo("kecamatan_id", queryDocumentSnapshots.getDocuments().get(0).getId())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> desaList = new ArrayList<>();
                        desaList.add("-- Pilih Desa --");
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for(int index = 0; index<documentSnapshots.size(); index++){
                            desaList.add(documentSnapshots.get(index).get("nama").toString());
                        }

                        desaList.add("-- Tambah Desa --");
                        view.constructDesa(desaList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Retrieve Data");
                    }
                });
            }
        });
    }

    @Override
    public void getDusunList(String desa){

        db.collection("desa_grouping").whereEqualTo("nama", desa)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                db.collection("dusun_grouping").whereEqualTo("desa_id", queryDocumentSnapshots.getDocuments().get(0).getId())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> dusunList = new ArrayList<>();
                        dusunList.add("-- Pilih Dusun --");
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for(int index = 0; index<documentSnapshots.size(); index++){
                            dusunList.add(documentSnapshots.get(index).get("nama").toString());
                        }

                        dusunList.add("-- Tambah Dusun --");
                        view.constructDusun(dusunList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Retrieve Data");
                    }
                });
            }
        });
    }

    @Override
    public void getPuskesmasList(String dusun){

        db.collection("dusun_grouping").whereEqualTo("nama", dusun)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                db.collection("puskesmas_grouping").whereEqualTo("dusun_id", queryDocumentSnapshots.getDocuments().get(0).getId())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> puskesmasList = new ArrayList<>();
                        puskesmasList.add("-- Pilih Puskesmas --");
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for(int index = 0; index<documentSnapshots.size(); index++){
                            puskesmasList.add(documentSnapshots.get(index).get("nama").toString());
                        }
                        puskesmasList.add("-- Tambah Puskesmas --");
                        view.constructPuskesmas(puskesmasList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showError("Failed Retrieve Data");
                    }
                });
            }
        });
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
