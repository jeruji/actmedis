package id.or.pelkesi.actmedis.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.model.Patient;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private List<Patient> listPasien;
    private SearchAdapterCallback mCallback;

    @Inject
    public SearchAdapter() {
        listPasien = new ArrayList<>();
    }

    public void setPasien(List<Patient> listPasien) {
        this.listPasien = new ArrayList<>();
        this.listPasien.addAll(listPasien);
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_pasien,
                parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        Patient patient = listPasien.get(position);
        holder.setPasien(patient);
        holder.pasienNama.setText(patient.getNama());
        holder.pasienGender.setText(patient.getGender());
        holder.pasienUmur.setText(patient.getUmur().toString());
        holder.pasienTanggalLahir.setText(patient.getTanggal_lahir());
        holder.pasienDateCreated.setText(patient.getDatecreated().toString());
    }

    public void setCallback(SearchAdapterCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    class SearchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pasienNama)
        TextView pasienNama;
        @BindView(R.id.genderPasien)
        TextView pasienGender;
        @BindView(R.id.umurPasien)
        TextView pasienUmur;
        @BindView(R.id.tanggalLahirPasien)
        TextView pasienTanggalLahir;
        @BindView(R.id.dateCreatedPasien)
        TextView pasienDateCreated;

        Patient patient;

        public SearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPasien(Patient patient) {
            this.patient = patient;
        }

        @OnClick(R.id.row_layout)
        void onItemClicked(View view) {
            if (mCallback != null) mCallback.onPasienClicked(patient);
        }

    }

    public interface SearchAdapterCallback {
        void onPasienClicked(Patient patient);
    }

}
