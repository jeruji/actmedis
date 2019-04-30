package id.or.pelkesi.actmedis.data.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import id.or.pelkesi.actmedis.util.CustomScope;
import id.or.pelkesi.actmedis.view.pasien.edit.EditPasienActivityInterface;

@Module
public class EditPasienActivityModule {

    private final EditPasienActivityInterface.View mView;
    private final Context mContext;

    public EditPasienActivityModule(EditPasienActivityInterface.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Provides
    @CustomScope
    EditPasienActivityInterface.View providesEditPasienActivityInterfaceView() { return mView; }

    @Provides
    @CustomScope
    Context providesEditPasienActivityContext() { return mContext; }

}
