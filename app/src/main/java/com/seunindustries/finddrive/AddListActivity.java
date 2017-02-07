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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LOG_TAG = AddListActivity.class.getSimpleName();

    private static final int ACTIVITY_RESULT_ADD_DESTINATION = 1;
    private static final int ACTIVITY_RESULT_EDIT_DESTINATION = 2;
    private static final int EVENT_FINISH = 100;

    EditText mListEdit;
    Button mAddDestButton;
    Button mRegister;
    Button mCancel;

    ArrayList<RawData> mArrayDestInfo;
    ArrayList<String> mArrayDestName;

    ListView mDestList;
    ArrayAdapter<String> mAdapter;

    MyHandler mMyHandler;
    int mListIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_acitivity);

        Intent intent = getIntent();
        mListIndex = intent.getIntExtra("count", 0);

        mListEdit = (EditText) findViewById(R.id.editlistname);

        mAddDestButton = (Button) findViewById(R.id.adddest);
        mAddDestButton.setOnClickListener(this);

        mRegister = (Button) findViewById(R.id.register);
        mRegister.setOnClickListener(this);

        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        mArrayDestInfo = new ArrayList<RawData>();
        mArrayDestName = new ArrayList<String>();

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mArrayDestName);

        mDestList = (ListView) findViewById(R.id.listview);
        mDestList.setAdapter(mAdapter);
        mDestList.setOnItemClickListener(this);

        mMyHandler = new MyHandler();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mArrayDestInfo.size() > 0) {
            mArrayDestInfo.clear();
            mArrayDestName.clear();
        }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.adddest:
                try {
                    startActivityForResult(new Intent(this, AddDestActivity.class),
                            ACTIVITY_RESULT_ADD_DESTINATION);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.activitynotfound, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.register:
                if (mArrayDestInfo != null && mArrayDestInfo.size() > 0) {
                    for (int i = 0; i< mArrayDestInfo.size(); i++) {
                        RawData data = mArrayDestInfo.get(i);
                        data.setCmd("PUT");
                        data.setDestID(i);
                        data.setListID(mListIndex);
                        data.setListName(mListEdit.getText().toString());

                        mArrayDestInfo.set(i, data);
                    }

                    // Thread로 웹서버에 접속
                    new Thread() {
                        public void run() {
                            CommonUtils.SendData(mArrayDestInfo);
                            mMyHandler.sendEmptyMessageDelayed(EVENT_FINISH, 500);
                        }
                    }.start();

                } else {
                    Toast.makeText(this, "mArrayDestInfo null", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cancel:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0 || i >= mArrayDestInfo.size())
            return;

        Intent intent = new Intent(this, AddDestActivity.class);
        intent.putExtra("destname", mArrayDestInfo.get(i).getDestName());
        intent.putExtra("addr", mArrayDestInfo.get(i).getAddr());
        intent.putExtra("lat", mArrayDestInfo.get(i).getLat().toString());
        intent.putExtra("lgt", mArrayDestInfo.get(i).getLgt().toString());
        intent.putExtra("index", i);

        try {
            startActivityForResult(intent,
                    ACTIVITY_RESULT_EDIT_DESTINATION);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activitynotfound, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_RESULT_ADD_DESTINATION) {
            if (resultCode == RESULT_OK) {
                RawData dData = new RawData();
                dData.setDestName(data.getStringExtra("destname"));
                dData.setAddr(data.getStringExtra("addr"));
                dData.setLat(Double.parseDouble(data.getStringExtra("lat")));
                dData.setLgt(Double.parseDouble(data.getStringExtra("lgt")));

                mArrayDestInfo.add(dData);
                mArrayDestName.add(dData.getDestName());

                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(LOG_TAG, "not ADD RESULT_OK");
            }
        } else if(requestCode == ACTIVITY_RESULT_EDIT_DESTINATION) {
            if (resultCode == RESULT_OK) {
                RawData dData = new RawData();
                dData.setDestName(data.getStringExtra("destname"));
                dData.setAddr(data.getStringExtra("addr"));
                dData.setLat(Double.parseDouble(data.getStringExtra("lat")));
                dData.setLgt(Double.parseDouble(data.getStringExtra("lgt")));

                int idx = data.getIntExtra("index", 0);

                if (idx >= 0 && idx < mArrayDestInfo.size()) {
                    mArrayDestInfo.set(idx, dData);
                    mArrayDestName.set(idx, dData.getDestName());

                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d(LOG_TAG, "not EDIT RESULT_OK");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case EVENT_FINISH :
                    finish();
                    break;
            }
        }
    }
}
