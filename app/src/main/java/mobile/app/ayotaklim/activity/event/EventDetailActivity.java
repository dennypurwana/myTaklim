package mobile.app.ayotaklim.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;

public class EventDetailActivity  extends AppCompatActivity {

    Event event;
    AppCompatButton applyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        event= (Event) getIntent().getSerializableExtra("Event");
        Toast.makeText(getApplicationContext(),"Event : "+event.getTitle(),Toast.LENGTH_LONG).show();

      applyButton = (AppCompatButton)  findViewById(R.id.btnApply);
      applyButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(EventDetailActivity.this,RegisterEventActivity.class);
              intent.putExtra("Event", event);
              startActivity(intent);
          }
      });

    }


}
