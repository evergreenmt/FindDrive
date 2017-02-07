package com.seunindustries.finddrive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    Button ListButton;
    Button ManageButton;
    Button ExitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListButton = (Button) findViewById(R.id.listbutton);
        ListButton.setOnClickListener(this);

        ManageButton = (Button) findViewById(R.id.managebutton);
        ManageButton.setOnClickListener(this);

        ExitButton = (Button) findViewById(R.id.exit);
        ExitButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.listbutton:
                intent = new Intent(this, ViewListActivity.class);
                startActivity(intent);
                break;
            case R.id.managebutton:
                intent = new Intent(this, ManageListActivity.class);
                startActivity(intent);
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}
