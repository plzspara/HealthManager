package net.kevin.com.healthmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.activity.WebActivity;
import net.kevin.com.healthmanager.javaBean.NewsBean;

import java.util.List;


/*
 *@author panlister
 * 新闻适配器
 */
public class MulRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NEW_SIMPLE_TYPE = 0;//单图文模式
    private static final int NEW_MUL_TYPE = 1;//多图文模式
    private static final int NEW_OTHER_TYPE = 2;//多图文模式
    private Context context;
    private List<NewsBean> list;
    private static final String TAG = "MulRecyclerViewAdapter";


    public MulRecyclerViewAdapter(Context context, List<NewsBean> list) {
        this.context = context;
        this.list = list;
    }

    //重写getItemViewType方法,通过此方法来判断应该加载是哪种类型布局
    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getList().size() == 1) {
            return NEW_SIMPLE_TYPE;
        } else {
            return NEW_MUL_TYPE;
        }
    }

    //根据不同的item类型来加载不同的viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case NEW_SIMPLE_TYPE:
                return new NewsViewHolder(inflater.inflate(R.layout.recyclerview_item_type_02, parent, false));
            case NEW_MUL_TYPE:
                return new NewsViewsHolder(inflater.inflate(R.layout.recyclerview_item_type_01, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //把对应位置的数据得到
        String title = list.get(position).getTitle();
        String time = list.get(position).getDate().substring(11);
        String author = list.get(position).getAuthor_name();
        //这里是json数据中的图片集合，也就是封面。不同类型item的封面图片数量是不一样的
        //  //无论是否单图文，标题和更新时间以及作者不变

        //如果单图文
        if (holder instanceof NewsViewHolder) {

            ((NewsViewHolder) holder).tx_news_simple_photos_title.setText(title);
            ((NewsViewHolder) holder).tx_news_simple_photos_time.setText(time);
            ((NewsViewHolder) holder).tx_news_simple_photos_author.setText(author);
            Glide.with(context).load(list.get(position).getList().get(0)).into(((NewsViewHolder) holder).img_news_simple_photos_01);
//            ((NewsPhotoViewHolder) holder).img_news_simple_photos_01.setImageBitmap(btm_01);//单图文不用遍历直接将图片转换bitmap对象设置到ImageView上
            ((NewsViewHolder) holder).img_news_simple_photos_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(list.get(position).getUrl());
                }
            });
            ((NewsViewHolder) holder).tx_news_simple_photos_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(list.get(position).getUrl());
                }
            });
            return;
        }

        //如果多图文
        if (holder instanceof NewsViewsHolder) {
            ((NewsViewsHolder) holder).tx_news_mul_photos_title.setText(title);
            ((NewsViewsHolder) holder).tx_news_mul_photos_time.setText(time);
            ((NewsViewsHolder) holder).tx_news_mul_photos_author.setText(author);
            Glide.with(context).load(list.get(position).getList().get(0)).into(((NewsViewsHolder) holder).img_news_mul_photos_01);
            Glide.with(context).load(list.get(position).getList().get(1)).into(((NewsViewsHolder) holder).img_news_mul_photos_02);
            ((NewsViewsHolder) holder).tx_news_mul_photos_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(list.get(position).getUrl());
                }
            });
            ((NewsViewsHolder) holder).img_news_mul_photos_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(list.get(position).getUrl());
                }
            });
            ((NewsViewsHolder) holder).img_news_mul_photos_02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(list.get(position).getUrl());
                }
            });
            if (list.get(position).getList().size() == 3) {
                Glide.with(context).load(list.get(position).getList().get(2)).into(((NewsViewsHolder) holder).img_news_mul_photos_03);
                ((NewsViewsHolder) holder).img_news_mul_photos_03.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick(list.get(position).getUrl());
                    }
                });
            } else {
                ((NewsViewsHolder) holder).img_news_mul_photos_03.setVisibility(View.GONE);
            }

//            ((NewsPhotosViewHolder) holder).img_news_mul_photos_01.setImageBitmap(btm_01);//多图文需要遍历list将每个图片链接转换成Bitmap对象设置到ImageView上
//            ((NewsPhotosViewHolder) holder).img_news_mul_photos_02.setImageBitmap(btm_02);
//            ((NewsPhotosViewHolder) holder).img_news_mul_photos_03.setImageBitmap(btm_03);
            return;
        }
    }

    public void onItemClick(String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }

    //具体item数据等于pages*30，每页30条
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * NewsViewHolder为单图文模式
     */
    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView tx_news_simple_photos_title;//标题
        private ImageView img_news_simple_photos_01;//单图文模式的唯一一张图
        private TextView tx_news_simple_photos_time;//单图文模式的更新时间
        private TextView tx_news_simple_photos_author;//单图文模式的新闻作者

        public NewsViewHolder(View itemView) {
            super(itemView);
            tx_news_simple_photos_title = (TextView) itemView.findViewById(R.id.tx_news_simple_photos_title);//标题
            img_news_simple_photos_01 = (ImageView) itemView.findViewById(R.id.tx_news_simple_photos_01);//单图文模式的唯一一张图
            tx_news_simple_photos_time = (TextView) itemView.findViewById(R.id.tx_news_simple_photos_time);//单图文模式的更新时间
            tx_news_simple_photos_author = (TextView) itemView.findViewById(R.id.img_news_simple_photos_author);//单图文模式的新闻作者

        }
    }

    /**
     * NewsViewHolder为多图模式
     */
    class NewsViewsHolder extends RecyclerView.ViewHolder {
        private TextView tx_news_mul_photos_title;//标题
          private ImageView img_news_mul_photos_01;//多图文模式的第一张图
        private ImageView img_news_mul_photos_02;//多图文模式的第二张图
        private ImageView img_news_mul_photos_03;//多图文模式的第三张图
        private TextView tx_news_mul_photos_time;//多图文模式的更新时间
        private TextView tx_news_mul_photos_author;//多图文模式的新闻作者

        public NewsViewsHolder(View itemView) {
            super(itemView);
            tx_news_mul_photos_title = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_title);
            img_news_mul_photos_01 = (ImageView) itemView.findViewById(R.id.img_news_mul_photos_01);
            img_news_mul_photos_02 = (ImageView) itemView.findViewById(R.id.img_news_mul_photos_02);
            img_news_mul_photos_03 = (ImageView) itemView.findViewById(R.id.img_news_mul_photos_03);
            tx_news_mul_photos_time = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_time);
            tx_news_mul_photos_author = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_author);
        }
    }

}