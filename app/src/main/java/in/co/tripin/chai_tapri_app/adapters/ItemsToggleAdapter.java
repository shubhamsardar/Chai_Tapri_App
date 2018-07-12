package in.co.tripin.chai_tapri_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import in.co.tripin.chai_tapri_app.Managers.Logger;
import in.co.tripin.chai_tapri_app.POJOs.Responces.HubItemsPojo;
import in.co.tripin.chai_tapri_app.R;

public class ItemsToggleAdapter extends RecyclerView.Adapter<ItemsToggleAdapter.ViewHolder> {

    public HubItemsPojo.Data.Item[] data;
    public Context context;
    public ItemSelectionCallback itemSelectionCallback;

    public ItemsToggleAdapter(Context context, HubItemsPojo.Data.Item[] data, ItemSelectionCallback itemSelectionCallback) {
        this.data = data;
        this.context = context;
        this.itemSelectionCallback = itemSelectionCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_toggle_item, parent, false);
        return new ItemsToggleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.itemName.setText(data[position].getName());
        holder.itemRate.setText("₹" + data[position].getRate());
        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorGreyLight));
                }else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorGreyWhite));
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        if (data != null) {
            return data.length;
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public TextView itemRate;
        public Switch toggle;


        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemRate = itemView.findViewById(R.id.itemrate);
            toggle = itemView.findViewById(R.id.toggle);


        }

    }
}
