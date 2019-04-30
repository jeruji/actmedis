package id.or.pelkesi.actmedis.data.component;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Component;
import id.or.pelkesi.actmedis.data.module.AppModule;
import id.or.pelkesi.actmedis.data.module.NetModule;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    FirebaseFirestore firestore();
}
