package mobile.app.ayotaklim.activity.performer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.venue.VenueListAdapter;
import mobile.app.ayotaklim.models.event.Event;
import mobile.app.ayotaklim.models.performer.Performer;
import mobile.app.ayotaklim.models.venue.Venue;
import mobile.app.ayotaklim.utils.ConvertImageBase64;

public class PerformerListAdapter extends RecyclerView.Adapter<PerformerListAdapter.PerformerListViewHolder> {


    private ArrayList<Performer> dataList;

    private OnItemClickListener listener;

    public PerformerListAdapter(ArrayList<Performer> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
    }

    @Override
    public PerformerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.performer_items, parent, false);
        return new PerformerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerformerListViewHolder holder,final int position) {
        holder.txtPerfName.setText(dataList.get(position).getNama());
        holder.txtPerfAddress.setText(dataList.get(position).getAlamat());
        ConvertImageBase64.setImageFromBase64(holder.imageUstadz,dataList.get(position).getImageUstadz());
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

    public class PerformerListViewHolder extends RecyclerView.ViewHolder{
        private TextView txtPerfName, txtPerfAddress;
        private CardView card;
        private ImageView imageUstadz;

        public PerformerListViewHolder(View itemView) {
            super(itemView);
            txtPerfName = (TextView) itemView.findViewById(R.id.performerName);
            txtPerfAddress = (TextView) itemView.findViewById(R.id.performerAddress);
            imageUstadz = (ImageView) itemView.findViewById(R.id.imagePerformer);
            card=(CardView) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Performer performer);
    }
}
