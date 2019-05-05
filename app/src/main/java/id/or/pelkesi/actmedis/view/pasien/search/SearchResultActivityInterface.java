package id.or.pelkesi.actmedis.view.pasien.search;

import java.util.List;

import id.or.pelkesi.actmedis.model.DetailPasien;
import id.or.pelkesi.actmedis.model.HarianGroup;
import id.or.pelkesi.actmedis.model.Patient;

public interface SearchResultActivityInterface {

    interface View{
        void showPatientData(Patient patient);
        void showSearchResultList();
        void createSearchResultList(HarianGroup harianGroup, DetailPasien detailPasien);
        void showError(String message);
    }

    interface Presenter{
        void getGroupAndDetailSearch(String patientId, List<String> kabupaten);
        void getPatientData(String patientId);
    }

}
