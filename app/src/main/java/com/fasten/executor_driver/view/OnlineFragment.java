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
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import javax.inject.Inject;

public class OnlineFragment extends BaseFragment implements OnlineSwitchViewActions {

  private OnlineSwitchViewModel onlineSwitchViewModel;
  @NonNull
  private final OnCheckedChangeListener onCheckedChangeListener =
      (buttonView, isChecked) -> onlineSwitchViewModel.setNewState(isChecked);

  @Nullable
  private SwitchCompat switchCompat;

  @Inject
  public void setOnlineSwitchViewModel(OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    switchCompat = (SwitchCompat) inflater.inflate(R.layout.fragment_online, container, false);
    return switchCompat;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    onlineSwitchViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    onlineSwitchViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (switchCompat != null) {
      switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
    }
  }

  @Override
  public void checkSwitch(boolean check) {
    if (switchCompat != null) {
      switchCompat.setOnCheckedChangeListener(null);
      switchCompat.setChecked(check);
      switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
    }
  }

  @Override
  public void showSwitchPending(boolean show) {
    showPending(show);
  }
}