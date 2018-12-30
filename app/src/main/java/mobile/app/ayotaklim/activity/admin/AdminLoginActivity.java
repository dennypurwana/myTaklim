package mobile.app.ayotaklim.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.EventActivity;
import mobile.app.ayotaklim.activity.splashscreen.SplashscreenActivity;
import mobile.app.ayotaklim.config.SessionManager;

public class AdminLoginActivity extends AppCompatActivity {
    SessionManager sessionManager;
    EditText username,password;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        sessionManager = new SessionManager(AdminLoginActivity.this);
        initView();
    }
    private void initView(){
        username = findViewById(R.id.edTextEmail);
        password = findViewById(R.id.edTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }
    private void submit(){
        if (username.getText().toString().equals("admin")&&password.getText().toString().equals("admin")){
            sessionManager.createAdminSession();
            Intent intent = new Intent(AdminLoginActivity.this, EventActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Username & Password harus di isi.",Toast.LENGTH_LONG).show();
        }
    }

}
