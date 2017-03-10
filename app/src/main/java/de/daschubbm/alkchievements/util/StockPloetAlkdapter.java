package de.daschubbm.alkchievements.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import de.daschubbm.alkchievements.R;

/**
 * Created by Jonathan on 01.03.2017.
 */

public class StockPloetAlkdapter extends RecyclerView.Adapter<StockPloetAlkdapter.MyViewHolder>{

    private ArrayList<String[]> list;
    private final boolean admin;
    private final Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, num;
        public EditText add;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.stock_drink);
            num = (TextView) view.findViewById(R.id.stock_number);
            add = (EditText) view.findViewById(R.id.stock_add);
        }
    }

    public StockPloetAlkdapter (ArrayList<String[]> stock, Context con, boolean admin) {
        list = stock;
        context = con;
        this.admin = admin;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String[] mess = list.get(position);
        holder.name.setText(mess[0]);
        holder.num.setText(mess[1]);
        holder.add.setVisibility(View.INVISIBLE);
        if (admin) {
            holder.add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String[] getItem(int position) {
        return list.get(position);
    }

}
