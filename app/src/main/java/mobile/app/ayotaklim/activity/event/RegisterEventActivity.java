package mobile.app.ayotaklim.activity.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;

public class RegisterEventActivity extends AppCompatActivity {

    AppCompatButton btnSubmit;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);
        btnSubmit = findViewById(R.id.btnSubmit);
        context=RegisterEventActivity.this;

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_LONG).show();
                dialogMessage();
            }
        });

    }

    private void dialogMessage() {
        new AlertDialog.Builder(context,R.style.CustomDialogTheme)
                .setTitle("Daftar Sukses")
                .setMessage("Anda telah berhasil mendaftar pada event ini. Silahkan lihat menu reminder pada menu aplikasi.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterEventActivity.this, EventActivity.class));
                    }
                })

                .show();

    }
}
