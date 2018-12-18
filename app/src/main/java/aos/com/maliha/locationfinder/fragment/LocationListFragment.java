package aos.com.maliha.locationfinder.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aos.com.maliha.locationfinder.AppViewModel.AppViewModel;
import aos.com.maliha.locationfinder.R;
import aos.com.maliha.locationfinder.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationListFragment extends Fragment {
    private AppViewModel mViewModel;

    public LocationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of((MainActivity)context).get(AppViewModel.class);
    }

}
