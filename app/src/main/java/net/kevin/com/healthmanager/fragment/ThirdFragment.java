package net.kevin.com.healthmanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.activity.ShopCarActivity;
import net.kevin.com.healthmanager.adapter.GoodsAdapter;
import net.kevin.com.healthmanager.javaBean.shop;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {


    private List<shop> shops = new ArrayList<>();

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        String bql ="select * from shop";
        new BmobQuery<shop>().doSQLQuery(bql,new SQLQueryListener<shop>(){
            @Override
            public void done(BmobQueryResult<shop> result, BmobException e) {
                if(e ==null){
                    shops = (List<shop>) result.getResults();
                    if(shops!=null && shops.size()>0){
                        goods();
                    }else{
                        Log.i("smile", "查询成功，无数据返回");
                    }
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopCarActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void goods(){
        GoodsAdapter goodsAdapter = new GoodsAdapter(getContext(),shops);
        recyclerView.setAdapter(goodsAdapter);
    }



}
