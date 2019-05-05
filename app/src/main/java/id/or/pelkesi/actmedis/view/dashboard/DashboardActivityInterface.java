package id.or.pelkesi.actmedis.view.dashboard;

public interface DashboardActivityInterface {

    interface View{
        void toPasienPage();
        void toFormPasien();
        void toLoginPage();
        void toAboutPage();
        void showProfile();
        void toSearchPage();
    }

    interface Presenter{
        void signOutUser();
    }

}
