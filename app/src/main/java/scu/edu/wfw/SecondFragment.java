package scu.edu.wfw;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.TencentMapGestureListener;

import scu.edu.wfw.databinding.FragmentSecondBinding;

public class SecondFragment extends SupportMapFragment {

    private FragmentSecondBinding binding;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Marker marker;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        preferences = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = preferences.edit();
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        mapV = binding.mapview;
        super.onCreateView(inflater, container, savedInstanceState);
        return binding.getRoot();
    }

    private void updateURL(float lng, float lat) {
        binding.textView.setText(String.format(getString(R.string.location), lng, lat));
        if (marker != null)
            marker.remove();
        marker = getMap().addMarker(new MarkerOptions(new LatLng(lat, lng)));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        String lng = preferences.getString("lng", "104.065901");
        String lat = preferences.getString("lat", "30.635226");
        updateURL(Float.valueOf(lng), Float.valueOf(lat));
        super.onViewCreated(view, savedInstanceState);
        getMap().getUiSettings().setRotateGesturesEnabled(false); // 关闭旋转
        getMap().animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Float.valueOf(lat), Float.valueOf(lng)))); //定位至天府广场
        getMap().addTencentMapGestureListener(new TencentMapGestureListener() {
            @Override
            public boolean onDoubleTap(float v, float v1) {
                return false;
            }

            @Override
            public boolean onSingleTap(float v, float v1) {
                LatLng latLng = getMap().getProjection().fromScreenLocation(new Point((int) v, (int) v1));
                editor.putString("lng", String.valueOf(latLng.getLongitude()));
                editor.putString("lat", String.valueOf(latLng.getLatitude()));
                updateURL((float) latLng.getLongitude(), (float) latLng.getLatitude());
                editor.commit();
                return true;
            }

            @Override
            public boolean onFling(float v, float v1) {
                return false;
            }

            @Override
            public boolean onScroll(float v, float v1) {
                return false;
            }

            @Override
            public boolean onLongPress(float v, float v1) {
                return false;
            }

            @Override
            public boolean onDown(float v, float v1) {
                return false;
            }

            @Override
            public boolean onUp(float v, float v1) {
                return false;
            }

            @Override
            public void onMapStable() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}