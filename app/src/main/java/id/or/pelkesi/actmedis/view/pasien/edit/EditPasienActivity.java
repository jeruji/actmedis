package id.or.pelkesi.actmedis.view.pasien.edit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.data.component.DaggerEditPasienActivityComponent;
import id.or.pelkesi.actmedis.data.module.EditPasienActivityModule;
import id.or.pelkesi.actmedis.model.PasienAndDetail;

public class EditPasienActivity extends AppCompatActivity implements EditPasienActivityInterface.View{

    @BindView(R.id.imagePasien)
    ImageView imgPasien;
    @BindView(R.id.edTextName)
    EditText nameEditText;
    @BindView(R.id.spinnerGender)
    Spinner genderSpinner;
    @BindView(R.id.edTextUmur)
    EditText umurEditText;
    @BindView(R.id.edTekananDarah)
    EditText tekananDarahEditText;
    @BindView(R.id.edSuhu)
    EditText suhuEditText;
    @BindView(R.id.edTBBB)
    EditText tbbbEditText;
    @BindView(R.id.edKeluhan)
    EditText keluhanEditText;
    @BindView(R.id.edDiagnosa)
    EditText diagnosaEditText;
    @BindView(R.id.edStatus)
    EditText statusEditText;
    @BindView(R.id.edTindakan)
    EditText tindakanEditText;
    @BindView(R.id.edGds)
    EditText gdsEditText;
    @BindView(R.id.edUricAcid)
    EditText uricAcidEditText;
    @BindView(R.id.edCholesterol)
    EditText cholesterolEditText;
    @BindView(R.id.edRujukan)
    EditText rujukanEditText;
    @BindView(R.id.progressUploadPasienImage)
    ProgressBar progressBar;
    @BindView(R.id.edTextTanggalLahir)
    EditText tanggalLahirEditText;
    @BindView(R.id.calendarTglLahir)
    ImageView calendarTglLahir;
    @BindView(R.id.btnSubmitEditPasien)
    Button submitEditPasien;
    @BindView(R.id.toolbar)
    Toolbar toolbarList;

    @Inject
    EditPasienPresenter pasienPresenter;

