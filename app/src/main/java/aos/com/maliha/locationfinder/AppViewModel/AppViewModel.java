package aos.com.maliha.locationfinder.AppViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import aos.com.maliha.locationfinder.model.LocationItem;

public class AppViewModel extends ViewModel {
    public MutableLiveData<ArrayList<LocationItem>> locationItemList=new MutableLiveData<ArrayList<LocationItem>>();
    public void initLocation(ArrayList<LocationItem> items){
        locationItemList=new MutableLiveData<ArrayList<LocationItem>>();
        locationItemList.setValue(items);
    }

}
