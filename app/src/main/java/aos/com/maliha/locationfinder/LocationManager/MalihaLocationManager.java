package aos.com.maliha.locationfinder.LocationManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MalihaLocationManager {
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private long UPDATE_INTERVAL=5000, FASTEST_INTERVAL=5000;
    //private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    private Context context;

    public MalihaLocationManager(GoogleApiClient googleApiClient, long UPDATE_INTERVAL, long FASTEST_INTERVAL,GoogleApiClient.OnConnectionFailedListener connectionFailedListener,LocationListener locationListener,Context context) {
        this.googleApiClient = googleApiClient;
        this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        this.FASTEST_INTERVAL = FASTEST_INTERVAL;
        //this.connectionCallbacks = connectionCallbacks;
        this.context=context;
        this.connectionFailedListener=connectionFailedListener;
        this.locationListener=locationListener;
        connect();
    }

    @SuppressLint("MissingPermission")
    private void connect() {

        googleApiClient = new GoogleApiClient.Builder(context).
                addApi(LocationServices.API).
                addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        locationRequest = new LocationRequest();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(UPDATE_INTERVAL);
                        locationRequest.setFastestInterval(FASTEST_INTERVAL);

                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,locationListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).
                addOnConnectionFailedListener(connectionFailedListener).build();

        googleApiClient.connect();






    }
}
