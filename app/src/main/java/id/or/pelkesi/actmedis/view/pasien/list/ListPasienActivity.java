package id.or.pelkesi.actmedis.view.pasien.list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.adapter.PasienAdapter;
import id.or.pelkesi.actmedis.data.component.DaggerListPasienActivityComponent;
import id.or.pelkesi.actmedis.data.module.ListPasienActivityModule;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.PasienAndDetail;

public class ListPasienActivity extends AppCompatActivity implements ListPasienActivityInterface.View, PasienAdapter.PasienAdapterCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbarList;

    @BindView(R.id.edTextDate)
    EditText textDate;

    @BindView(R.id.spinnerKabupaten)
    Spinner kabupatenSpinner;

    @BindView(R.id.spinnerKecamatan)
    Spinner kecamatanSpinner;

    @BindView(R.id.spinnerDesa)
    Spinner desaSpinner;

    @BindView(R.id.spinnerDusun)
    Spinner dusunSpinner;

    @BindView(R.id.spinnerPuskesmas)
    Spinner puskesmasSpinner;

    @BindView(R.id.recycler_view_pasien)
    RecyclerView mRecyclerView;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    List<HarianGroup> harianGroups;

    @Inject
    ListPasienPresenter listPasienPresenter;

    @Inject
    PasienAdapter pasienAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    List<PasienAndDetail> patientList;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarList);
        getSupportActionBar().setTitle("Pasien");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerListPasienActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .listPasienActivityModule(new ListPasienActivityModule(this, this))
                .build().inject(this);

        showProgress(false);
        pasienAdapter.setCallback(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(pasienAdapter);
        listPasienPresenter.getKabupatenList();
    }

    @Override
    public void showProgress(boolean show) {
        if (show && pasienAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void toPasienForm() {

    }

    @Override
    public void showPasien() {
        mRecyclerView.setVisibility(View.VISIBLE);
        pasienAdapter.setPasien(patientList);
        pasienAdapter.notifyDataSetChanged();
        showProgress(false);
    }

    @Override
    public void createPatientList(PasienAndDetail patientListToBeAdd){
        patientList.add(patientListToBeAdd);
        showPasien();
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
    public void setHarianGroups(List<HarianGroup> harianGroups){
        this.harianGroups = harianGroups;
    }

    @OnClick(R.id.calender)
    public void showCalendar() {
        calendarDialog(99).show();
    }

    private Dialog calendarDialog(int id) {
        if (id == 99) {
            int[] dateArr = listPasienPresenter.getDate();
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

        textDate.setText(dateStr);
    }

    @Override
    public void constructKabupaten(ArrayList<String> kabupatenList) {
        ArrayAdapter<String> adapterKabupaten = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kabupatenList);
        adapterKabupaten.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kabupatenSpinner.setAdapter(adapterKabupaten);
    }

    @OnItemSelected(R.id.spinnerKabupaten)
    public void kabupatenSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Kabupaten --")&&
                !textDate.getText().toString().equals("")){
            listPasienPresenter.getKecamatanList(spinner.getSelectedItem().toString());
        }
        else if(!spinner.getSelectedItem().toString().equals("-- Pilih Kabupaten --")&&
                textDate.getText().toString().equals("")){
            showError("Please Select Date");
            spinner.setSelection(0);
        }
    }

    @Override
    public void constructKecamatan(ArrayList<String> kecamatanList) {
        ArrayAdapter<String> adapterKecamatan = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, kecamatanList);
        adapterKecamatan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kecamatanSpinner.setAdapter(adapterKecamatan);
    }

    @OnItemSelected(R.id.spinnerKecamatan)
    public void kecamatanSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Kecamatan --")){
            listPasienPresenter.getDesaList(spinner.getSelectedItem().toString(),
                    kabupatenSpinner.getSelectedItem().toString());
        }
    }

    @Override
    public void constructDesa(ArrayList<String> desaList) {
        ArrayAdapter<String> adapterDesa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, desaList);
        adapterDesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        desaSpinner.setAdapter(adapterDesa);
    }

    @OnItemSelected(R.id.spinnerDesa)
    public void desaSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Desa --")){
            listPasienPresenter.getDusunList(spinner.getSelectedItem().toString(),
                    kecamatanSpinner.getSelectedItem().toString(),
                    kabupatenSpinner.getSelectedItem().toString());
        }
    }

    @Override
    public void constructDusun(ArrayList<String> dusunList) {
        ArrayAdapter<String> adapterDusun = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, dusunList);
        adapterDusun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dusunSpinner.setAdapter(adapterDusun);
    }

    @OnItemSelected(R.id.spinnerDusun)
    public void dusunSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Dusun --")){
            listPasienPresenter.getPuskesmasList(spinner.getSelectedItem().toString(),
                    desaSpinner.getSelectedItem().toString(),
                    kecamatanSpinner.getSelectedItem().toString(),
                    kabupatenSpinner.getSelectedItem().toString());
        }
    }

    @Override
    public void constructPuskesmas(ArrayList<String> puskesmasList) {
        ArrayAdapter<String> adapterPuskesmas = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, puskesmasList);
        adapterPuskesmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puskesmasSpinner.setAdapter(adapterPuskesmas);
    }

    @OnItemSelected(R.id.spinnerPuskesmas)
    public void puskesmasSpinnerItemSelected(Spinner spinner, int position){
        if(!spinner.getSelectedItem().toString().equals("-- Pilih Puskesmas --")){
            patientList = new ArrayList<>();
            listPasienPresenter.getGroupingList(spinner.getSelectedItem().toString(),
                    dusunSpinner.getSelectedItem().toString(),
                    desaSpinner.getSelectedItem().toString(),
                    kecamatanSpinner.getSelectedItem().toString(),
                    kabupatenSpinner.getSelectedItem().toString());
        }
    }

    @Override
    public Date getSelectedDate(){

        String selectedDateString = textDate.getText().toString();
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

    public Date getSelectedDatePlusOne(Date selectedDate){
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPasienClicked(PasienAndDetail patient) {
        Intent intent = new Intent();
        intent.putExtra("pasienId", patient.getId());
        intent.putExtra("groupId", patient.getGroup_id());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.edit.EditPasienActivity");
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    public void toAddPatientActivity(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.pasien.add.AddPasienActivity");
        startActivity(intent);
    }
}
