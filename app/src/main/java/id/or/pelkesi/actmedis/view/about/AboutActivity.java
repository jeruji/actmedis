package id.or.pelkesi.actmedis.view.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.or.pelkesi.actmedis.App;
import id.or.pelkesi.actmedis.R;
import id.or.pelkesi.actmedis.data.component.DaggerAboutActivityComponent;
import id.or.pelkesi.actmedis.data.module.AboutActivityModule;

public class AboutActivity extends AppCompatActivity implements AboutActivityInterface.View {

    @BindView(R.id.aboutWebview)
    WebView webViewAbout;

    @BindView(R.id.toolbar)
    Toolbar toolbarAbout;

    @Inject
    AboutPresenter pasienPresenter;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarAbout);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DaggerAboutActivityComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .aboutActivityModule(new AboutActivityModule(this, this))
                .build().inject(this);

        setWebViewAbout();

    }

    public void setWebViewAbout(){
        String aboutHtml = "<h3 style=\"text-align:center;\">Tentang Aplikasi</h3>"+
                            "<div><p style=\"text-align:justify;\">Rekam Medis Pelkesi merupakan aplikasi milik Persekutuan Pelayanan Kristen untuk Kesehatan di Indonesia\n" +
                "           yang digunakan sebagai alat pencatatan medis korban bencana alam di beberapa daerah.</p> <br>Aplikasi ini hanya boleh digunakan oleh Pelkesi ataupun oleh pihak-pihak yang sudah diberikan ijin. \n" +
                "    </div>";

        webViewAbout.loadData(aboutHtml, "text/html", "UTF-8");
    }

}
