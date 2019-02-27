package net.kevin.com.healthmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;

import java.util.ArrayList;
import java.util.List;

/*
 *@author panlister
 */
public class RecyclerDemoAdapter extends RecyclerView.Adapter<RecyclerDemoAdapter.MyHolder>{
    // 展示数据
    Context context;
    List<String> list;

    public RecyclerDemoAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyHolder viewHolder, int i) {
        String s = list.get(i);
        viewHolder.textView.setText(s);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"textView",Toast.LENGTH_SHORT);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"itemView",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_num);
        }
    }


}
