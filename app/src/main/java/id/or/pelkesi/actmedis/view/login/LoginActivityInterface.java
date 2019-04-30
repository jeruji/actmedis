package id.or.pelkesi.actmedis.view.login;

import id.or.pelkesi.actmedis.model.User;

public interface LoginActivityInterface {

    interface View{
        void submitLoginEmailWithPassword();
        void submitResult(boolean result);
        void showProgressBar(String message);
        void hideProgressBar();
        void toDashboardPage(User user);
        void showPassword();
        void hidePassword();
    }

    interface Presenter{
        int checkValidation(String email,String password);
        void submitLoginByEmailPassword(String email,String password);
    }

}
