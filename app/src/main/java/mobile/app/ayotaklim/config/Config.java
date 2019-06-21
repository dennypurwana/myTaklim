package mobile.app.ayotaklim.config;

public class Config {
//http://157.119.220.138/codeless-api/api/TemplateData/keluaran/75.json
    public static String    BASE_URL              = "http://157.119.220.138/codeless-api/",
                            IMAGE_URL             =  BASE_URL + "webroot/files/upload/",
                            GET_VENUE             =  BASE_URL+  "rest/public/getall/11.json?limit=10000",
                            ADD_VENUE             =  BASE_URL+  "rest/public/create/11.json",
                            EDIT_VENUE            =  BASE_URL+  "rest/public/update/11.json",
                            UPLOAD_FILE           =  BASE_URL+  "api/form/uploadfile",
                            DELETE_VENUE          =  BASE_URL+  "rest/public/delete/11.json",
                            GET_EVENT             =  BASE_URL+  "rest/public/getall/14.json?limit=10000",
                            GET_EVENT_PEMATERI    =  BASE_URL+  "rest/public/getall/16.json?limit=10000",
                            GET_USTADZ            =  BASE_URL+  "rest/public/getall/13.json?limit=10000",
                            GET_USTADZ_BY_ID      =  BASE_URL+  "rest/public/get/13.json?limit=10000",
                            GET_VENUE_BY_ID       =  BASE_URL+  "rest/public/get/11.json?limit=10000",
                            ADD_USTADZ            =  BASE_URL+  "rest/public/create/13.json",
                            EDIT_USTADZ           =  BASE_URL+  "rest/public/update/13.json",
                            DELETE_USTADZ         =  BASE_URL+  "rest/public/delete/13.json",
                            ADD_EVENT             =  BASE_URL+  "rest/public/create/14.json",
                            EDIT_EVENT            =  BASE_URL+  "rest/public/update/14.json",
                            ADD_EVENT_JADWAL      =  BASE_URL+  "rest/public/create/16.json",
                            ADD_EVENT_PEMBICARA   =  BASE_URL+  "rest/public/create/15.json",
                            REGISTER_MEMBER       =  BASE_URL+  "rest/public/create/17.json",
                            REGISTER_EVENT_MEMBER =  BASE_URL+  "rest/public/create/18.json",
                            CHECK_EVENT_MEMBER    =  BASE_URL+  "rest/public/getall/18.json?limit=10000",
                            CHECK_EXISTING_MEMBER =  BASE_URL+  "rest/public/getall/17.json?limit=10000",
                            CREATE_DONASI         =  BASE_URL+  "rest/public/create/19.json",
                            GET_DONASI            =  BASE_URL+  "rest/public/getall/19.json",
                            GET_REMINDER          =  BASE_URL+  "api/TemplateData/keluaran/78.json?limit=10000",
                            GET_DATA_EVENT_HOME   =  BASE_URL+  "api/TemplateData/keluaran/75.json?limit=10000",
                            GET_DATA_EVENT_JADWAL =  BASE_URL+  "api/TemplateData/keluaran/77.json?limit=10000",
                            AUTH_LOGIN            =  BASE_URL + "rest/RestAuth/login",
                            LOGIN_ADMIN           =  BASE_URL + "api/pengguna/token";

}
