package mobile.app.ayotaklim.activity.event;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.models.event.Event;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder> {


    private ArrayList<Event> dataList;
    private OnItemClickListener listener;

    public EventListAdapter(ArrayList<Event> dataList,OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_items, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, final int position) {
        holder.txtEventTime.setText(dataList.get(position).getStartTime());
        holder.txtEventTitle.setText(dataList.get(position).getTitle());
        holder.txtEventVenue.setText(dataList.get(position).getVenue());
        holder.txtEventAddress.setText(dataList.get(position).getVenueAddress());
        holder.txtEventDate.setText(dataList.get(position).getDate());
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

    public class EventListViewHolder extends RecyclerView.ViewHolder{
        private TextView txtEventTitle, txtEventAddress, txtEventVenue, txtEventDate, txtEventTime;
        private CardView card;
        public EventListViewHolder(View itemView) {
            super(itemView);
            txtEventTitle = (TextView) itemView.findViewById(R.id.titleEvent);
            txtEventAddress = (TextView) itemView.findViewById(R.id.venueAddress);
            txtEventVenue = (TextView) itemView.findViewById(R.id.venue);
            txtEventDate = (TextView) itemView.findViewById(R.id.eventDate);
            txtEventTime = (TextView) itemView.findViewById(R.id.eventTime);
            card =(CardView) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
}
