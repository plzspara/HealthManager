package net.kevin.com.healthmanager.javaBean;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/*
 *@author panlister
 */
public class ShopCar extends BmobObject {

    private String shopName;
    private String userId;
    private List<String> count;
    private List<String> goodsId;
    private Boolean isSelect_Shops = false;
    private List<shop> shops = new ArrayList<>();

    public List<shop> getShops() {
        return shops;
    }

    public void setShops(List<shop> shops) {
        this.shops = shops;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getCount() {
        return count;
    }

    public void setCount(List<String> count) {
        this.count = count;
    }

    public List<String> getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(List<String> goodsId) {
        this.goodsId = goodsId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }



    public Boolean getSelect_Shops() {
        return isSelect_Shops;
    }

    public void setSelect_Shops(Boolean select_Shops) {
        isSelect_Shops = select_Shops;
    }



    public static class shop extends BmobObject{
        private String goodImage;
        private int stocks;
        private Double price;
        private String goodsName;
        private String shopName;
        private int sales;
        private Boolean isSelect_Goods = false;

        public int getSales() {
            return sales;
        }

        public void setSales(int sales) {
            this.sales = sales;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getGoodImage() {
            return goodImage;
        }

        public void setGoodImage(String goodImage) {
            this.goodImage = goodImage;
        }

        public int getStocks() {
            return stocks;
        }

        public void setStocks(int stocks) {
            this.stocks = stocks;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public Boolean getSelect_Goods() {
            return isSelect_Goods;
        }

        public void setSelect_Goods(Boolean select_Goods) {
            isSelect_Goods = select_Goods;
        }
    }
}
