package com.example.sudhanshu.gis;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ListItem listItem = listItems.get(position);
        holder.date.setText(listItem.getDate());

        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = ((Activity) context).getFragmentManager();
                Log.i("ListView", Integer.toString((position)));
                SharedPreferences spref = context.getSharedPreferences("year_selected", 0);
                String yr = spref.getString("year", "2013");
                Log.d("TAG year", yr);
                DialogFragment d = new DateDialog(v,Integer.parseInt(yr),9,11, position);
                d.show(manager,"Date Picker");
                listItem.setDate(holder.date.getText().toString());
                SharedPreferences sp= context.getSharedPreferences("dates", Context.MODE_PRIVATE);
                String dt = sp.getString("Date_1","none");
                Log.d("Date", dt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView date;
        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);

        }
    }
}
