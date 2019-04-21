package net.kevin.com.healthmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.ShopCar;
import net.kevin.com.healthmanager.javaBean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DetailActivity extends Activity implements View.OnClickListener {

    private ImageView goodsImage, add, sub;
    private TextView text_goodsName, text_shopName, text_price, text_sales, text_stocks, text_count;
    private Button addToShopCar,shopCar;
    private Toolbar toolbar;

    private Double price = 0.0;
    private String url, shopName, goodsName, objectId;
    private int sales = 0, stocks = 0, count = 1;
    private String userObjectId;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        /*String bql ="select * from ShopCar";
        new BmobQuery<ShopCar>().doSQLQuery(bql,new SQLQueryListener<ShopCar>(){

            @Override
            public void done(BmobQueryResult<ShopCar> result, BmobException e) {
                if(e ==null){
                    Log.d("ok", "done: "+ ((List<ShopCar>) result.getResults()).get(0).getCount().get(1));
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });*/
        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        goodsImage = (ImageView) findViewById(R.id.goodsimage);
        add = (ImageView) findViewById(R.id.add);
        sub = (ImageView) findViewById(R.id.sub);

        text_goodsName = (TextView) findViewById(R.id.goodsname);
        text_shopName = (TextView) findViewById(R.id.shopname);
        text_price = (TextView) findViewById(R.id.goodsprice);
        text_sales = (TextView) findViewById(R.id.sales);
        text_stocks = (TextView) findViewById(R.id.stocks);
        text_count = (TextView) findViewById(R.id.count);

        addToShopCar = (Button) findViewById(R.id.addtoshopcar);
        shopCar = (Button) findViewById(R.id.shopcar);

        toolbar.setOnClickListener(this);
        add.setOnClickListener(this);
        sub.setOnClickListener(this);
        addToShopCar.setOnClickListener(this);
        shopCar.setOnClickListener(this);
    }

    public void initData() {
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");

        BmobQuery<ShopCar.shop> query = new BmobQuery<ShopCar.shop>();
        query.getObject(objectId, new QueryListener<ShopCar.shop>() {

            @Override
            public void done(ShopCar.shop object, BmobException e) {
                if(e==null){
                    goodsName = object.getGoodsName();
                    price = object.getPrice();
                    sales = object.getSales();
                    stocks = object.getStocks();
                    shopName = object.getShopName();
                    url = object.getGoodImage();
                    show();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }

        });



    }

    private void show() {
        Glide.with(getApplicationContext()).load(url).into(goodsImage);
        text_goodsName.setText(goodsName);
        text_price.setText("￥" + price);
        text_sales.setText("销量" + sales);
        text_shopName.setText(shopName);
        text_stocks.setText("库存" + stocks);
        text_count.setText("1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        count=1;
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (count < stocks) {
                    count++;
                    text_count.setText(count + "");
                } else {
                    Toast.makeText(DetailActivity.this, "没有库存了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sub:
                if (count > 1) {
                    count--;
                    text_count.setText(count + "");
                } else {
                    Toast.makeText(DetailActivity.this, "数量最少为1", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.addtoshopcar:
                User user = BmobUser.getCurrentUser(User.class);
                userObjectId = user.getObjectId();

                BmobQuery<ShopCar> categoryBmobQuery = new BmobQuery<>();
                categoryBmobQuery.addWhereEqualTo("userId", userObjectId);
                categoryBmobQuery.addWhereEqualTo("shopName", shopName);
                categoryBmobQuery.findObjects(new FindListener<ShopCar>() {
                    @Override
                    public void done(List<ShopCar> object, BmobException e) {
                        if (e == null) {
                            List<ShopCar> shopCars = object;
                            if (shopCars!=null && shopCars.size()>0) {

                                if (shopCars.get(0).getGoodsId().size() == 0) {
                                    update(shopCars.get(0).getObjectId(), count, objectId);

                                } else {
                                    for (int i=0;i<shopCars.get(0).getGoodsId().size();i++) {
                                        if (shopCars.get(0).getGoodsId().get(i).equals(objectId)) {
                                            count += Integer.parseInt(shopCars.get(0).getCount().get(i));
                                            updateAdd(shopCars.get(0).getObjectId(),count,i);
                                            break;
                                        }
                                        if (i==shopCars.get(0).getGoodsId().size()-1) {
                                            update(shopCars.get(0).getObjectId(), count, objectId);
                                        }
                                    }
                                }
                            } else {
                                newUpdate();
                            }
                        } else {
                            Log.e("BMOB", e.getMessage());
                        }
                    }
                });



                break;
            case R.id.toolbar:
                finish();
                break;

            case R.id.shopcar:
                Intent intent = new Intent(DetailActivity.this,ShopCarActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    public void update(String objectId, int count, String goodsId) {
        ShopCar shopCar = new ShopCar();
        shopCar.setObjectId(objectId);
        shopCar.add("goodsId",goodsId);
        shopCar.add("count",count+"");
        shopCar.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(DetailActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("bmob","更新失败："+e.getMessage());
                }
            }
        });
    }

    public void updateAdd(String objectId, int count,int position) {
        ShopCar shopCar = new ShopCar();
        shopCar.setValue("count."+position,count+"");
        shopCar.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(DetailActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("bmob","更新失败："+e.getMessage());
                }
            }
        });
    }

    private void newUpdate() {

        List<String> strings = new ArrayList<>();
        strings.add(count+"");
        List<String> list = new ArrayList<>();
        list.add(objectId);
        ShopCar shopCar = new ShopCar();
        shopCar.setShopName(shopName);
        shopCar.setUserId(userObjectId);
        shopCar.setCount(strings);
        shopCar.setGoodsId(list);
        shopCar.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(DetailActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "done: fail" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
