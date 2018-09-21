package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewActions;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchNavigate;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewActions;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import javax.inject.Inject;

public abstract class OnlineFragment extends BaseFragment implements OnlineSwitchViewActions,
    OnlineButtonViewActions {

  private OnlineSwitchViewModel onlineSwitchViewModel;
  private OnlineButtonViewModel onlineButtonViewModel;

  @Inject
  public void setOnlineSwitchViewModel(@NonNull OnlineSwitchViewModel onlineSwitchViewModel) {
    this.onlineSwitchViewModel = onlineSwitchViewModel;
  }

  @Inject
  public void setOnlineButtonViewModel(@NonNull OnlineButtonViewModel onlineButtonViewModel) {
    this.onlineButtonViewModel = onlineButtonViewModel;
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
    View takeBreakAction = getTakeBreakAction();
    if (takeBreakAction != null) {
      takeBreakAction.setOnClickListener(v -> onlineSwitchViewModel.setNewState(false));
    }
    View resumeWorkAction = getResumeWorkAction();
    if (resumeWorkAction != null) {
      resumeWorkAction.setOnClickListener(v -> onlineSwitchViewModel.setNewState(true));
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

  @Nullable
  protected abstract View getBreakText();

  @Nullable
  protected abstract View getTakeBreakAction();

  @Nullable
  protected abstract View getResumeWorkAction();

  @Override
  public void showBreakText(boolean show) {
    View breakText = getBreakText();
    if (breakText != null) {
      breakText.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public void showTakeBreakButton(boolean show) {
    View takeBreakAction = getTakeBreakAction();
    if (takeBreakAction != null) {
      takeBreakAction.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public void showResumeWorkButton(boolean show) {
    View resumeWorkAction = getResumeWorkAction();
    if (resumeWorkAction != null) {
      resumeWorkAction.setVisibility(show ? View.VISIBLE : View.GONE);
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
    View takeBreakAction = getTakeBreakAction();
    if (takeBreakAction != null) {
      takeBreakAction.setEnabled(enable);
    }
    View resumeWorkAction = getResumeWorkAction();
    if (resumeWorkAction != null) {
      resumeWorkAction.setEnabled(enable);
    }
  }
}
