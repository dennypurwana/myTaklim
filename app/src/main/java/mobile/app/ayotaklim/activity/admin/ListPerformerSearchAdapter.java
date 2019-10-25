package mobile.app.ayotaklim.activity.admin;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.config.Config;
import mobile.app.ayotaklim.models.performer.Performer;

public class ListPerformerSearchAdapter extends RecyclerView.Adapter<ListPerformerSearchAdapter.PerformerListViewHolder> {


    private ArrayList<Performer> dataList;

    private OnItemClickListener listener;
    Context mContext;
    public ListPerformerSearchAdapter(Context context, ArrayList<Performer> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener=listener;
        this.mContext = context;
    }

    @Override
    public PerformerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.performer_items_search, parent, false);
        return new PerformerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerformerListViewHolder holder,final int position) {
        holder.txtPerfName.setText(dataList.get(position).getNama());
        holder.txtPerfAddress.setText(dataList.get(position).getAlamat());
        Log.d("image url : ",Config.IMAGE_URL+dataList.get(position).getImageUstadz() );
        Picasso.get()
                .load(Config.IMAGE_URL+dataList.get(position).getImageUstadz())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .fit()
                .into(holder.imageUstadz);
//
       // ConvertImageBase64.setImageFromBase64(holder.imageUstadz,dataList.get(position).getImageUstadz());
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
        private RelativeLayout card;
        private ImageView imageUstadz , btnDelete;

        public PerformerListViewHolder(View itemView) {
            super(itemView);
            txtPerfName = (TextView) itemView.findViewById(R.id.performerName);
            txtPerfAddress = (TextView) itemView.findViewById(R.id.performerAddress);
            imageUstadz = (ImageView) itemView.findViewById(R.id.imagePerformer);
            card=(RelativeLayout) itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Performer performer);
    }
}
