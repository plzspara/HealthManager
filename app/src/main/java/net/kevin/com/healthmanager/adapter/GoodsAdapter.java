package net.kevin.com.healthmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.activity.DetailActivity;
import net.kevin.com.healthmanager.javaBean.shop;

import java.util.List;

/*
 *@author panlister
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private static final String TAG = "GoodsAdapter";
    private Context context;
    private List<shop> shops;

    public GoodsAdapter(Context context, List<shop> shops) {
        this.context = context;
        this.shops = shops;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_goods, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final String url = shops.get(i).goodImage;
        final String goodsName = shops.get(i).goodsName;
        final Double price = shops.get(i).price;
        final String shopName = shops.get(i).shopName;
        final int sales = shops.get(i).sales;
        final int stocks = shops.get(i).stocks;
        final String objectId = shops.get(i).objectId;
        Glide.with(context).load(url).into(((ViewHolder) viewHolder).imageView);
        ((ViewHolder) viewHolder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("objectId",objectId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }
    }

}
