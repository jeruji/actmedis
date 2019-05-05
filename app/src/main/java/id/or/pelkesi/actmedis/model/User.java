package id.or.pelkesi.actmedis.model;

import java.util.List;

import lombok.Data;

@Data
public class User {
    String name;
    String email;
    List<String> region;
    String user_id;

}

