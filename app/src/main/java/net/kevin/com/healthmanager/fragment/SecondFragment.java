package net.kevin.com.healthmanager.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.adapter.MulRecyclerViewAdapter;
import net.kevin.com.healthmanager.javaBean.NewsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SecondFragment extends Fragment {

    private static final String TAG = "BlankFragment";
    private RecyclerView recyclerView;
    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private Handler handler = new Handler();
    private int page = 0;
    //private List<NewsPhotoBean> list = new ArrayList<NewsPhotoBean>();
    private List<NewsBean> list = new ArrayList<NewsBean>();
    private MulRecyclerViewAdapter adapter;
    private RecyclerAdapterWithHF mAdapter;

    private String url = "http://v.juhe.cn/toutiao/index?type=top&key=65cdaca310bd5bf342f78a067e24e719";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.test_list_view_frame);
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        loadNews();
        adapter = new MulRecyclerViewAdapter(getActivity(), list);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        recyclerView.setAdapter(mAdapter);
        ptrClassicFrameLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh(true);
            }
        }, 150);
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                list.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 0;
                        loadNews();
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.refreshComplete();
                        ptrClassicFrameLayout.setLoadMoreEnable(true);
                        Log.d("TAG", "正在刷新...");
                    }
                }, 1500);
            }
        });
        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadNews();
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.loadMoreComplete(true);
                        page++;
                        Log.e("TAG", page + "页");
                        Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });
        return view;
    }

    private void loadNews(){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    JSONArray jsonArray = jsonObject1.getJSONArray("data");
                    for (int i = 0; i < 30; i++) {
                        NewsBean newsBean = new NewsBean();
                        newsBean.setTitle(jsonArray.getJSONObject(i).getString("title"));
                        newsBean.setAuthor_name(jsonArray.getJSONObject(i).getString("author_name"));
                        newsBean.setCategory(jsonArray.getJSONObject(i).getString("category"));
                        newsBean.setDate(jsonArray.getJSONObject(i).getString("date"));
                        newsBean.setUrl(jsonArray.getJSONObject(i).getString("url"));
                        List<String> ls = new ArrayList<>();
                        for (int j = 0; j < jsonArray.getJSONObject(i).length() - 6; j++) {
                            String str = "";
                            if (j == 1) {
                                str = "02";
                            } else if (j == 2) {
                                str = "03";
                            }

                            String s = jsonArray.getJSONObject(i).getString("thumbnail_pic_s"+str);
                            ls.add(s);
                        }
                        newsBean.setList(ls);
                        list.add(newsBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

}
