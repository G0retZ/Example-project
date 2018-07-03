package com.fasten.executor_driver.view;

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

  private View rootView;
  private CallToOperatorViewModel callToOperatorViewModel;

  @Inject
  public void setCallToOperatorViewModel(@NonNull CallToOperatorViewModel callToOperatorViewModel) {
    this.callToOperatorViewModel = callToOperatorViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return rootView = inflater.inflate(R.layout.fragment_call_to_operator, container, false);
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

  public void callToOperator() {
    callToOperatorViewModel.callToOperator();
  }

  @Override
  public void showCallingToOperator(boolean calling) {
    rootView.setVisibility(calling ? View.VISIBLE : View.GONE);
  }
}
