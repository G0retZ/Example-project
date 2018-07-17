package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewActions;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModel;
import javax.inject.Inject;

/**
 * Отображает индикатор звонка к клиенту.
 */

public class CallToClientFragment extends BaseFragment implements CallToClientViewActions {

  private View rootView;
  private CallToClientViewModel callToClientViewModel;

  @Inject
  public void setCallToClientViewModel(@NonNull CallToClientViewModel callToClientViewModel) {
    this.callToClientViewModel = callToClientViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return rootView = inflater.inflate(R.layout.fragment_call_to_client, container, false);
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    callToClientViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    callToClientViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  public void callToClient() {
    callToClientViewModel.callToClient();
  }

  @Override
  public void showCallToClientPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showCallingToClient(boolean calling) {
    rootView.setVisibility(calling ? View.VISIBLE : View.GONE);
  }
}
