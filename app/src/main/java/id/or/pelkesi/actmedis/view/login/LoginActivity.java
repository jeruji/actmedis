package id.or.pelkesi.actmedis.view.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.data.component.DaggerLoginActivityComponent;
import id.or.pelkesi.actmedis.data.module.LoginActivityModule;
import id.or.pelkesi.actmedis.model.User;
import id.or.pelkesi.actmedis.view.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity implements LoginActivityInterface.View {

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.titleProgress)
    TextView titleProgressBar;

    @BindView(R.id.edTextEmail)
    EditText email;

    @BindView(R.id.edTextPassword)
    EditText password;

    @BindView(R.id.visibility)
    ImageView _showPassword;

    @BindView(R.id.invisibility)
    ImageView _hidePassword;

    @Inject
    LoginPresenter loginPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        checkSession();

        DaggerLoginActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .loginActivityModule(new LoginActivityModule(this, this))
                .build().inject(this);
    }

    @Override
    @OnClick(R.id.btnLogin)
    public void submitLoginEmailWithPassword() {

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        int statusValidation = loginPresenter.checkValidation(emailStr, passwordStr);
        if (statusValidation == 1) {
            email.setError("Please correct your email address");
        } else if (statusValidation == 2) {
            password.setError("Please correct your password");
        } else
            loginPresenter.submitLoginByEmailPassword(emailStr, passwordStr);

    }

    @Override
    public void submitResult(boolean result) {
        if (!result) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Login Failed, Please Check Your Email & Password");
            dlgAlert.setTitle("Failed");
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    });
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        }
    }

    @Override
    public void showProgressBar(String message) {
        progressBar.setVisibility(View.VISIBLE);
        titleProgressBar.setVisibility(View.VISIBLE);
        titleProgressBar.setText("Sedang login..");
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        titleProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void toDashboardPage(User user) {
        Intent intent = new Intent();
        intent.setClassName(this, "id.or.pelkesi.actmedis.view.dashboard.DashboardActivity");
        App.getInstance().getPrefManager().storeUser(user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void checkSession() {
        if (App.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    @Override
    @OnClick(R.id.visibility)
    public void showPassword() {
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        _showPassword.setVisibility(View.GONE);
        _hidePassword.setVisibility(View.VISIBLE);
    }

    @Override
    @OnClick(R.id.invisibility)
    public void hidePassword() {
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        _showPassword.setVisibility(View.VISIBLE);
        _hidePassword.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
