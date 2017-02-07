package com.seunindustries.finddrive;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.location.Geocoder;
/*
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;
*/

import java.io.IOException;
import java.util.List;

public class AddDestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = AddDestActivity.class.getSimpleName();

    EditText mEditName;
    EditText mEditAddr;
    EditText mEditLat;
    EditText mEditLgt;

    Button mGetLatLgt;
    Button mRegister;
    Button mCancel;

    int mIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dest);

        mEditName = (EditText) findViewById(R.id.editdestname);
        mEditAddr = (EditText) findViewById(R.id.editaddr);
        mEditLat = (EditText) findViewById(R.id.editlat);
        mEditLgt = (EditText) findViewById(R.id.editlgt);

        mGetLatLgt = (Button) findViewById(R.id.getlatlgt);
        mGetLatLgt.setOnClickListener(this);

        mRegister = (Button) findViewById(R.id.register);
        mRegister.setOnClickListener(this);

        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        Intent intent = getIntent();

        String name = intent.getStringExtra("destname");

        if (!TextUtils.isEmpty(name)) {
            mEditName.setText(name);
            mEditAddr.setText(intent.getStringExtra("addr"));
            mEditLat.setText(intent.getStringExtra("lat"));
            mEditLgt.setText(intent.getStringExtra("lgt"));

            mIndex = intent.getIntExtra("index", 0);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                if (!TextUtils.isEmpty(mEditName.getText().toString())
                        || !TextUtils.isEmpty(mEditLat.getText().toString())
                        || !TextUtils.isEmpty(mEditLgt.getText().toString())
                        || !TextUtils.isEmpty(mEditAddr.getText().toString())) {

                    Intent intent = new Intent();
                    intent.putExtra("destname", mEditName.getText().toString());
                    intent.putExtra("addr", mEditAddr.getText().toString());
                    intent.putExtra("lat", mEditLat.getText().toString());
                    intent.putExtra("lgt", mEditLgt.getText().toString());
                    intent.putExtra("index", mIndex);

                    setResult(Activity.RESULT_OK, intent);

                    finish();
                } else {
                    Toast.makeText(this, R.string.inputalldata, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.getlatlgt:
                double[] coords = findGeoPoint(mEditAddr.getText().toString());
                if (coords != null) {
                    mEditLat.setText(coords[0] + "");
                    mEditLgt.setText(coords[1] + "");
                } else {
                    Toast.makeText(this, R.string.cannotfindaddress, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    /**
     * fron address to latlgt
     *
     * @param address
     */
    private double[] findGeoPoint(String address) {
        Geocoder geocoder = new Geocoder(this);
        Address addr;

        double[] location = null;
        try {
            List<Address> listAddress = geocoder.getFromLocationName(address, 5);
            if (listAddress.size() > 0) { // 주소값이 존재 하면

                int nSize = listAddress.size();

                for (int i = 0; i < nSize; i++) {
                    addr = listAddress.get(i); // Address형태로
                    location = new double[2];
                    location[0] = addr.getLatitude();
                    location[1] = addr.getLongitude();

                    addr.hasLatitude();
                    addr.hasLongitude();
                    addr.getAddressLine(0);

                    Log.d(LOG_TAG, "hasLatitude : " + addr.hasLatitude() + ", hasLongitude : " + addr.hasLongitude());
                    Log.d(LOG_TAG, "getAddressLine : " + addr.getAddressLine(0));
                    Log.d(LOG_TAG, "latitude1 : " + addr.getLatitude() + ", longitude1 : " + addr.getLongitude());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

/*
    private Float[] geoCoding(String location) {
        if (location == null)
            return null;

        Geocoder geocoder = new Geocoder();

        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(location).setLanguage("ko").getGeocoderRequest();
        GeocodeResponse geocoderResponse;

        try {
            geocoderResponse = geocoder.geocode(geocoderRequest);
            if (geocoderResponse.getStatus() == GeocoderStatus.OK & !geocoderResponse.getResults().isEmpty()) {

                GeocoderResult geocoderResult=geocoderResponse.getResults().iterator().next();
                LatLng latitudeLongitude = geocoderResult.getGeometry().getLocation();

                Float[] coords = new Float[2];
                coords[0] = latitudeLongitude.getLat().floatValue();
                coords[1] = latitudeLongitude.getLng().floatValue();
                return coords;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
*/
}
