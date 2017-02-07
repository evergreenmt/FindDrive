package com.seunindustries.finddrive;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewListOneActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LOG_TAG = ViewListOneActivity.class.getSimpleName();

    private static final int EVENT_SYNC_LIST = 1;

    Button mDriveStart;
    Button mViewMap;

    ListView mListView;
    ArrayAdapter<String> mAdapter;

    ArrayList<String> mArrayListName = new ArrayList<String>();
    ArrayList<RawData> mListRawData = new ArrayList<RawData>();
    String mResult = null;

    MyHandler mMyHandler;
    int mListIndex = 0;
    int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_one);

        mDriveStart = (Button) findViewById(R.id.drivestart);
        mDriveStart.setOnClickListener(this);

        mViewMap = (Button) findViewById(R.id.viewmap);
        mViewMap.setOnClickListener(this);

        Intent intent = getIntent();
        mListIndex = intent.getIntExtra("index", 0);
        Log.d(LOG_TAG, "mListIndex : " + mListIndex);

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mArrayListName);

        mListView = (ListView) findViewById(R.id.destlistview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mMyHandler = new MyHandler();

        // Thread로 웹서버에 접속
        new Thread() {
            public void run() {
                RawData rData = new RawData();
                ArrayList<RawData> listData = new ArrayList<RawData>();
                rData.setCmd("GETDEST");
                rData.setListID(mListIndex);
                listData.add(rData);

                mResult = CommonUtils.SendData(listData);
                Log.d(LOG_TAG, "mResult : " + mResult);

                if (mResult.length() > 30) {
                    mMyHandler.sendEmptyMessageDelayed(EVENT_SYNC_LIST, 100);
                }
            }
        }.start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drivestart:
                //mCurrentIndex;
                String urlstring = CommonUtils.MakeURL(mListRawData.get(mCurrentIndex));

                Intent i = new Intent(Intent.ACTION_VIEW);

                Uri u = Uri.parse(urlstring);
                i.setData(u);
                startActivity(i);

                //startActivity(intent);
                break;
            case R.id.viewmap:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (i < 0 || i >= mListView.getCount())
            return;
/*
        Intent intent = new Intent(this, ListOneActivity.class);
        intent.putExtra("index", i);

        startActivity(intent);
*/
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case EVENT_SYNC_LIST :
                    Log.d(LOG_TAG, "EVENT_SYNC_LIST");

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<RawData>>(){}.getType();
                    mListRawData = gson.fromJson(mResult, type);

                    int nSize = mListRawData.size();
                    Log.d(LOG_TAG, "nSize : "  + nSize);

                    for (int i = 0; i < nSize; i++) {
                        mArrayListName.add(mListRawData.get(i).getDestName());
                    }

                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
