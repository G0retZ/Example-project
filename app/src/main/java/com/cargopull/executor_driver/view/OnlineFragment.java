package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewActions;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchNavigate;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import javax.inject.Inject;

public class OnlineFragment extends BaseFragment implements OnlineSwitchViewActions,
    OnlineButtonViewActions {

  private OnlineSwitchViewModel onlineSwitchViewModel;
  @NonNull
  private final OnCheckedChangeListener onCheckedChangeListener =
      (buttonView, isChecked) -> onlineSwitchViewModel.setNewState(isChecked);
  private OnlineButtonViewModel onlineButtonViewModel;
  @Nullable
  private SwitchCompat switchCompat;

  @Inject
  public void setOnlineSwitchViewModel(@NonNull OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Inject
  public void setOnlineButtonViewModel(@NonNull OnlineButtonViewModel onlineButtonViewModel) {
    this.onlineButtonViewModel = onlineButtonViewModel;
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
    onlineButtonViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    onlineButtonViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
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
  protected void navigate(@NonNull String destination) {
    if (destination.equals(OnlineSwitchNavigate.VEHICLE_OPTIONS)) {
      onlineButtonViewModel.goOnline();
    } else {
      super.navigate(destination);
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
  public void showSwitchPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showGoOnlinePending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  @Override
  public void enableGoOnlineButton(boolean enable) {
    if (switchCompat != null) {
      switchCompat.setEnabled(enable);
    }
  }
}
