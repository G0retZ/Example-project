package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
  private OnlineButtonViewModel onlineButtonViewModel;
  private TextView breakText;
  private Button takeBreakAction;
  private Button resumeWorkAction;

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
    View view = inflater.inflate(R.layout.fragment_online, container, false);
    breakText = view.findViewById(R.id.breakText);
    takeBreakAction = view.findViewById(R.id.takeBreak);
    resumeWorkAction = view.findViewById(R.id.resumeWork);
    takeBreakAction.setOnClickListener(v -> onlineSwitchViewModel.setNewState(false));
    resumeWorkAction.setOnClickListener(v -> onlineSwitchViewModel.setNewState(true));
    return view;
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
  protected void navigate(@NonNull String destination) {
    if (destination.equals(OnlineSwitchNavigate.VEHICLE_OPTIONS)) {
      onlineButtonViewModel.goOnline();
    } else {
      super.navigate(destination);
    }
  }

  @Override
  public void showBreakText(boolean show) {
    breakText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showTakeBreakButton(boolean show) {
    takeBreakAction.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showResumeWorkButton(boolean show) {
    resumeWorkAction.setVisibility(show ? View.VISIBLE : View.GONE);
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
    takeBreakAction.setEnabled(enable);
    resumeWorkAction.setEnabled(enable);
  }
}
