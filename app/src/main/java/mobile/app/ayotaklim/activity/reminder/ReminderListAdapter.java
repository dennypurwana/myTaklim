package mobile.app.ayotaklim.activity.reminder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.venue.VenueListAdapter;
import mobile.app.ayotaklim.models.reminder.Reminder;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class ReminderListAdapter  extends RecyclerView.Adapter<ReminderListAdapter.ReminderListViewHolder> {


    private ArrayList<Reminder> dataList;
    private ReminderListAdapter.OnItemClickListener listener;
    private FormatTanggalIDN formatTanggalIDN;

    public ReminderListAdapter(ArrayList<Reminder> dataList, ReminderListAdapter.OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public ReminderListAdapter.ReminderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reminder_items, parent, false);
        return new ReminderListAdapter.ReminderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderListAdapter.ReminderListViewHolder holder, final int position) {
        formatTanggalIDN = new FormatTanggalIDN();
        holder.txtEvent.setText(dataList.get(position).getEventName());
        holder.txtEventAddress.setText(dataList.get(position).getEventLocation()+"\n"+dataList.get(position).getEventAddress());
        holder.txtEventDateTime.setText(formatTanggalIDN.formatDate(dataList.get(position).getEventDate()));
        holder.txtTimeReminder.setText("1 Hari ");
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(dataList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ReminderListViewHolder extends RecyclerView.ViewHolder{
        private TextView txtEvent, txtEventAddress, txtEventDateTime, txtTimeReminder;
        private RelativeLayout card;
        public ReminderListViewHolder(View itemView) {
            super(itemView);
            txtEvent = (TextView) itemView.findViewById(R.id.eventName);
            txtEventAddress = (TextView) itemView.findViewById(R.id.eventLocation);
            txtEventDateTime = itemView.findViewById(R.id.eventDateTime);
            txtTimeReminder = itemView.findViewById(R.id.eventReminderTime);
            card=(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Reminder reminder);
    }
}
