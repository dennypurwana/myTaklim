package mobile.app.ayotaklim.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.venue.Venue;

public class EventDetailActivity  extends AppCompatActivity {

    Event event;
    AppCompatButton applyButton;
    TextView eventName,
            eventTopic,
            eventPerformer,
            eventDate,
            eventDesc,
            eventVenue,
            eventVenueAddress,eventTime
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        eventName = findViewById(R.id.eventTitle);
        eventPerformer = findViewById(R.id.venueAddress);
        eventPerformer = findViewById(R.id.eventPerformer);
        eventDate = findViewById(R.id.eventDate);
        eventDesc =findViewById(R.id.eventDesc);
        eventVenue= findViewById(R.id.eventVenue);
        eventVenueAddress = findViewById(R.id.eventVenueAddress);
        eventTime = findViewById(R.id.eventTime);
        eventTopic=findViewById(R.id.eventTopic);

        event= (Event) getIntent().getSerializableExtra("Event");

        eventName.setText(event.getTitle());
        eventTopic.setText(event.getTopic());
        eventPerformer.setText(event.getPerformer());
        eventDate.setText(event.getDate());
        eventDesc.setText(event.getDescription());
        eventVenue.setText(event.getVenue());
        eventVenueAddress.setText(event.getVenueAddress());
        eventTime.setText(event.getStartTime()+"-"+event.getEndTime());

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
