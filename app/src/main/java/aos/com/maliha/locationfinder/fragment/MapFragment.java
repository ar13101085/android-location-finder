package aos.com.maliha.locationfinder.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aos.com.maliha.locationfinder.AppViewModel.AppViewModel;
import aos.com.maliha.locationfinder.R;
import aos.com.maliha.locationfinder.activity.MainActivity;

public class MapFragment extends Fragment {

    private AppViewModel mViewModel;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of((MainActivity)context).get(AppViewModel.class);
    }
}
