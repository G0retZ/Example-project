package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchNavigate;
import com.google.android.gms.maps.GoogleMap;

public class OnlineFragment extends MapFragment {

  @NonNull
  private final OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
    if (isChecked) {
      navigate(OnlineSwitchNavigate.SERVICES);
    }
  };
  private SwitchCompat switchCompat;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_online, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    switchCompat = view.findViewById(R.id.goOnlineSwitch);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    super.onMapReady(googleMap);
    switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
  }
}