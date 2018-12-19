package mobile.app.ayotaklim.config;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AYO_TAKLIM";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_URL = "url";
    public static final String TOKEN = "token";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(){
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();

    }
    public void logoutSession(){
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

    }

    public void createAccessToken(String token){
        editor.putString(TOKEN, token);
        editor.commit();

    }

   public  String getAksesToken(){
        return pref.getString(TOKEN, null);
   }

    public  boolean checkSession(){

        return pref.getBoolean(IS_LOGIN, false);
    }
}