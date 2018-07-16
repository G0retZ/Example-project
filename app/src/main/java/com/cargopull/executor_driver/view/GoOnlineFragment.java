package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewActions;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import javax.inject.Inject;

/**
 * Отображает кнопку выхода на линию.
 */

public class GoOnlineFragment extends BaseFragment implements OnlineButtonViewActions {

  private OnlineButtonViewModel onlineButtonViewModel;
  private Button goOnlineRequest;
  private boolean pending;

  @Inject
  public void setOnlineButtonViewModel(@NonNull OnlineButtonViewModel onlineButtonViewModel) {
    this.onlineButtonViewModel = onlineButtonViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_go_online, container, false);
    goOnlineRequest = view.findViewById(R.id.goOnline);
    goOnlineRequest.setOnClickListener(v -> onlineButtonViewModel.goOnline());
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
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
  public void enableGoOnlineButton(boolean enable) {
    goOnlineRequest.setEnabled(enable);
  }

  @Override
  public void showGoOnlinePending(boolean show) {
    if (this.pending != show) {
      showPending(pending);
    }
    this.pending = show;
  }
}
