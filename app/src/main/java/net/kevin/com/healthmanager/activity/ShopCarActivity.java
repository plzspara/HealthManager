package net.kevin.com.healthmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.adapter.ShoppingCarAdapter;
import net.kevin.com.healthmanager.customview.RoundCornerDialog;
import net.kevin.com.healthmanager.javaBean.ShopCar;
import net.kevin.com.healthmanager.javaBean.User;
import net.kevin.com.healthmanager.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 购物车的最佳实现（分店铺）
 * 主要功能：1.刷新数据；
 * 2.单选、全选；
 * 3.合计；
 * 4.删除；
 * 5.商品数量加减；
 */
public class ShopCarActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitlebarCenter, tvTitlebarRight, tvTotalPrice;
    ExpandableListView elvShoppingCar;
    ImageView ivSelectAll, ivNoContant;
    LinearLayout llSelectAll;
    Button btnOrder, btnDelete;
    RelativeLayout rlTotalPrice, rl, rlNoContant;
    private List<ShopCar> shopCars = new ArrayList<ShopCar>();
    private Context context;
    private ShoppingCarAdapter shoppingCarAdapter;
    private List<Integer> a = new ArrayList<>();
    private List<Integer> b = new ArrayList<>();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        context = this;
        initView();
        initExpandableListView();
        initData();
    }

    private void initView() {


        tvTitlebarCenter = (TextView) findViewById(R.id.tv_titlebar_center);
        tvTitlebarRight = (TextView) findViewById(R.id.tv_titlebar_right);
        elvShoppingCar = (ExpandableListView) findViewById(R.id.elv_shopping_car);
        ivSelectAll = (ImageView) findViewById(R.id.iv_select_all);
        llSelectAll = (LinearLayout) findViewById(R.id.ll_select_all);
        btnOrder = (Button) findViewById(R.id.btn_order);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        rlTotalPrice = (RelativeLayout) findViewById(R.id.rl_total_price);
        rl = (RelativeLayout) findViewById(R.id.rl);
        ivNoContant = (ImageView) findViewById(R.id.iv_no_contant);
        rlNoContant = (RelativeLayout) findViewById(R.id.rl_no_contant);
        tvTitlebarRight.setOnClickListener(this);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        User user = BmobUser.getCurrentUser(User.class);
        String userId = user.getObjectId();
        String bql = "select * from ShopCar where userId = '"+userId+"'";
        new BmobQuery<ShopCar>().doSQLQuery(bql, new SQLQueryListener<ShopCar>() {

            @Override
            public void done(BmobQueryResult<ShopCar> result, BmobException e) {
                if (e == null) {
                    shopCars = (List<ShopCar>) result.getResults();
                    initGoods();
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });

        //initExpandableListViewData(shopCars);
    }

    private void initGoods() {
        for (int i = 0; i < shopCars.size(); i++) {
            for (int j = 0; j < shopCars.get(i).getGoodsId().size(); j++) {
                String objectId = shopCars.get(i).getGoodsId().get(j);
                query(objectId);
            }
        }

    }

    private void query(final String objectId) {
        BmobQuery<ShopCar.shop> bmobQuery = new BmobQuery<ShopCar.shop>();
        bmobQuery.getObject(objectId, new QueryListener<ShopCar.shop>() {
            @Override
            public void done(ShopCar.shop object, BmobException e) {
                if (e == null) {
                    addGoods(object);
                } else {
                    Log.e("smile", "done: " + e.getMessage());
                }
            }
        });
    }


    private void addGoods(ShopCar.shop shop) {
        for (int i=0;i<shopCars.size();i++) {
            if (shopCars.get(i).getShopName().equals(shop.getShopName())){
                shopCars.get(i).getShops().add(shop);
            }
        }
        //完成状态
        boolean status = true;

        for (int i =0;i<shopCars.size();i++) {
            if (shopCars.get(i).getShops().size() != shopCars.get(i).getGoodsId().size()){
                status = false;
                break;
            }
        }

        if (status) {
            initExpandableListViewData(shopCars);
        }
    }

    /**
     * 初始化ExpandableListView
     * 创建数据适配器adapter，并进行初始化操作
     */
    private void initExpandableListView() {
        shoppingCarAdapter = new ShoppingCarAdapter(context, llSelectAll, ivSelectAll, btnOrder, btnDelete, rlTotalPrice, tvTotalPrice);
        elvShoppingCar.setAdapter(shoppingCarAdapter);

        //删除的回调
        shoppingCarAdapter.setOnDeleteListener(new ShoppingCarAdapter.OnDeleteListener() {
            @Override
            public void onDelete(List<ShopCar> object) {
                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).getSelect_Shops()) {
                        ShopCar shopCar = new ShopCar();
                        shopCar.setObjectId(object.get(i).getObjectId());
                        shopCar.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "删除成功");
                                } else {
                                    Log.d(TAG, "删除失败");
                                }
                            }
                        });
                    } else {
                        for (int j = 0; j < object.get(i).getShops().size(); j++) {
                            Log.d(TAG, "onDelete: " + i + "-" + j);
                            if (object.get(i).getShops().get(j).getSelect_Goods()) {
                                ShopCar shopCar = new ShopCar();
                                shopCar.setObjectId(object.get(i).getObjectId());
                                shopCar.removeAll("goodsId", Arrays.asList(object.get(i).getGoodsId().get(j)));
                                shopCar.removeAll("count",Arrays.asList(object.get(i).getCount().get(j)));
                                shopCar.update(new UpdateListener() {

                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            Log.i("bmob","成功");
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
                initDelete();
            }
        });

        //修改商品数量的回调
        shoppingCarAdapter.setOnChangeCountListener(new ShoppingCarAdapter.OnChangeCountListener() {
            @Override
            public void onChangeCount(String objectId, int count, int position) {
                /**
                 * 实际开发中，在此请求修改商品数量的接口，商品数量修改成功后，
                 * 通过initExpandableListViewData（）方法刷新购物车数据。
                 */
                ShopCar shopCar = new ShopCar();
                shopCar.setValue("count."+position,count+"");
                shopCar.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Log.i("bmob","成功");
                        }else{
                            Log.i("bmob","失败："+e.getMessage());
                        }
                    }
                });

            }
        });
    }


    private void initExpandableListViewData(List<ShopCar> shopCars) {
        if (shopCars != null && shopCars.size() > 0) {
            //刷新数据时，保持当前位置
            shoppingCarAdapter.setData(shopCars);

            //使所有组展开
            for (int i = 0; i < shoppingCarAdapter.getGroupCount(); i++) {
                elvShoppingCar.expandGroup(i);
            }

            //使组点击无效果
            elvShoppingCar.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return true;
                }
            });

            tvTitlebarRight.setVisibility(View.VISIBLE);
            tvTitlebarRight.setText("编辑");
            rlNoContant.setVisibility(View.GONE);
            elvShoppingCar.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            rlTotalPrice.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        } else {
            tvTitlebarRight.setVisibility(View.GONE);
            rlNoContant.setVisibility(View.VISIBLE);
            elvShoppingCar.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否要弹出删除的dialog
     * 通过bean类中的DatasBean的isSelect_shop属性，判断店铺是否被选中；
     * GoodsBean的isSelect属性，判断商品是否被选中，
     */
    private void initDelete() {
        //判断是否有店铺或商品被选中
        //true为有，则需要刷新数据；反之，则不需要；
        boolean hasSelect = false;
        //创建临时的List，用于存储没有被选中的购物车数据
        List<ShopCar> datasTemp = new ArrayList<>();

        for (int i = 0; i < shopCars.size(); i++) {
            List<ShopCar.shop> goods = shopCars.get(i).getShops();
            boolean isSelect_shop = shopCars.get(i).getSelect_Shops();

            if (isSelect_shop) {
                hasSelect = true;
                //跳出本次循环，继续下次循环。
                continue;
            } else {
                datasTemp.add(shopCars.get(i));
                datasTemp.get(datasTemp.size() - 1).setShops(new ArrayList<ShopCar.shop>());
            }

            for (int y = 0; y < goods.size(); y++) {
                ShopCar.shop goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getSelect_Goods();

                if (isSelect) {
                    hasSelect = true;
                } else {
                    datasTemp.get(datasTemp.size() - 1).getShops().add(goodsBean);
                }
            }
        }

        if (hasSelect) {
            showDeleteDialog(datasTemp);
        } else {
            ToastUtil.makeText(context, "请选择要删除的商品");
        }
    }

    /**
     * 展示删除的dialog（可以自定义弹窗，不用删除即可）
     *
     * @param datasTemp
     */
    private void showDeleteDialog(final List<ShopCar> datasTemp) {
        View view = View.inflate(context, R.layout.dialog_two_btn, null);
        final RoundCornerDialog roundCornerDialog = new RoundCornerDialog(context, 0, 0, view, R.style.RoundCornerDialog);
        roundCornerDialog.show();
        roundCornerDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        roundCornerDialog.setOnKeyListener(keylistener);//设置点击返回键Dialog不消失

        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        TextView tv_logout_confirm = (TextView) view.findViewById(R.id.tv_logout_confirm);
        TextView tv_logout_cancel = (TextView) view.findViewById(R.id.tv_logout_cancel);
        tv_message.setText("确定要删除商品吗？");

        //确定
        tv_logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();
                shopCars = datasTemp;
                initExpandableListViewData(shopCars);
            }
        });
        //取消
        tv_logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();
            }
        });
    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_titlebar_right://编辑
                String edit = tvTitlebarRight.getText().toString().trim();
                if (edit.equals("编辑")) {
                    tvTitlebarRight.setText("完成");
                    rlTotalPrice.setVisibility(View.GONE);
                    btnOrder.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    tvTitlebarRight.setText("编辑");
                    rlTotalPrice.setVisibility(View.VISIBLE);
                    btnOrder.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }
}
