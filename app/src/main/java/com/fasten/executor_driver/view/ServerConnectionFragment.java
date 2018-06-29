package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.serverconnection.ServerConnectionViewActions;
import com.fasten.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
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
    rootView = inflater.inflate(R.layout.fragment_no_connection, container, false);
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
}
