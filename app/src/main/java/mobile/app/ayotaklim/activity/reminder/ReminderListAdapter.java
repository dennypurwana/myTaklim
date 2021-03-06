package mobile.app.ayotaklim.activity.reminder;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.reminder.Reminder;
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
        FormatTanggalIDN formatTanggalIDN = new FormatTanggalIDN();
        final String dateDiff =String.valueOf(formatTanggalIDN.dateDiff(dataList.get(position).getEventDate()));
        holder.txtTimeReminder.setText(dateDiff + " hari lagi");
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(dataList.get(position), dateDiff);
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
        void onItemClick(Reminder reminder, String dateDiff);
    }
}
