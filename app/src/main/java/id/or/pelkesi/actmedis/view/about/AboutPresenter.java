package id.or.pelkesi.actmedis.view.about;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class AboutPresenter implements AboutActivityInterface.Presenter{

    AboutActivityInterface.View view;
    FirebaseFirestore db;
    Context context;

    @Inject
    public AboutPresenter(FirebaseFirestore db, AboutActivityInterface.View view, Context context){

        this.db = db;
        this.view = view;
        this.context = context;
    }

}
