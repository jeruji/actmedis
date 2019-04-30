package id.or.pelkesi.actmedis.view.pasien.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.PasienAndDetail;
import id.or.pelkesi.actmedis.model.Patient;

public class EditPasienPresenter implements EditPasienActivityInterface.Presenter {

    EditPasienActivityInterface.View view;
    FirebaseFirestore db;
    Context context;

    @Inject
    public EditPasienPresenter(FirebaseFirestore db, EditPasienActivityInterface.View view, Context context){

        this.db = db;
        this.view = view;
        this.context = context;
    }

    @Override
    public void getPasienAndDetailData(String pasienId, final String groupId) {

        db.collection("data-pasien").document(pasienId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {
                Patient patient = documentSnapshots.toObject(Patient.class);
                patient.setId(documentSnapshots.getId());
                getPasienDetail(patient, groupId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    @Override
    public void submitImagePatient(File image, final String imgName) {
        view.showProgress(true);
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
                view.showProgress(false);
                view.showError("Failed Upload Image File");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                view.showProgress(false);
                updateFotoPath("gs://actmedis-admin.appspot.com/"+imgName + ".jpg");
            }
        });

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

    void updateFotoPath(String path){
        db.collection("data-pasien").document(view.getPasienId()).update("foto", path);
    }


    public void getPasienDetail(final Patient patient, String groupId){
        db.collection("detail_pasien").whereEqualTo("pasien_id", patient.getId())
                .whereEqualTo("group_id", groupId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DetailPasien> detailPasienListpatient = queryDocumentSnapshots.toObjects(DetailPasien.class);
                constructPasienAndDetail(detailPasienListpatient, patient);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Retrieve Data");
            }
        });
    }

    void getPasienDetail(final PasienAndDetail pasienAndDetail){
        db.collection("detail_pasien").whereEqualTo("pasien_id", view.getPasienId())
                .whereEqualTo("group_id", view.getGroupId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                submitEditedDetailData(pasienAndDetail, queryDocumentSnapshots.getDocuments().get(0).getId());
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.showError("Failed Retrieve Data");
                }
        });
    }

    public void constructPasienAndDetail(List<DetailPasien> detailPasienList, Patient patient){
        PasienAndDetail pasienAndDetail = new PasienAndDetail();
        pasienAndDetail.setId(patient.getId());
        pasienAndDetail.setNama(patient.getNama());
        pasienAndDetail.setTanggal_lahir(patient.getTanggal_lahir());
        pasienAndDetail.setUmur(patient.getUmur());
        pasienAndDetail.setGender(patient.getGender());
        pasienAndDetail.setFoto(patient.getFoto());
        pasienAndDetail.setDatecreated(patient.getDatecreated());
        pasienAndDetail.setDiagnosa(detailPasienList.get(0).getDiagnosa());
        pasienAndDetail.setGds(detailPasienList.get(0).getGds());
        pasienAndDetail.setGroup_id(detailPasienList.get(0).getGroup_id());
        pasienAndDetail.setKeluhan(detailPasienList.get(0).getKeluhan());
        pasienAndDetail.setKolesterol(detailPasienList.get(0).getKolesterol());
        pasienAndDetail.setRujukan(detailPasienList.get(0).getRujukan());
        pasienAndDetail.setStatus_penyakit(detailPasienList.get(0).getStatus_penyakit());
        pasienAndDetail.setSuhu(detailPasienList.get(0).getSuhu());
        pasienAndDetail.setTb_bb(detailPasienList.get(0).getTb_bb());
        pasienAndDetail.setTekanan_darah(detailPasienList.get(0).getTekanan_darah());
        pasienAndDetail.setTindakan(detailPasienList.get(0).getTindakan());
        pasienAndDetail.setUric_acid(detailPasienList.get(0).getUric_acid());

        view.constructEditData(pasienAndDetail);
    }

    @Override
    public int[] getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{year, month, day};
    }

    @Override
    public void submitEditData(final PasienAndDetail pasienAndDetail) {
        Map<String, Object> patient = new HashMap<>();
        patient.put("nama", pasienAndDetail.getNama());
        patient.put("tanggal_lahir", pasienAndDetail.getTanggal_lahir());
        patient.put("umur", pasienAndDetail.getUmur());
        patient.put("gender", pasienAndDetail.getGender());
        db.collection("data-pasien").document(view.getPasienId()).update(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getPasienDetail(pasienAndDetail);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Update Data");
            }
        });
    }

    void submitEditedDetailData(PasienAndDetail pasienAndDetail, String detailId){
        Map<String, Object> patientDetail = new HashMap<>();
        patientDetail.put("diagnosa", pasienAndDetail.getDiagnosa());
        patientDetail.put("gds", pasienAndDetail.getGds());
        patientDetail.put("keluhan", pasienAndDetail.getKeluhan());
        patientDetail.put("kolesterol", pasienAndDetail.getKolesterol());
        patientDetail.put("rujukan", pasienAndDetail.getRujukan());
        patientDetail.put("status_penyakit", pasienAndDetail.getStatus_penyakit());
        patientDetail.put("suhu", pasienAndDetail.getSuhu());
        patientDetail.put("tb_bb", pasienAndDetail.getTb_bb());
        patientDetail.put("tekanan_darah", pasienAndDetail.getTekanan_darah());
        patientDetail.put("tindakan", pasienAndDetail.getTindakan());
        patientDetail.put("uric_acid", pasienAndDetail.getUric_acid());


        db.collection("detail_pasien").document(detailId).update(patientDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.reloadDetailPasien();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.showError("Failed Update Data");
            }
        });
    }
}
