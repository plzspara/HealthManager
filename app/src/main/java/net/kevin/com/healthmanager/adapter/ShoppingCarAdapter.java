package net.kevin.com.healthmanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.ShopCar;
import net.kevin.com.healthmanager.util.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 购物车的adapter
 * 因为使用的是ExpandableListView，所以继承BaseExpandableListAdapter
 */
public class ShoppingCarAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final LinearLayout llSelectAll;
    private final ImageView ivSelectAll;
    private final Button btnOrder;
    private final Button btnDelete;
    private final RelativeLayout rlTotalPrice;
    private final TextView tvTotalPrice;
    private List<ShopCar> data;
    private boolean isSelectAll = false;
    private double total_price;
    ShopCar datasBean;
    boolean isSelect;
    boolean isSelect_shop = false;
    private static final String TAG = "ShoppingCarAdapter";

    public ShoppingCarAdapter(Context context, LinearLayout llSelectAll,
                              ImageView ivSelectAll, Button btnOrder, Button btnDelete,
                              RelativeLayout rlTotalPrice, TextView tvTotalPrice) {
        this.context = context;
        this.llSelectAll = llSelectAll;
        this.ivSelectAll = ivSelectAll;
        this.btnOrder = btnOrder;
        this.btnDelete = btnDelete;
        this.rlTotalPrice = rlTotalPrice;
        this.tvTotalPrice = tvTotalPrice;
    }

    /**
     * 自定义设置数据方法；
     * 通过notifyDataSetChanged()刷新数据，可保持当前位置
     *
     * @param data 需要刷新的数据
     */
    public void setData(List<ShopCar> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_group, null);

            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        datasBean = data.get(groupPosition);
        //店铺名称
        String store_name = datasBean.getShopName();
        //设置商家名
        if (store_name != null) {
            groupViewHolder.tvStoreName.setText(store_name);
        } else {
            groupViewHolder.tvStoreName.setText("");
        }

        //店铺内的商品都选中的时候，店铺的也要选中
        //遍历所有某商家的所有商品
        for (int i = 0; i < datasBean.getShops().size(); i++) {
            ShopCar.shop shops = datasBean.getShops().get(i);
            isSelect = shops.getSelect_Goods();
            if (isSelect) {
                datasBean.setSelect_Shops(true);
            } else {
                //有任一商品没有被选中，就商家不能被选中
                datasBean.setSelect_Shops(false);
                break;
            }
        }

        //因为set之后要重新get，所以这一块代码要放到一起执行
        //店铺是否在购物车中被选中
        if (datasBean.getSelect_Shops()) {
            groupViewHolder.ivSelect.setImageResource(R.mipmap.select);
        } else {
            groupViewHolder.ivSelect.setImageResource(R.mipmap.unselect);
        }

        //店铺选择框的点击事件
        groupViewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopCar selectData = data.get(groupPosition);
                isSelect_shop = !selectData.getSelect_Shops();
                selectData.setSelect_Shops(isSelect_shop);
                List<ShopCar.shop> goods = selectData.getShops();
                for (int i = 0; i < goods.size(); i++) {
                    ShopCar.shop goodsBean = goods.get(i);
                    goodsBean.setSelect_Goods(isSelect_shop);
                }
                notifyDataSetChanged();
            }
        });
        /*groupViewHolder.ll_shopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasBean.setSelect_Shops(!isSelect_shop);
                List<ShopCar.shop> goods = datasBean.getShops();
                for (int i = 0; i < goods.size(); i++) {
                    ShopCar.shop goodsBean = goods.get(i);
                    goodsBean.setSelect_Goods(!isSelect_shop);
                }
                Log.d(TAG, "onClick: " + !isSelect_shop);
                notifyDataSetChanged();
            }
        });*/

        //当所有的选择框都是选中的时候，全选也要选中
        w:
        for (int i = 0; i < data.size(); i++) {
            List<ShopCar.shop> goods = data.get(i).getShops();
            for (int y = 0; y < goods.size(); y++) {
                ShopCar.shop goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getSelect_Goods();
                if (isSelect) {
                    isSelectAll = true;
                } else {
                    isSelectAll = false;
                    break w;//根据标记，跳出嵌套循环
                }
            }
        }
        if (isSelectAll) {
            ivSelectAll.setBackgroundResource(R.mipmap.select);
        } else {
            ivSelectAll.setBackgroundResource(R.mipmap.unselect);
        }

        //全选的点击事件
        llSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectAll = !isSelectAll;

                if (isSelectAll) {
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelect_Shops(true);
                        List<ShopCar.shop> goods = data.get(i).getShops();
                        for (int y = 0; y < goods.size(); y++) {
                            ShopCar.shop goodsBean = goods.get(y);
                            goodsBean.setSelect_Goods(true);
                        }
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelect_Shops(false);
                        List<ShopCar.shop> goods = data.get(i).getShops();
                        for (int y = 0; y < goods.size(); y++) {
                            ShopCar.shop goodsBean = goods.get(y);
                            goodsBean.setSelect_Goods(false);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        //合计的计算
        total_price = 0.0;
        tvTotalPrice.setText("¥0.00");
        for (int i = 0; i < data.size(); i++) {
            List<ShopCar.shop> goods = data.get(i).getShops();
            for (int y = 0; y < goods.size(); y++) {
                ShopCar.shop goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getSelect_Goods();
                if (isSelect) {
                    int num = Integer.parseInt(data.get(i).getCount().get(y));
                    double price = goodsBean.getPrice();

                    total_price = total_price + num * price;

                    //让Double类型完整显示，不用科学计数法显示大写字母E
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvTotalPrice.setText("¥" + decimalFormat.format(total_price));
                }
            }
        }

        //去结算的点击事件
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建临时的List，用于存储被选中的商品
                List<ShopCar.shop> temp = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    List<ShopCar.shop> goods = data.get(i).getShops();
                    for (int y = 0; y < goods.size(); y++) {
                        ShopCar.shop goodsBean = goods.get(y);
                        boolean isSelect = goodsBean.getSelect_Goods();
                        if (isSelect) {
                            temp.add(goodsBean);
                        }
                    }
                }

                if (temp != null && temp.size() > 0) {//如果有被选中的
                    /**
                     * 实际开发中，如果有被选中的商品，
                     * 则跳转到确认订单页面，完成后续订单流程。
                     */
                    ToastUtil.makeText(context, "跳转到确认订单页面，完成后续订单流程");
                } else {
                    ToastUtil.makeText(context, "请选择要购买的商品");
                }
            }
        });

        //删除的点击事件
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteListener != null) {
                    mDeleteListener.onDelete(data);
                }
            }
        });

        return convertView;
    }


    static class GroupViewHolder {
        ImageView ivSelect;
        TextView tvStoreName;

        GroupViewHolder(View view) {
            ivSelect = (ImageView) view.findViewById(R.id.iv_select);
            tvStoreName = (TextView) view.findViewById(R.id.tv_store_name);
        }
    }


    //------------------------------------------------------------------------------------------------
    @Override
    public int getChildrenCount(int groupPosition) {
        if (data.get(groupPosition).getShops() != null && data.get(groupPosition).getShops().size() > 0) {
            return data.get(groupPosition).getShops().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).getShops().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_child, null);

            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        final ShopCar datasBean = data.get(groupPosition);

        //店铺名称
        String store_name = datasBean.getShopName();
        //店铺是否在购物车中被选中
        final boolean isSelect_shop = datasBean.getSelect_Shops();
        final ShopCar.shop goodsBean = datasBean.getShops().get(childPosition);
        //商品图片
        String goods_image = goodsBean.getGoodImage();
        //商品ID
        final String goods_id = goodsBean.getObjectId();
        //商品名称
        String goods_name = goodsBean.getGoodsName();
        //商品价格
        Double goods_price = goodsBean.getPrice();

        //商品数量
        int goods_num = Integer.parseInt(data.get(groupPosition).getCount().get(childPosition));
        //商品是否被选中
        final boolean isSelect = goodsBean.getSelect_Goods();

        Glide.with(context)
                .load(goods_image)
                .into(childViewHolder.ivPhoto);
        if (goods_name != null) {
            childViewHolder.tvName.setText(goods_name);
        } else {
            childViewHolder.tvName.setText("");
        }
        if (goods_price != null) {
            childViewHolder.tvPriceValue.setText(goods_price + "");
        } else {
            childViewHolder.tvPriceValue.setText("");
        }
        if (goods_num > 0) {
            childViewHolder.tvEditBuyNumber.setText(goods_num + "");
        } else {
            childViewHolder.tvEditBuyNumber.setText("");
        }

        //商品是否被选中
        if (isSelect) {
            childViewHolder.ivSelect.setImageResource(R.mipmap.select);
        } else {
            childViewHolder.ivSelect.setImageResource(R.mipmap.unselect);
        }

        //商品选择框的点击事件
        childViewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsBean.setSelect_Goods(!isSelect);
                if (!isSelect == false) {
                    datasBean.setSelect_Shops(false);
                }
                notifyDataSetChanged();
            }
        });

        //加号的点击事件
        childViewHolder.ivEditAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模拟加号操作
                int num = Integer.parseInt(datasBean.getCount().get(childPosition));
                num++;

                data.get(groupPosition).getCount().set(childPosition, num + "");

                notifyDataSetChanged();

                /**
                 * 实际开发中，通过回调请求后台接口实现数量的加减
                 */
                if (mChangeCountListener != null) {
                    mChangeCountListener.onChangeCount(data.get(groupPosition).getObjectId(), num, childPosition);
                }


            }
        });
        //减号的点击事件
        childViewHolder.ivEditSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模拟减号操作
                int num = Integer.parseInt(datasBean.getCount().get(childPosition));
                if (num > 1) {
                    num--;
                    data.get(groupPosition).getCount().set(childPosition, num + "");

                    notifyDataSetChanged();

                    /**
                     * 实际开发中，通过回调请求后台接口实现数量的加减
                     */
                    if (mChangeCountListener != null) {
                        mChangeCountListener.onChangeCount(data.get(groupPosition).getObjectId(), num, childPosition);
                    }
                } else {
                    ToastUtil.makeText(context, "商品不能再减少了");
                }
                notifyDataSetChanged();
            }
        });

        if (childPosition == data.get(groupPosition).getShops().size() - 1) {
            childViewHolder.mView.setVisibility(View.GONE);
            childViewHolder.viewLast.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.mView.setVisibility(View.VISIBLE);
            childViewHolder.viewLast.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ChildViewHolder {
        ImageView ivSelect, ivPhoto, ivEditSubtract, ivEditAdd;
        TextView tvName, tvPriceKey, tvPriceValue, tvEditBuyNumber;
        View mView, viewLast;

        ChildViewHolder(View view) {
            ivSelect = (ImageView) view.findViewById(R.id.iv_select);
            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvPriceKey = (TextView) view.findViewById(R.id.tv_price_key);
            tvPriceValue = (TextView) view.findViewById(R.id.tv_price_value);
            ivEditSubtract = (ImageView) view.findViewById(R.id.iv_edit_subtract);
            tvEditBuyNumber = (TextView) view.findViewById(R.id.tv_edit_buy_number);
            ivEditAdd = (ImageView) view.findViewById(R.id.iv_edit_add);
            mView = (View) view.findViewById(R.id.view);
            viewLast = (View) view.findViewById(R.id.view_last);
        }
    }

    //-----------------------------------------------------------------------------------------------

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //删除的回调
    public interface OnDeleteListener {
        void onDelete(List<ShopCar> shopCars);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    private OnDeleteListener mDeleteListener;

    //修改商品数量的回调
    public interface OnChangeCountListener {
        void onChangeCount(String objectId, int count, int position);
    }

    public void setOnChangeCountListener(OnChangeCountListener listener) {
        mChangeCountListener = listener;
    }

    private OnChangeCountListener mChangeCountListener;
}
