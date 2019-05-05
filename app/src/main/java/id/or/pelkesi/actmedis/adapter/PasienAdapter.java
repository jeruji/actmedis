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
import id.or.pelkesi.actmedis.model.PasienAndDetail;

public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.PasienHolder>{

    private List<PasienAndDetail> listPasien;
    private PasienAdapterCallback mCallback;

    @Inject
    public PasienAdapter() {
        listPasien = new ArrayList<>();
    }

    public void setPasien(List<PasienAndDetail> listPasien) {
        this.listPasien = new ArrayList<>();
        this.listPasien.addAll(listPasien);
    }

    @Override
    public PasienHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_pasien,
                parent, false);
        return new PasienHolder(view);
    }

    @Override
    public void onBindViewHolder(PasienHolder holder, int position) {
        PasienAndDetail patient = listPasien.get(position);
        holder.setPasien(patient);
        holder.pasienNama.setText(patient.getNama());
        holder.pasienGender.setText(patient.getGender());
        holder.pasienUmur.setText(patient.getUmur().toString());
        holder.pasienTanggalLahir.setText(patient.getTanggal_lahir());
        holder.pasienDateCreated.setText(patient.getDatecreated().toString());
    }

    public void setCallback(PasienAdapterCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    class PasienHolder extends RecyclerView.ViewHolder {

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

        PasienAndDetail patient;

        public PasienHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPasien(PasienAndDetail patient) {
            this.patient = patient;
        }

        @OnClick(R.id.row_layout)
        void onItemClicked(View view) {
            if (mCallback != null) mCallback.onPasienClicked(patient);
        }

    }

    public interface PasienAdapterCallback {
        void onPasienClicked(PasienAndDetail patient);
    }
}
