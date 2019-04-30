package id.or.pelkesi.actmedis.view.pasien.add;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import id.or.pelkesi.actmedis.model.Patient;

public interface AddPasienActivityInterface {

    interface View {
        void constructKabupaten(ArrayList<String> kabupatenList);
        void constructKecamatan(ArrayList<String> kecamatanList);
        void constructDesa(ArrayList<String> desaList);
        void constructDusun(ArrayList<String> dusunList);
        void constructPuskesmas(ArrayList<String> puskesmasList);
        Date getSelectedDate();
        Date getSelectedDatePlusOne(Date selectedDate);
        void showError(String message);
        void showExistingPatientData(Patient patient);
        void toListPatient();
        File getImageFile();
    }

    interface Presenter {
        int[] getDate();
        void getKabupatenList();
        void getKecamatanList(String kabupaten);
        void getDesaList(String kecamatan, String kabupaten);
        void getDusunList(String desa, String kecamatan, String kabupaten);
        void getPuskesmasList(String dusun, String desa, String kecamatan, String kabupaten);
        void getGroupingList(String puskesmas, String dusun, String desa, String kecamatan, String kabupaten);
        void setPatientSelectedId(Patient patient);
        void submitImagePatient(File image, String imgName);
        String[] getPatientAndGroupId();
        void submitPatientData(Map<String, Object> detailPasien);
        void submitPatientData(Map<String, Object> detailPasien, Map<String, Object> harianGroup);
        void submitPatientDataByGroup(Map<String, Object> detailPasien, Map<String, Object> patient);
        void submitPatientData(Map<String, Object> detailPasien, Map<String, Object> harianGroup, Map<String, Object> patient);
    }

}
