package mobile.app.ayotaklim.activity.event;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.utils.ConvertImageBase64;
import mobile.app.ayotaklim.utils.FormatTanggalIDN;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder> {


    private ArrayList<Event> dataList;
    private OnItemClickListener listener;
    FormatTanggalIDN formatTanggalIDN;
    String flag;
    public EventListAdapter(String flag,ArrayList<Event> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
        this.flag= flag;
    }

    @Override
    public EventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_items, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventListViewHolder holder, final int position) {
        formatTanggalIDN=new FormatTanggalIDN();
        String eventDate;
        String pathImage="";
        holder.txtEventTitle.setText(dataList.get(position).getNamaEvent());
        String time = dataList.get(position).getJamMulai().replace(":",".")+" - "+dataList.get(position).getJamSelesai().replace(":",".");
        Log.d("TIME",time);
        if (dataList.get(position).getTglMulai().equalsIgnoreCase(dataList.get(position).getTglBerakhir())){
            eventDate = formatTanggalIDN.formatDate(dataList.get(position).getTglMulai());
        }else{
            eventDate = formatTanggalIDN.formatDate(dataList.get(position).getTglMulai())+" - "+formatTanggalIDN.formatDate(dataList.get(position).getTglBerakhir());
        }
        holder.txtEventDate.setText(eventDate + "   "+time+" WIB");
        holder.txtNamaUstadz.setText(dataList.get(position).getNamaUstadz());
        if (flag.equalsIgnoreCase("eventDetail")){
            holder.txtVenue.setVisibility(View.GONE);
            holder.txtVenueAddress.setVisibility(View.GONE);
            pathImage = Config.IMAGE_URL+dataList.get(position).getImageUstadz();
        }
        else if(flag.equalsIgnoreCase("eventPerformer")){
            holder.txtNamaUstadz.setVisibility(View.GONE);
            pathImage = Config.IMAGE_URL+dataList.get(position).getBannerImage();
        }
        else{
            pathImage = Config.IMAGE_URL+dataList.get(position).getImageUstadz();
        }
        holder.txtVenue.setText(dataList.get(position).getNamaVenue());
        holder.txtVenueAddress.setText(dataList.get(position).getAlamatVenue());
       // holder.txtEventTime.setText(dataList.get(position).getJamMulai().replace(":",".")+" - "+dataList.get(position).getJamSelesai().replace(":","."));
        Picasso.get()
                .load(pathImage)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(holder.bannerImage);;

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
        private TextView txtEventTitle, txtVenue,txtVenueAddress, txtNamaUstadz, txtEventDate, txtEventTime;
        private RelativeLayout card;
        private ImageView bannerImage,iconView;
        public EventListViewHolder(View itemView) {
            super(itemView);
            txtEventTitle = (TextView) itemView.findViewById(R.id.titleEvent);
            txtEventDate = (TextView) itemView.findViewById(R.id.eventDate);
            txtNamaUstadz = (TextView) itemView.findViewById(R.id.namaUstadz);
            txtVenue = (TextView) itemView.findViewById(R.id.venue);
            txtVenueAddress = (TextView) itemView.findViewById(R.id.venueAddress);
//            txtEventTime =(TextView) itemView.findViewById(R.id.waktuEvent);
//            iconView = itemView.findViewById(R.id.iconView);
            bannerImage = (ImageView) itemView.findViewById(R.id.bannerImage);
            card =(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
}
