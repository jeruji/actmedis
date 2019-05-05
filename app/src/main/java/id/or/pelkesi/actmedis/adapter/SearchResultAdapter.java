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
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.HarianGroup;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder>{
    private List<HarianGroup> listHarianGroup;
    private List<DetailPasien> listDetailPasien;

    @Inject
    public SearchResultAdapter() {
        listHarianGroup = new ArrayList<>();
        listDetailPasien = new ArrayList<>();
    }

    public void setResult(List<HarianGroup> listHarianGroup, List<DetailPasien> listDetailPasien) {
        this.listHarianGroup = new ArrayList<>();
        this.listHarianGroup.addAll(listHarianGroup);
        this.listDetailPasien = new ArrayList<>();
        this.listDetailPasien.addAll(listDetailPasien);
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_detail_pasien,
                parent, false);
        return new SearchResultHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        HarianGroup harianGroup = listHarianGroup.get(position);
        holder.setHarianGroup(harianGroup);
        holder.resultKecamatan.setText(harianGroup.getKecamatan());
        holder.resultDesa.setText(harianGroup.getDesa());
        holder.resultDusun.setText(harianGroup.getDusun());
        holder.resultPuskesmas.setText(harianGroup.getPuskesmas());
        holder.resultTanggal.setText(harianGroup.getTanggal().toString());

        DetailPasien detailPasien = listDetailPasien.get(position);
        holder.setDetailPasien(detailPasien);
        holder.resultTekananDarah.setText(detailPasien.getTekanan_darah());
        holder.resultSuhu.setText(detailPasien.getSuhu());
        holder.resultTBBB.setText(detailPasien.getTb_bb());
        holder.resultKeluhan.setText(detailPasien.getKeluhan());
        holder.resultDiagnosa.setText(detailPasien.getDiagnosa());
        holder.resultStatus.setText(detailPasien.getStatus_penyakit());
        holder.resultTindakan.setText(detailPasien.getTindakan());
        holder.resultGds.setText(detailPasien.getGds());
        holder.resultUricAcid.setText(detailPasien.getUric_acid());
        holder.resultKolesterol.setText(detailPasien.getKolesterol());
        holder.resultRujukan.setText(detailPasien.getRujukan());
    }

    @Override
    public int getItemCount() {
        return listHarianGroup.size();
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.kecamatanResultPasien)
        TextView resultKecamatan;
        @BindView(R.id.desaResultPasien)
        TextView resultDesa;
        @BindView(R.id.dusunResultPasien)
        TextView resultDusun;
        @BindView(R.id.puskesmasResultPasien)
        TextView resultPuskesmas;
        @BindView(R.id.tanggalResultPasien)
        TextView resultTanggal;

        @BindView(R.id.pasienResultTekananDarah)
        TextView resultTekananDarah;
        @BindView(R.id.suhuResultPasien)
        TextView resultSuhu;
        @BindView(R.id.tbbbResultPasien)
        TextView resultTBBB;
        @BindView(R.id.keluhanResultPasien)
        TextView resultKeluhan;
        @BindView(R.id.diagnosaResultPasien)
        TextView resultDiagnosa;
        @BindView(R.id.statusResultPasien)
        TextView resultStatus;
        @BindView(R.id.tindakanResultPasien)
        TextView resultTindakan;
        @BindView(R.id.gdsResultPasien)
        TextView resultGds;
        @BindView(R.id.uricAcidResultPasien)
        TextView resultUricAcid;
        @BindView(R.id.kolesterolResultPasien)
        TextView resultKolesterol;
        @BindView(R.id.rujukanResultPasien)
        TextView resultRujukan;

        HarianGroup harianGroup;
        DetailPasien detailPasien;

        public SearchResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setHarianGroup(HarianGroup harianGroup) {
            this.harianGroup = harianGroup;
        }

        public void setDetailPasien(DetailPasien detailPasien) {
            this.detailPasien = detailPasien;
        }

    }
}
