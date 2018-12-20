package aos.com.maliha.locationfinder.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import aos.com.maliha.locationfinder.AppViewModel.AppViewModel;
import aos.com.maliha.locationfinder.R;
import aos.com.maliha.locationfinder.activity.MainActivity;
import aos.com.maliha.locationfinder.model.LocationItem;

public class MapFragment extends Fragment {

    private AppViewModel mViewModel;
    private MainActivity activity;
    private GoogleMap mMap;
    private MapView mMapView;
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.map_fragment, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap=googleMap;
                //Toast.makeText(activity, "Ready", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        if(getActivity()!=null) {
//            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                    .findFragmentById(R.id.map);

//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
//        Log.e("Ready","Map ready 1");
//
//        if (mapFragment != null) {
//            Log.e("Ready","Map ready 2");
//            mapFragment.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(final GoogleMap googleMap) {
//                    mMap=googleMap;
//                    Log.e("Ready","Map ready");
//                }
//            });
//        }else
//            Log.e("Ready","Map ready 3");
    }
    public void addToMap(GoogleMap mMap,LocationItem item){
        LatLng location = new LatLng(item.lat, item.lng);
        Marker marker=mMap.addMarker(new MarkerOptions().position(location).title(item.name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        Log.e("Marker","item");

    }

    @Subscribe
    public void loadRes(ArrayList<LocationItem> locationItems){
        //Toast.makeText(activity, "Calling..", Toast.LENGTH_SHORT).show();
        if(mMap==null)
            return;
        mMap.clear();

        for (LocationItem item:locationItems
                ) {
            addToMap(mMap,item);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(MainActivity)context;

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
