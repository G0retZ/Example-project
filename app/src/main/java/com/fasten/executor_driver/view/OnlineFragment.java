package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
  @Nullable
  private Context context;

  @Inject
  public void setOnlineSwitchViewModel(OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
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
    if (switchCompat != null) {
      switchCompat.setOnCheckedChangeListener(onCheckedChangeListener);
    }
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
  public void onDetach() {
    super.onDetach();
    context = null;
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

  @Override
  public void showError(@Nullable @StringRes Integer messageId, boolean retrySocket) {
    if (context != null && messageId != null) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(messageId)
          .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
            if (retrySocket) {
              onlineSwitchViewModel.refreshStates();
            } else {
              onlineSwitchViewModel.consumeServerError();
            }
          })
          .create()
          .show();
    }

  }
}