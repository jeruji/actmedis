package id.or.pelkesi.actmedis.model;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

import lombok.Data;

@Data
public class Patient {
    @Exclude
    String id;
    String nama;
    Integer umur;
    String gender;
    String foto;
    Date datecreated;
    String tanggal_lahir;
}
