package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.calltooperator.CallToOperatorViewActions;
import com.fasten.executor_driver.presentation.calltooperator.CallToOperatorViewModel;
import javax.inject.Inject;

/**
 * Отображает индикатор звонка к оператору.
 */

public class CallToOperatorFragment extends BaseFragment implements CallToOperatorViewActions {

  private CallToOperatorViewModel callToOperatorViewModel;
  private Context context;
  private boolean pending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setCallToOperatorViewModel(@NonNull CallToOperatorViewModel callToOperatorViewModel) {
    this.callToOperatorViewModel = callToOperatorViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_call_to_operator, container, false);
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    callToOperatorViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    callToOperatorViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void onResume() {
    super.onResume();
    callToOperatorViewModel.callToOperator();
  }

  @Override
  public void showCallToOperatorPending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void showNetworkErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }
}
