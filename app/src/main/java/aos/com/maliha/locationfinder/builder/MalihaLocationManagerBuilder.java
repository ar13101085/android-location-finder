package aos.com.maliha.locationfinder.builder;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import aos.com.maliha.locationfinder.LocationManager.MalihaLocationManager;

public class MalihaLocationManagerBuilder {
    private GoogleApiClient googleApiClient;
    private LocationListener locationListener;
    private long update_interval;
    private long fastest_interval;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    private Context context;




    public MalihaLocationManagerBuilder setUPDATE_INTERVAL(long update_interval) {
        this.update_interval = update_interval;
        return this;
    }

    public MalihaLocationManagerBuilder setFASTEST_INTERVAL(long fastest_interval) {
        this.fastest_interval = fastest_interval;
        return this;
    }


    public MalihaLocationManagerBuilder setConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        this.connectionFailedListener = connectionFailedListener;
        return this;
    }

    public MalihaLocationManagerBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public MalihaLocationManagerBuilder setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
        return this;
    }

    public MalihaLocationManager build() {
        return new MalihaLocationManager(googleApiClient, update_interval, fastest_interval,connectionFailedListener,locationListener,context);
    }

    public void disconnect(){
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
            googleApiClient.disconnect();
        }
    }

}