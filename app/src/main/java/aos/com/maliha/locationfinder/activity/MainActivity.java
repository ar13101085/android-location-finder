package aos.com.maliha.locationfinder.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import aos.com.maliha.locationfinder.R;

public class MainActivity extends AppCompatActivity {

    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Toast.makeText(MainActivity.this, "Google api client successfully connected..", Toast.LENGTH_SHORT).show();
                        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                &&  ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        Log.e("LOCATION_UPDATE",location.getLatitude()+","+location.getLongitude());
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).
                addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Connection Failed",connectionResult.getErrorMessage());
                    }
                }).build();

    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location currentLocation) {
                location=currentLocation;
                Log.e("LOCATION_UPDATE",location.getLatitude()+","+location.getLongitude());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isHaveAllPermission=true;
        for (String perm : permissions) {
            if (!hasPermission(perm)) {
                Toast.makeText(this, "Please allow all permission..", Toast.LENGTH_SHORT).show();
                isHaveAllPermission=true;
            }
        }
        if(isHaveAllPermission){
            if (googleApiClient != null) {
                googleApiClient.connect();
            }
        }
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e("LOCATION_UPDATE",location.getLatitude()+","+location.getLongitude());
                }
            });
            googleApiClient.disconnect();
        }
    }
}
