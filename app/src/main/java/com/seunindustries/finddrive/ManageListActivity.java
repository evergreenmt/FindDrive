package com.seunindustries.finddrive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManageListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LOG_TAG = ManageListActivity.class.getSimpleName();

    private static final int EVENT_SYNC_LIST = 1;

    Button AddListButton;
    ListView mListView;
    ArrayAdapter<String> mAdapter;

    ArrayList<String> mArrayListName = new ArrayList<String>();
    ArrayList<RawData> mListRawData = new ArrayList<RawData>();
    String mResult = null;

    MyHandler mMyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);

        AddListButton = (Button) findViewById(R.id.addlistbutton);
        AddListButton.setOnClickListener(this);

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mArrayListName);

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mMyHandler = new MyHandler();

        // Thread로 웹서버에 접속
        new Thread() {
            public void run() {
                RawData rData = new RawData();
                rData.setCmd("GETLIST");
                mListRawData.add(rData);

                mResult = CommonUtils.SendData(mListRawData);
                Log.d(LOG_TAG, "mResult : " + mResult);

                if (mResult.length() > 30) {
                    mMyHandler.sendEmptyMessageDelayed(EVENT_SYNC_LIST, 100);
                }
            }
        }.start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addlistbutton:
                Intent intent = new Intent(this, AddListActivity.class);
                intent.putExtra("count", mListView.getCount());
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (i < 0 || i >= mListView.getCount())
            return;

        Intent intent = new Intent(this, ListOneActivity.class);
        intent.putExtra("index", i);

        startActivity(intent);
}

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case EVENT_SYNC_LIST :
                    Log.d(LOG_TAG, "EVENT_SYNC_LIST");

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<RawData>>(){}.getType();
                    ArrayList<RawData> RawDataList = gson.fromJson(mResult, type);

                    int nSize = RawDataList.size();
                    Log.d(LOG_TAG, "nSize : "  + nSize);

                    for (int i = 0; i < nSize; i++) {
                        mArrayListName.add(RawDataList.get(i).getListName());
                    }

                    mAdapter.notifyDataSetChanged();
                    //mResult;
                    break;
            }
        }
    }

}