    File imgFile;
    Bitmap bitmap;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarList);
        getSupportActionBar().setTitle("Pasien");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerEditPasienActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .editPasienActivityModule(new EditPasienActivityModule(this, this))
                .build().inject(this);

        getPasienAndGroupId();
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getPasienAndGroupId(){
        Intent intent=getIntent();
        String pasienId = intent.getStringExtra("pasienId");
        String groupId = intent.getStringExtra("groupId");
        pasienPresenter.getPasienAndDetailData(pasienId, groupId);
    }

    @Override
    public String getPasienId(){
        Intent intent=getIntent();
        String pasienId = intent.getStringExtra("pasienId");
        return pasienId;
    }

    @Override
    public String getGroupId(){
        Intent intent=getIntent();
        String groupId = intent.getStringExtra("groupId");
        return groupId;
    }

    @OnClick(R.id.btnUpload)
    public void chooseImageProfile() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.imgFile = getBitmapFile(data);
            showImage(this.imgFile);
            pasienPresenter.submitImagePatient(this.imgFile, nameEditText.getText().toString().replaceAll(" ", "_"));
        }

    }

    public File getBitmapFile(Intent data) {
        isStoragePermissionGranted();
        Uri selectedImage = data.getData();
        Cursor cursor = getContentResolver().query(selectedImage, new String[] { android.provider.MediaStore.Images.Media.DATA }, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        String selectedImagePath = cursor.getString(idx);
        cursor.close();
        return new File(selectedImagePath);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void showImage(File imgFile) {
        Picasso.get()
                .load(imgFile)
                .into(imgPasien);
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
    public void constructEditData(PasienAndDetail pasienAndDetail) {
        nameEditText.setText(pasienAndDetail.getNama());
        tanggalLahirEditText.setText(pasienAndDetail.getTanggal_lahir());
        umurEditText.setText(pasienAndDetail.getUmur().toString());
        tekananDarahEditText.setText(pasienAndDetail.getTekanan_darah());

        if(pasienAndDetail.getGender().equals("Laki-Laki")) {
            genderSpinner.setSelection(0);
        }
        else {
            genderSpinner.setSelection(1);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://actmedis-admin.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.getRoot().child(pasienAndDetail.getFoto().substring(pasienAndDetail.getFoto().lastIndexOf("/")+1)).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgPasien);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError("Failed Retrieve Picture");
                    }
                });

        suhuEditText.setText(pasienAndDetail.getSuhu());
        tbbbEditText.setText(pasienAndDetail.getTb_bb());
        keluhanEditText.setText(pasienAndDetail.getKeluhan());
        diagnosaEditText.setText(pasienAndDetail.getDiagnosa());
        statusEditText.setText(pasienAndDetail.getStatus_penyakit());
        tindakanEditText.setText(pasienAndDetail.getTindakan());
        gdsEditText.setText(pasienAndDetail.getGds());
        uricAcidEditText.setText(pasienAndDetail.getUric_acid());
        cholesterolEditText.setText(pasienAndDetail.getKolesterol());
        rujukanEditText.setText(pasienAndDetail.getRujukan());
    }

    @OnClick(R.id.calendarTglLahir)
    public void showCalendar() {
        calendarDialog(99).show();
    }

    private Dialog calendarDialog(int id) {
        if (id == 99) {
            int[] dateArr = pasienPresenter.getDate();
            return new DatePickerDialog(this, dateListener, dateArr[0], dateArr[1], dateArr[2]);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        String dateStr = "";

        if(Integer.toString(month).length()==1)
            dateStr = "0"+month;
        else
            dateStr = Integer.toString(month);

        if(Integer.toString(day).length()==1)
            dateStr += "/0"+day;
        else
            dateStr += "/"+day;

        dateStr += "/"+year;

        tanggalLahirEditText.setText(dateStr);
    }

    @OnClick(R.id.btnSubmitEditPasien)
    public void submitEditData(){

        submitEditPasien.setClickable(false);

        boolean validate = true;

        if(nameEditText.getText().toString().equals("")||
                tanggalLahirEditText.getText().toString().equals("")||
                umurEditText.getText().toString().equals("")||
                tekananDarahEditText.getText().toString().equals("")||
                suhuEditText.getText().toString().equals("")||
                tbbbEditText.getText().toString().equals("")||
                keluhanEditText.getText().toString().equals("")||
                diagnosaEditText.getText().toString().equals("")||
                statusEditText.getText().toString().equals("")||
                tindakanEditText.getText().toString().equals("")||
                gdsEditText.getText().toString().equals("")||
                uricAcidEditText.getText().toString().equals("")||
                cholesterolEditText.getText().toString().equals("")||
                rujukanEditText.getText().toString().equals(""))
            validate = false;

        if(validate) {
            PasienAndDetail pasienAndDetail = new PasienAndDetail();
            pasienAndDetail.setNama(nameEditText.getText().toString());
            pasienAndDetail.setTanggal_lahir(tanggalLahirEditText.getText().toString());
            pasienAndDetail.setUmur(Integer.parseInt(umurEditText.getText().toString()));
            pasienAndDetail.setTekanan_darah(tekananDarahEditText.getText().toString());
            String editGender = "";

            if (genderSpinner.getSelectedItemPosition() == 0)
                editGender = "Laki-Laki";
            else
                editGender = "Perempuan";

            pasienAndDetail.setGender(editGender);
            pasienAndDetail.setSuhu(suhuEditText.getText().toString());
            pasienAndDetail.setTb_bb(tbbbEditText.getText().toString());
            pasienAndDetail.setKeluhan(keluhanEditText.getText().toString());
            pasienAndDetail.setDiagnosa(diagnosaEditText.getText().toString());
            pasienAndDetail.setStatus_penyakit(statusEditText.getText().toString());
            pasienAndDetail.setTindakan(tindakanEditText.getText().toString());
            pasienAndDetail.setGds(gdsEditText.getText().toString());
            pasienAndDetail.setUric_acid(uricAcidEditText.getText().toString());
            pasienAndDetail.setKolesterol(cholesterolEditText.getText().toString());
            pasienAndDetail.setRujukan(rujukanEditText.getText().toString());

            pasienPresenter.submitEditData(pasienAndDetail);
        }
        else{
            showError("Please Check Your Data Before Submit");
        }
    }

    @Override
    public void reloadDetailPasien() {
        Intent intent = new Intent();
        intent.putExtra("pasienId", getPasienId());
        intent.putExtra("groupId", getGroupId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.edit.EditPasienActivity");
        startActivity(intent);
    }
}
