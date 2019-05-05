package id.or.pelkesi.actmedis.view.pasien.search;

import java.util.List;

import id.or.pelkesi.actmedis.model.Patient;

public interface SearchPasienActivityInterface {

    interface View {
        void showError(String message);
        void showPatient();
        void createPatientList(List<Patient> patientListToBeAdd);
        void showProgress(boolean show);
    }

    interface Presenter {
        void searchPatient(String nama);
    }

}
