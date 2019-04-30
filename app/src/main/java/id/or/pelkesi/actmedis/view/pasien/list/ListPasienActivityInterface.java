package id.or.pelkesi.actmedis.view.pasien.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.PasienAndDetail;
import id.or.pelkesi.actmedis.model.Patient;

public interface ListPasienActivityInterface {

    interface View {
        void toPasienForm();
        void showProgress(boolean show);
        void showPasien();
        void createPatientList(PasienAndDetail patientListToBeAdd);
        void showError(String message);
        void setHarianGroups(List<HarianGroup> harianGroups);
        void constructKabupaten(ArrayList<String> kabupatenList);
        void constructKecamatan(ArrayList<String> kecamatanList);
        void constructDesa(ArrayList<String> desaList);
        void constructDusun(ArrayList<String> dusunList);
        void constructPuskesmas(ArrayList<String> puskesmasList);
        Date getSelectedDate();
        Date getSelectedDatePlusOne(Date selectedDate);
    }

    interface Presenter {
        void getGroupingList(String puskesmas, String dusun, String desa, String kecamatan, String kabupaten);
        void getPasienDetail(Patient patient);
        int[] getDate();
        void getKabupatenList();
        void getKecamatanList(String kabupaten);
        void getDesaList(String kecamatan, String kabupaten);
        void getDusunList(String desa, String kecamatan, String kabupaten);
        void getPuskesmasList(String dusun, String desa, String kecamatan, String kabupaten);
    }

}
