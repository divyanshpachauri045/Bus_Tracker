package com.google.bustracker;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    private static final int Error_Dialog_Request  = 9001;
    private static final String TAG = MainActivity.class.getSimpleName();


    //Widgets
    private EditText busname;
    private Button button;
    private Boolean locationPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busname = findViewById(R.id.busnameid);
        button = findViewById(R.id.addid);

        if(isServiceOK()){
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationPermission) {
                    if (!TextUtils.isEmpty(busname.getText().toString())) {

                        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
                        intent.putExtra("busname",busname.getText().toString());
                        Log.d(TAG,"Main busname : "+busname.getText().toString());
                        startService(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Enter Tracker ID Properly", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            locationPermission = true;
        } else {
            finish();
        }
    }


    public boolean isServiceOK(){
        Log.d(TAG,"isServiceOK: Checking Google service");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //Everything is fine and user can make map request
            Log.d(TAG,"isServiceOK: Google Play Service is Working");
            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // An error occur but we can resolve it
            Log.d(TAG,"isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,Error_Dialog_Request);
            dialog.show();
        }
        else{
            Toast.makeText(MainActivity.this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

