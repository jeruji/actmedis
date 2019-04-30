package id.or.pelkesi.actmedis.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import id.or.pelkesi.actmedis.model.Patient;

public class NameAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<Patient> mData;
    FirebaseFirestore db;

    public NameAutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource){
        super(context, resource);
        this.mData = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int index) {
        return mData.get(index).getNama();
    }

    public String getId(int index) {
        return mData.get(index).getId();
    }

    public Patient getPatient(int index) {
        return mData.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                final List<Patient> suggestions = new ArrayList<>();
                if (constraint != null) {
                    String strSearch = constraint.toString();
                    int strLength = strSearch.length();
                    String candidates = "abcdefghijklmnopqrstuvwxyz";
                    String strFront = strSearch.substring(0,strLength-1);
                    String strEndCode = strSearch.substring(strLength-1, strLength);
                    String searchCompare = strFront+candidates.charAt(candidates.indexOf(strEndCode)+1);
                    db.collection("data-pasien").whereGreaterThanOrEqualTo("nama", strSearch)
                            .whereLessThan("nama",searchCompare)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Patient> patientList = queryDocumentSnapshots.toObjects(Patient.class);
                            for(int index = 0; index<patientList.size(); index++){
                                patientList.get(index).setId(queryDocumentSnapshots.getDocuments().get(index).getId());
                            }
                            suggestions.addAll(patientList);
                            results.values = suggestions;
                            results.count = suggestions.size();
                            mData = suggestions;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getContext());
                            dlgAlert.setMessage("Failed Retrieve Data");
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
                    });
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }
}
