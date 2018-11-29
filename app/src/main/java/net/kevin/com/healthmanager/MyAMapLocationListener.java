package net.kevin.com.healthmanager;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

/*
 *@author panlister
 */
public class MyAMapLocationListener implements AMapLocationListener {

    private String myLocation = null;
    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location location = new Location();
                location.setLatitude(aMapLocation.getLatitude());
                location.setLongitude(aMapLocation.getLongitude());
                location.setAddress(aMapLocation.getAddress());
                location.setCountry(aMapLocation.getCountry());
                location.setCity(aMapLocation.getCity());
                location.setDistrict(aMapLocation.getDistrict());
                location.setStreet(aMapLocation.getStreet());
                location.setStreetNum(aMapLocation.getStreetNum());
                location.setCityCode(aMapLocation.getCityCode());
                location.setAdCode(aMapLocation.getAdCode());
                location.setPoiName(aMapLocation.getPoiName());
                location.setAoiName(aMapLocation.getAoiName());
                location.setErrorCode(aMapLocation.getErrorCode());
                Gson gson = new Gson();
                String json = gson.toJson(location);
                myLocation = json;
            }
        }).start();

    }

    public String getMyLocation(){
        return myLocation;
    }
}
