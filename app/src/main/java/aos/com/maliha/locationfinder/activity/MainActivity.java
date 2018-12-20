package aos.com.maliha.locationfinder.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aos.com.maliha.locationfinder.AppViewModel.AppViewModel;
import aos.com.maliha.locationfinder.R;
import aos.com.maliha.locationfinder.builder.MalihaHttpClientBuilder;
import aos.com.maliha.locationfinder.builder.MalihaLocationManagerBuilder;
import aos.com.maliha.locationfinder.fragment.LocationListFragment;
import aos.com.maliha.locationfinder.fragment.MapFragment;
import aos.com.maliha.locationfinder.httpClient.RequestType;
import aos.com.maliha.locationfinder.model.LocationItem;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Location currentUserLocation;
    private MalihaLocationManagerBuilder locationManagerBuilder;
    private LocationListener locationListener;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    private TextView locationText;
    private TextView filterName;
    private ImageView searchBtn;
    private EditText keyword;
    AppViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText=findViewById(R.id.locationText);
        filterName=findViewById(R.id.filterName);
        searchBtn=findViewById(R.id.searchBtn);
        keyword=findViewById(R.id.keyword);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        filterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(MainActivity.this, filterName);

                menu.getMenu().add("restaurant");
                menu.getMenu().add("atm");
                menu.getMenu().add("hospital");
                menu.getMenu().add("school");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        filterName.setText(item.getTitle());
                        return true;
                    }
                });

                menu.show();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchNearByPlace(currentUserLocation);
            }
        });

        tabSetup();
        takePermission();
        listenerSetup();

    }

    private void tabSetup() {

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Map", MapFragment.class)
                //.add("List", LocationListFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    private void takePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
        }
    }



    private void listenerSetup() {
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Location Update",location.getLatitude()+","+location.getLongitude());
                currentUserLocation=location;
                currentLocationUpdate(location);
            }
        };


        connectionFailedListener=new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.e("Failed",connectionResult.getErrorMessage());
                Toast.makeText(MainActivity.this, "Connection failed...", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void searchNearByPlace(Location location){
        String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+currentUserLocation.getLatitude()+","+currentUserLocation.getLongitude()+"&radius=10000&type="+filterName.getText().toString()+"&keyword="+keyword.getText().toString()+"&key=AIzaSyDsj__sDfjvf0gm9B6POx1PtHRrHf2D3KE";
        System.out.println(url);
        new MalihaHttpClientBuilder()
                .setRequestType(RequestType.GET)
                .setUrl(url)
                .setResponseHandler(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.e("response",new String(responseBody));
                        try {

                            JSONObject obj=new JSONObject(new String(responseBody));
                            JSONArray res=obj.getJSONArray("results");
                            if(res.length()>0){
                                ArrayList<LocationItem> items=new ArrayList<>();
                                for (int i = 0; i < res.length(); i++) {
                                    JSONObject type=res.getJSONObject(i);
                                    String name=type.getString("name");
                                    double lat=type.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    double lng=type.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                    items.add(new LocationItem(name,lat,lng));

                                }
                                EventBus.getDefault().post(items);
                            }else{
                                Toast.makeText(MainActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                })
                .build();
    }

    private void currentLocationUpdate(Location location){
        getLocationAddress(new LatLng(location.getLatitude(),location.getLongitude()));
//        new MalihaHttpClientBuilder()
//                .setRequestType(RequestType.GET)
//                .setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&key=AIzaSyDsj__sDfjvf0gm9B6POx1PtHRrHf2D3KE")
//                .setResponseHandler(new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        Log.e("Success","My Location Update "+new String(responseBody));
//                        try {
//                            JSONObject obj=new JSONObject(new String(responseBody));
//                            JSONArray array=obj.getJSONArray("results");
//                            if(array.length()>0){
//                                locationText.setText(array.getJSONObject(0).getString("formatted_address"));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        Log.e("Result","Failed..");
//                    }
//                })
//                .build();
    }


    public String getLocationAddress(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String _Location = "";
                    if (listAddresses.get(0).getAddressLine(0) != null) {
                        _Location = _Location + listAddresses.get(0).getAddressLine(0) + ",";
                    }
                    if (listAddresses.get(0).getSubLocality() != null) {
                        _Location = _Location + listAddresses.get(0).getSubLocality() + ",";
                    }
                    if (listAddresses.get(0).getLocality() != null) {
                        _Location = _Location + listAddresses.get(0).getLocality();
                    }
                    locationText.setText(_Location +"("+latLng.latitude+","+latLng.longitude+")");
                    return _Location;
                }
            } catch (IOException e) {
            }
        } catch (Exception e) {
        }
        return "";
    }


    private void locationManagerSetup() {


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
            return;
        }

        locationManagerBuilder=new MalihaLocationManagerBuilder()
                .setLocationListener(locationListener)
                .setConnectionFailedListener(connectionFailedListener)
                .setContext(this)
                .setFASTEST_INTERVAL(1000)
                .setUPDATE_INTERVAL(1000);

        locationManagerBuilder.build();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isHaveAllPermission=true;
        for (String perm : permissions) {
            if (!hasPermission(perm)) {
                isHaveAllPermission=false;
            }
        }
        if(isHaveAllPermission){
            locationManagerSetup();
        }else{
            Toast.makeText(this, "Please allow all permission..", Toast.LENGTH_SHORT).show();
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

        if (locationManagerBuilder != null) {
            locationManagerBuilder.build();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManagerBuilder != null) {
            locationManagerBuilder.disconnect();
        }

    }



}
