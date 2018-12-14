package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
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
  private int currentVisibility;

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
    rootView.setVisibility(currentVisibility = connected ? View.GONE : View.VISIBLE);
  }

  public void blink() {
    rootView.setVisibility(View.VISIBLE);
    Animation anim = new AlphaAnimation(0.5f, 1f);
    anim.setInterpolator(new DecelerateInterpolator());
    anim.setDuration(300); //You can manage the blinking time with this parameter
    anim.setRepeatMode(Animation.REVERSE);
    anim.setRepeatCount(4);
    anim.setAnimationListener(new AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        rootView.setVisibility(currentVisibility);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });
    rootView.startAnimation(anim);
  }
}
