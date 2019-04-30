package id.or.pelkesi.actmedis.view.pasien.edit;

import java.io.File;

import id.or.pelkesi.actmedis.model.PasienAndDetail;

public interface EditPasienActivityInterface {
    interface View {
        void showError(String message);
        void constructEditData(PasienAndDetail pasienAndDetail);
        void showImage(File imgFile);
        void showProgress(boolean show);
        String getPasienId();
        String getGroupId();
        void reloadDetailPasien();
    }

    interface Presenter {
        void getPasienAndDetailData(String pasienId, String groupId);
        void submitImagePatient(File image, String imgName);
        int[] getDate();
        void submitEditData(PasienAndDetail pasienAndDetail);
    }
}
