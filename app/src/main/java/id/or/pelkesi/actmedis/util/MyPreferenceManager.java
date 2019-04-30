package id.or.pelkesi.actmedis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import id.or.pelkesi.actmedis.model.User;

public class MyPreferenceManager {

    private static final String PREF_NAME = "act_medis";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_REGION = "region";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_FIREBASE_TOKEN = "firebase_token";
    private static final String KEY_NOTIFICATIONS = "notifications";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private String TAG = MyPreferenceManager.class.getSimpleName();

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getUser_id());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_REGION, user.getRegion());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. user id : " + user.getUser_id() + " , " + user.getName() + ", " + user.getEmail() + " , " + getFirebaseToken());
    }


    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email, password, region, phone, province, city, address, usertype, firebaseToken, lawfirm, lawfirmcity, lawfirmaddress, lawfirmphone, specialization;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_NAME, null);
            email = pref.getString(KEY_EMAIL, null);
            firebaseToken = pref.getString(KEY_USER_FIREBASE_TOKEN, null);
            password = pref.getString(KEY_PASSWORD, null);
            region = pref.getString(KEY_REGION, null);
            User user = new User();
            user.setUser_id(id);
            user.setName(name);
            user.setEmail(email);
            user.setRegion(region);

            return user;
        }
        return null;
    }

    public String getUserId() {
        String userId = pref.getString(KEY_USER_ID, null);
        Log.d("User Id", userId);
        return userId;
    }

    public String getUserName() {
        String name = pref.getString(KEY_NAME, null);
        return name;
    }

    public String getUserEmail() {
        String email = pref.getString(KEY_EMAIL, null);
        return email;
    }

    public String getUserPassword() {
        String password = pref.getString(KEY_PASSWORD, null);
        return password;
    }

    public String getUserRegion() {
        String region = pref.getString(KEY_REGION, null);
        return region;
    }

    public void addNotification(String notification) {
        String oldNotifications = getNotifications();
        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void addTokenFirebase(String token) {
        editor.putString(KEY_USER_FIREBASE_TOKEN, token);
        editor.commit();
    }

    public String getFirebaseToken() {
        return pref.getString(KEY_USER_FIREBASE_TOKEN, null);
    }

    public void deleteToken() {
        pref.edit().remove(KEY_USER_FIREBASE_TOKEN).commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
