package id.or.pelkesi.actmedis.view.pasien.add;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.adapter.NameAutoCompleteAdapter;
import id.or.pelkesi.actmedis.data.component.DaggerAddPasienActivityComponent;
import id.or.pelkesi.actmedis.data.module.AddPasienActivityModule;
import id.or.pelkesi.actmedis.model.Patient;

public class AddPasienActivity extends AppCompatActivity implements AddPasienActivityInterface.View {

    @BindView(R.id.edTextDateGroupAdd)
    EditText addGroupDateEd;
    @BindView(R.id.spinnerKabupatenGroupAdd)
    Spinner addKabupatenGroupSpinner;
    @BindView(R.id.spinnerKecamatanGroupAdd)
    Spinner addKecamatanGroupSpinner;
    @BindView(R.id.spinnerDesaGroupAdd)
    Spinner addDesaGroupSpinner;
    @BindView(R.id.edKecamatanAdd)
    EditText addKecamatanEd;
    @BindView(R.id.spinnerDusunGroupAdd)
    Spinner addDusunGroupSpinner;
    @BindView(R.id.spinnerPuskesmasGroupAdd)
    Spinner addPuskesmasGroupSpinner;
    @BindView(R.id.edDesaAdd)
    EditText addDesaEd;
    @BindView(R.id.edDusunAdd)
    EditText addDusunEd;
    @BindView(R.id.edPuskesmasAdd)
    EditText addPuskesmasEd;
    @BindView(R.id.edTextNameAdd)
    AutoCompleteTextView addNameAutoComplete;
    @BindView(R.id.edTextTanggalLahirAdd)
    EditText addTanggalLahirEd;
    @BindView(R.id.spinnerGenderAdd)
    Spinner addGenderSpinner;
    @BindView(R.id.imagePasienAdd)
    ImageView addImagePasien;
    @BindView(R.id.imgPathAdd)
    TextView addImgPath;
    @BindView(R.id.edDiagnosaAdd)
    EditText addDiagnosaEd;
    @BindView(R.id.edGdsAdd)
    EditText addGdsEd;
    @BindView(R.id.edKeluhanAdd)
    EditText addKeluhanEd;
    @BindView(R.id.edCholesterolAdd)
    EditText addCholesterolAdd;
    @BindView(R.id.edRujukanAdd)
    EditText addRujukanEd;
    @BindView(R.id.spinnerStatusAdd)
    Spinner addStatusSpinner;
    @BindView(R.id.edSuhuAdd)
    EditText addSuhuEd;
    @BindView(R.id.edTBBBAdd)
    EditText addTBBBEd;
    @BindView(R.id.edTekananDarahAdd)
    EditText addTekananDarahEd;
    @BindView(R.id.edTindakanAdd)
    EditText addTindakanEd;
    @BindView(R.id.edUricAcidAdd)
    EditText addUricAcidEd;

    @BindView(R.id.toolbar)
    Toolbar toolbarList;

    @BindView(R.id.btnSubmitAddPasien)
    Button submitAddPasien;

    @Inject
    AddPasienPresenter pasienPresenter;

