package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewActions;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import javax.inject.Inject;

/**
 * Отображает индикатор отсутствия подключения к сети.
 */

public class ServerConnectionFragment extends BaseFragment implements ServerConnectionViewActions {

  private ServerConnectionViewModel serverConnectionViewModel;
  private View rootView;

  @Inject
  public void setServerConnectionViewModel(
      @NonNull ServerConnectionViewModel serverConnectionViewModel) {
    this.serverConnectionViewModel = serverConnectionViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_server_connection, container, false);
    return rootView;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    serverConnectionViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void showConnectionReady(boolean connected) {
    rootView.setVisibility(connected ? View.GONE : View.VISIBLE);
  }

  public void blink() {
    Animation anim = new AlphaAnimation(1.0f, 0.8f);
    anim.setDuration(50); //You can manage the blinking time with this parameter
    anim.setStartOffset(20);
    anim.setRepeatMode(Animation.REVERSE);
    anim.setRepeatCount(3);
    rootView.startAnimation(anim);
  }
}
