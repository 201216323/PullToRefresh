package com.lynnchurch.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.WrapRecyclerView;
import com.lynnchurch.pulltorefresh.MyPullListener;
import com.lynnchurch.pulltorefresh.R;
import com.lynnchurch.pulltorefresh.RecyclerAdapter;

import java.util.ArrayList;

public class PullableRecyclerViewActivity extends Activity
{
    private static final String TAG = PullableRecyclerViewActivity.class.getSimpleName();
    private WrapRecyclerView recycler_view;
    private PullToRefreshLayout ptrl;
    private ArrayList<String> mData = new ArrayList<>();
    private RecyclerAdapter mAdapter;
    private int mRequestCount; // 请求次数

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
        ptrl.setPullUpEnable(false);
        ptrl.setOnPullListener(new MyPullListener());
        recycler_view = (WrapRecyclerView) ptrl.getPullableView();
        initRecyclerView();
    }

    /**
     * ListView初始化方法
     */
    private void initRecyclerView()
    {
        for (int i = 0; i < 10; i++)
        {
            mData.add("这里是item " + i);
        }
        // 设置列表
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter(this, mData);
        mAdapter.setOnLoadmoreListener(new RecyclerAdapter.OnLoadmoreListener()
        {
            @Override
            public void onLoadmore()
            {
                mRequestCount++;
                new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        if (3 == mRequestCount)
                        {
                            // 请求失败时
                            mAdapter.onFailed();
                            return;
                        }
                        if (5 == mRequestCount)
                        {
                            // 没有更多内容时
                            mAdapter.onNothing();
                            return;
                        }
                        int size = mData.size();
                        for (int i = size; i < size + 5; i++)
                        {
                            // 由于加载更多的视图是用最后的item实现的,所以要插入在最后一项的前面
                            mData.add(mData.size() - 1, "这里是item " + (i - 1));
                        }
                        Log.i(TAG, "加载更多成功");
                        mAdapter.notifyDataSetChanged();
                    }
                }.sendEmptyMessageDelayed(0, 3000);
            }
        });
        mAdapter.setmOnItemClickListener(new RecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Toast.makeText(PullableRecyclerViewActivity.this,
                        " Click on " + mData.get(position),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLoginClick(View view, int position)
            {
                Toast.makeText(
                        PullableRecyclerViewActivity.this,
                        "LongClick on "
                                + mData.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
        recycler_view.setAdapter(mAdapter);
    }


}