    File fotoFile;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_add);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarList);
        getSupportActionBar().setTitle("Pasien");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerAddPasienActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .addPasienActivityModule(new AddPasienActivityModule(this, this))
                .build().inject(this);

        constructAutoComplete();
        pasienPresenter.getKabupatenList();
    }

    public void constructAutoComplete(){
        int layout = android.R.layout.simple_list_item_1;
        final NameAutoCompleteAdapter adapter = new NameAutoCompleteAdapter (this, layout);
        addNameAutoComplete.setAdapter(adapter);

        addNameAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                pasienPresenter.setPatientSelectedId(adapter.getPatient(position));
            }
        });
    }

    @OnClick(R.id.calenderGroupAdd)
    public void showCalendar() {
        calendarDialog(99).show();
    }

    private Dialog calendarDialog(int id) {
        if (id == 99) {
            int[] dateArr = pasienPresenter.getDate();
            return new DatePickerDialog(this, dateListener, dateArr[0], dateArr[1], dateArr[2]);
        }
        else if(id == 97) {
            int[] dateArr = pasienPresenter.getDate();
            return new DatePickerDialog(this, dateListenerTglLahir, dateArr[0], dateArr[1], dateArr[2]);
        }

        return null;
    }

    @OnClick(R.id.calendarTglLahirAdd)
    public void showCalendarTglLahir() { calendarDialog(97).show(); }

    private DatePickerDialog.OnDateSetListener dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener dateListenerTglLahir = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDateTglLahir(arg1, arg2+1, arg3);
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

        addGroupDateEd.setText(dateStr);
    }

    private void showDateTglLahir(int year, int month, int day) {
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

        addTanggalLahirEd.setText(dateStr);
    }

    @Override
    public void constructKabupaten(ArrayList<String> kabupatenList) {
        ArrayAdapter<String> adapterKabupaten = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kabupatenList);
        adapterKabupaten.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addKabupatenGroupSpinner.setAdapter(adapterKabupaten);
    }

    @Override
    public void constructKecamatan(ArrayList<String> kecamatanList) {
        ArrayAdapter<String> adapterKecamatan = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, kecamatanList);
        adapterKecamatan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addKecamatanGroupSpinner.setAdapter(adapterKecamatan);
    }

    @Override
    public void constructDesa(ArrayList<String> desaList) {
        ArrayAdapter<String> adapterDesa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, desaList);
        adapterDesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addDesaGroupSpinner.setAdapter(adapterDesa);
    }

    @Override
    public void constructDusun(ArrayList<String> dusunList) {
        ArrayAdapter<String> adapterDusun = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, dusunList);
        adapterDusun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addDusunGroupSpinner.setAdapter(adapterDusun);
    }

    @Override
    public void constructPuskesmas(ArrayList<String> puskesmasList) {
        ArrayAdapter<String> adapterPuskesmas = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, puskesmasList);
        adapterPuskesmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addPuskesmasGroupSpinner.setAdapter(adapterPuskesmas);
    }

    @Override
    public Date getSelectedDate() {
        String selectedDateString = addGroupDateEd.getText().toString();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date selectedDate = null;

        try {
            selectedDate = format.parse(selectedDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.add(Calendar.DATE, -1);
        Date beforeDate = null;

        try {
            beforeDate = format.parse(format.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return beforeDate;
    }

    @Override
    public Date getSelectedDatePlusOne(Date selectedDate) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.add(Calendar.DATE, 2);
        Date untilDate = null;

        try {
            untilDate = format.parse(format.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return untilDate;
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

    @OnItemSelected(R.id.spinnerKabupatenGroupAdd)
    public void kabupatenSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Kabupaten --")&&
                !addGroupDateEd.getText().toString().equals("")){
            pasienPresenter.getKecamatanList(spinner.getSelectedItem().toString());
        }
        else if(!spinner.getSelectedItem().toString().equals("-- Pilih Kabupaten --")&&
                addGroupDateEd.getText().toString().equals("")){
            showError("Please Select Date");
            spinner.setSelection(0);
        }
    }

    @OnItemSelected(R.id.spinnerKecamatanGroupAdd)
    public void kecamatanSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Kecamatan --")&&!spinner.getSelectedItem().toString().equals("-- Tambah Kecamatan --")){

            addKecamatanEd.setVisibility(View.GONE);
            addDesaEd.setVisibility(View.GONE);
            addDusunEd.setVisibility(View.GONE);
            addPuskesmasEd.setVisibility(View.GONE);

            pasienPresenter.getDesaList(spinner.getSelectedItem().toString());
        }
        else if(spinner.getSelectedItem().toString().equals("-- Tambah Kecamatan --")){
            addKecamatanEd.setVisibility(View.VISIBLE);
            addDesaEd.setVisibility(View.VISIBLE);
            addDusunEd.setVisibility(View.VISIBLE);
            addPuskesmasEd.setVisibility(View.VISIBLE);
        }
    }

    @OnItemSelected(R.id.spinnerDesaGroupAdd)
    public void desaSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Desa --")&&!spinner.getSelectedItem().toString().equals("-- Tambah Desa --")){

            addDesaEd.setVisibility(View.GONE);
            addDusunEd.setVisibility(View.GONE);
            addPuskesmasEd.setVisibility(View.GONE);

            pasienPresenter.getDusunList(spinner.getSelectedItem().toString());
        }
        else if(spinner.getSelectedItem().toString().equals("-- Tambah Desa --")){
            addDesaEd.setVisibility(View.VISIBLE);
            addDusunEd.setVisibility(View.VISIBLE);
            addPuskesmasEd.setVisibility(View.VISIBLE);
        }
    }

    @OnItemSelected(R.id.spinnerDusunGroupAdd)
    public void dusunSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Dusun --")&&!spinner.getSelectedItem().toString().equals("-- Tambah Dusun --")){

            addDusunEd.setVisibility(View.GONE);
            addPuskesmasEd.setVisibility(View.GONE);

            pasienPresenter.getPuskesmasList(spinner.getSelectedItem().toString());
        }
        else if(spinner.getSelectedItem().toString().equals("-- Tambah Dusun --")){
            addDusunEd.setVisibility(View.VISIBLE);
            addPuskesmasEd.setVisibility(View.VISIBLE);
        }
    }

    @OnItemSelected(R.id.spinnerPuskesmasGroupAdd)
    public void puskesmasSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Puskesmas --")&&!spinner.getSelectedItem().toString().equals("-- Tambah Puskesmas --")){

            addPuskesmasEd.setVisibility(View.GONE);

            pasienPresenter.getGroupingList(spinner.getSelectedItem().toString(),
                    addDusunGroupSpinner.getSelectedItem().toString(),
                    addDesaGroupSpinner.getSelectedItem().toString(),
                    addKecamatanGroupSpinner.getSelectedItem().toString(),
                    addKabupatenGroupSpinner.getSelectedItem().toString());
        }
        else if(spinner.getSelectedItem().toString().equals("-- Tambah Puskesmas --")){
            addPuskesmasEd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showExistingPatientData(Patient patient){
        addTanggalLahirEd.setText(patient.getTanggal_lahir());
        if(patient.getGender().equals("Laki-Laki"))
            addGenderSpinner.setSelection(0);
        else
            addGenderSpinner.setSelection(1);

        setImagePatient(patient.getFoto());

    }

    public void setImagePatient(String imagePath){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://actmedis-admin.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.getRoot().child(imagePath.substring(imagePath.lastIndexOf("/")+1)).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(addImagePasien);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError("Failed Retrieve Picture");
                    }
                });
    }

    @OnClick(R.id.btnUploadAdd)
    public void uploadFotoPasien(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.btnSubmitAddPasien)
    public void uploadDataPasien(){

        submitAddPasien.setClickable(false);

        boolean validate = true;

        if(addNameAutoComplete.getText().toString().equals("")||
                addTanggalLahirEd.getText().toString().equals("")||
                addGroupDateEd.getText().toString().equals("")||
                addDiagnosaEd.getText().toString().equals("")||
                addGdsEd.getText().toString().equals("")||
                addKeluhanEd.getText().toString().equals("")||
                addCholesterolAdd.getText().toString().equals("")||
                addRujukanEd.getText().toString().equals("")||
                addSuhuEd.getText().toString().equals("")||
                addTBBBEd.getText().toString().equals("")||
                addTekananDarahEd.getText().toString().equals("")||
                addTindakanEd.getText().toString().equals("")||
                addUricAcidEd.getText().toString().equals("")
                ){
            validate = false;
        }

        if(validate) {

            if (pasienPresenter.getPatientAndGroupId() != null) {

                String[] patientAndGroupId = pasienPresenter.getPatientAndGroupId();

                if (!patientAndGroupId[0].equals("") && !patientAndGroupId[1].equals("")) {
                    Map<String, Object> detailPasien = new HashMap<>();
                    detailPasien.put("diagnosa", addDiagnosaEd.getText().toString());
                    detailPasien.put("gds", addGdsEd.getText().toString());
                    detailPasien.put("group_id", patientAndGroupId[1]);
                    detailPasien.put("keluhan", addKeluhanEd.getText().toString());
                    detailPasien.put("kolesterol", addCholesterolAdd.getText().toString());
                    detailPasien.put("rujukan", addRujukanEd.getText().toString());
                    detailPasien.put("pasien_id", patientAndGroupId[0]);

                    if (addStatusSpinner.getSelectedItemPosition() == 0)
                        detailPasien.put("status_penyakit", "Menular");
                    else
                        detailPasien.put("status_penyakit", "Tidak Menular");

                    detailPasien.put("suhu", addSuhuEd.getText().toString());
                    detailPasien.put("tb_bb", addTBBBEd.getText().toString());
                    detailPasien.put("tekanan_darah", addTekananDarahEd.getText().toString());
                    detailPasien.put("tindakan", addTindakanEd.getText().toString());
                    detailPasien.put("uric_acid", addUricAcidEd.getText().toString());

                    pasienPresenter.submitPatientData(detailPasien);
                } else if (!patientAndGroupId[0].equals("") && patientAndGroupId[1].equals("")) {
                    Map<String, Object> detailPasien = new HashMap<>();
                    detailPasien.put("diagnosa", addDiagnosaEd.getText().toString());
                    detailPasien.put("gds", addGdsEd.getText().toString());
                    detailPasien.put("keluhan", addKeluhanEd.getText().toString());
                    detailPasien.put("kolesterol", addCholesterolAdd.getText().toString());
                    detailPasien.put("rujukan", addRujukanEd.getText().toString());
                    detailPasien.put("pasien_id", patientAndGroupId[0]);

                    if (addStatusSpinner.getSelectedItemPosition() == 0)
                        detailPasien.put("status_penyakit", "Menular");
                    else
                        detailPasien.put("status_penyakit", "Tidak Menular");

                    detailPasien.put("suhu", addSuhuEd.getText().toString());
                    detailPasien.put("tb_bb", addTBBBEd.getText().toString());
                    detailPasien.put("tekanan_darah", addTekananDarahEd.getText().toString());
                    detailPasien.put("tindakan", addTindakanEd.getText().toString());
                    detailPasien.put("uric_acid", addUricAcidEd.getText().toString());

                    String harianDateString = addGroupDateEd.getText().toString();
                    Date harianDate = null;
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                    try {
                        harianDate = format.parse(harianDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Map<String, Object> harianGroup = new HashMap<>();
                    harianGroup.put("tanggal", harianDate);
                    harianGroup.put("kabupaten", addKabupatenGroupSpinner.getSelectedItem().toString());
                    if (addKecamatanEd.getText().toString().equals(""))
                        harianGroup.put("kecamatan", addKecamatanGroupSpinner.getSelectedItem().toString());
                    else
                        harianGroup.put("kecamatan", addKecamatanEd.getText().toString());

                    if (addDesaEd.getText().toString().equals(""))
                        harianGroup.put("desa", addDesaGroupSpinner.getSelectedItem().toString());
                    else
                        harianGroup.put("desa", addDesaEd.getText().toString());

                    if (addDusunEd.getText().toString().equals(""))
                        harianGroup.put("dusun", addDusunGroupSpinner.getSelectedItem().toString());
                    else
                        harianGroup.put("dusun", addDusunEd.getText().toString());

                    if (addPuskesmasEd.getText().toString().equals(""))
                        harianGroup.put("puskesmas", addPuskesmasGroupSpinner.getSelectedItem().toString());
                    else
                        harianGroup.put("puskesmas", addPuskesmasEd.getText().toString());

                    if (!harianGroup.get("kecamatan").equals("")&&!harianGroup.get("desa").equals("")&&!harianGroup.get("dusun").equals("")&&
                            !harianGroup.get("puskesmas").equals(""))
                        pasienPresenter.submitPatientData(detailPasien, harianGroup);
                    else
                        showError("Please Check Your Data Before Submit");

                } else if (patientAndGroupId[0].equals("") && !patientAndGroupId[1].equals("")) {
                    Map<String, Object> detailPasien = new HashMap<>();
                    detailPasien.put("diagnosa", addDiagnosaEd.getText().toString());
                    detailPasien.put("gds", addGdsEd.getText().toString());
                    detailPasien.put("keluhan", addKeluhanEd.getText().toString());
                    detailPasien.put("kolesterol", addCholesterolAdd.getText().toString());
                    detailPasien.put("rujukan", addRujukanEd.getText().toString());
                    detailPasien.put("group_id", patientAndGroupId[1]);

                    if (addStatusSpinner.getSelectedItemPosition() == 0)
                        detailPasien.put("status_penyakit", "Menular");
                    else
                        detailPasien.put("status_penyakit", "Tidak Menular");

                    detailPasien.put("suhu", addSuhuEd.getText().toString());
                    detailPasien.put("tb_bb", addTBBBEd.getText().toString());
                    detailPasien.put("tekanan_darah", addTekananDarahEd.getText().toString());
                    detailPasien.put("tindakan", addTindakanEd.getText().toString());
                    detailPasien.put("uric_acid", addUricAcidEd.getText().toString());

                    Map<String, Object> patient = new HashMap<>();
                    patient.put("nama", addNameAutoComplete.getText().toString());
                    patient.put("tanggal_lahir", addTanggalLahirEd.getText().toString());

                    if (addGenderSpinner.getSelectedItemPosition() == 0)
                        patient.put("gender", "Laki-Laki");
                    else
                        patient.put("gender", "Perempuan");

                    patient.put("datecreated", new Date());

                    String birthDateString = addTanggalLahirEd.getText().toString();
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Date birthDate = null;

                    try {
                        birthDate = format.parse(birthDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    patient.put("umur", getAge(birthDate));

                    pasienPresenter.submitPatientDataByGroup(detailPasien, patient);
                }
            } else {
                Map<String, Object> patient = new HashMap<>();
                patient.put("nama", addNameAutoComplete.getText().toString());
                patient.put("tanggal_lahir", addTanggalLahirEd.getText().toString());

                if (addGenderSpinner.getSelectedItemPosition() == 0)
                    patient.put("gender", "Laki-Laki");
                else
                    patient.put("gender", "Perempuan");

                patient.put("datecreated", new Date());

                String birthDateString = addTanggalLahirEd.getText().toString();
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                Date birthDate = null;

                try {
                    birthDate = format.parse(birthDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                patient.put("umur", getAge(birthDate));

                String harianDateString = addGroupDateEd.getText().toString();
                Date harianDate = null;

                try {
                    harianDate = format.parse(harianDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Map<String, Object> harianGroup = new HashMap<>();
                harianGroup.put("tanggal", harianDate);
                harianGroup.put("kabupaten", addKabupatenGroupSpinner.getSelectedItem().toString());
                if (addKecamatanEd.getText().toString().equals(""))
                    harianGroup.put("kecamatan", addKecamatanGroupSpinner.getSelectedItem().toString());
                else
                    harianGroup.put("kecamatan", addKecamatanEd.getText().toString());

                if (addDesaEd.getText().toString().equals(""))
                    harianGroup.put("desa", addDesaGroupSpinner.getSelectedItem().toString());
                else
                    harianGroup.put("desa", addDesaEd.getText().toString());

                if (addDusunEd.getText().toString().equals(""))
                    harianGroup.put("dusun", addDusunGroupSpinner.getSelectedItem().toString());
                else
                    harianGroup.put("dusun", addDusunEd.getText().toString());

                if (addPuskesmasEd.getText().toString().equals(""))
                    harianGroup.put("puskesmas", addPuskesmasGroupSpinner.getSelectedItem().toString());
                else
                    harianGroup.put("puskesmas", addPuskesmasEd.getText().toString());

                Map<String, Object> detailPasien = new HashMap<>();
                detailPasien.put("diagnosa", addDiagnosaEd.getText().toString());
                detailPasien.put("gds", addGdsEd.getText().toString());
                detailPasien.put("keluhan", addKeluhanEd.getText().toString());
                detailPasien.put("kolesterol", addCholesterolAdd.getText().toString());
                detailPasien.put("rujukan", addRujukanEd.getText().toString());

                if (addStatusSpinner.getSelectedItemPosition() == 0)
                    detailPasien.put("status_penyakit", "Menular");
                else
                    detailPasien.put("status_penyakit", "Tidak Menular");

                detailPasien.put("suhu", addSuhuEd.getText().toString());
                detailPasien.put("tb_bb", addTBBBEd.getText().toString());
                detailPasien.put("tekanan_darah", addTekananDarahEd.getText().toString());
                detailPasien.put("tindakan", addTindakanEd.getText().toString());
                detailPasien.put("uric_acid", addUricAcidEd.getText().toString());

                if (!harianGroup.get("kecamatan").equals("")&&!harianGroup.get("desa").equals("")&&!harianGroup.get("dusun").equals("")&&
                        !harianGroup.get("puskesmas").equals(""))
                    pasienPresenter.submitPatientData(detailPasien, harianGroup, patient);
                else
                    showError("Please Check Your Data Before Submit");
            }
        }
        else{
            showError("Please Check Your Data Before Submit");
        }
    }

    public int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            showError("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            age--;
        }

        return age;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fotoFile = getBitmapFile(data);
            addImgPath.setText(fotoFile.getPath().toString());
            Picasso.get().load(fotoFile).into(addImagePasien);
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
    public void toListPatient(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.list.ListPasienActivity");
        startActivity(intent);
    }

    @Override
    public File getImageFile(){
        return fotoFile;
    }

}
