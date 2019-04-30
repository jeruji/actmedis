package id.or.pelkesi.actmedis.model;

import java.util.Date;

import lombok.Data;

@Data
public class HarianGroup {

    String desa;
    String dusun;
    String kabupaten;
    String kecamatan;
    String puskesmas;
    Date tanggal;

}
