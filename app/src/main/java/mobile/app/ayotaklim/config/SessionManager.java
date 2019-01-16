package mobile.app.ayotaklim.config;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AYO_TAKLIM";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_ADMIN = "IsAdmin";
    public static final String KEY_URL = "url";
    public static final String TOKEN = "token";
    public static final String TOKEN_UPLOAD = "token_upload";
    public static final String IS_MEMBER = "isMember";
    public static final String MEMBER_ID = "MemberId";
    public static final String MEMBER_PHONE = "MemberPhone";
    public static final String MEMBER_EMAIL = "MemberEmail";
    public static final String MEMBER_NAMA = "MemberNama";
    public static final String MEMBER_ALAMAT = "MemberAlamat";
    public static final String MEMBER_IMAGE = "MemberImage";
    public static final String MEMBER_KTP = "MemberKTP";


    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
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

    public void createAccessTokenUpload(String token){
        editor.putString(TOKEN_UPLOAD, token);
        editor.commit();

    }

    public void createMemberId(int id){
        editor.putInt(MEMBER_ID, id);
        editor.commit();

    }

    public void createMemberPhone(String phone){
        editor.putString(MEMBER_PHONE, phone);
        editor.commit();

    }

    public void createMemberEmail(String mail){
        editor.putString(MEMBER_EMAIL, mail);
        editor.commit();

    }

    public void createMemberSession(){
        editor.putBoolean(IS_MEMBER,true);
        editor.commit();

    }

    public void createAdminSession(){
        editor.putBoolean(IS_ADMIN,true);
        editor.commit();
    }

    public void logoutAdminSession(){
        editor.putBoolean(IS_ADMIN,false);
        editor.commit();
    }

    public  String getAksesToken(){
        return pref.getString(TOKEN, null);
   }


    public  String getTokenUpload(){
        return pref.getString(TOKEN, null);
    }
    public  int getMemberId(){
        return pref.getInt(MEMBER_ID, 0);
    }
    public  String getMemberPhone(){
        return pref.getString(MEMBER_PHONE, "");
    }
    public  String getMemberEmail(){
        return pref.getString(MEMBER_EMAIL, "");
    }
    public  boolean checkSession(){

        return pref.getBoolean(IS_LOGIN, false);
    }

    public  boolean checkMember(){

        return pref.getBoolean(IS_MEMBER, false);

    }

    public  boolean isAdmin(){
        return pref.getBoolean(IS_ADMIN, false);

    }
}