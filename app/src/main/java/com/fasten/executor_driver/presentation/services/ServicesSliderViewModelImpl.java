package com.fasten.executor_driver.presentation.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import javax.inject.Inject;

public class ServicesSliderViewModelImpl extends ViewModel implements ServicesSliderViewModel,
    ViewState<ServicesSliderViewActions> {

  @NonNull
  private final ServicesListItems servicesListItems;
  @NonNull
  private final MutableLiveData<ViewState<ServicesSliderViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;

  @Inject
  public ServicesSliderViewModelImpl(@NonNull ServicesListItems servicesListItems) {
    this.servicesListItems = servicesListItems;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ServicesSliderViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void refresh() {
    viewStateLiveData.postValue(this);
  }

  @Override
  public void apply(@NonNull ServicesSliderViewActions servicesSliderViewActions) {
    servicesSliderViewActions.setSliderPosition(servicesListItems.getCurrentPosition());
    servicesSliderViewActions.setMaxPrice(servicesListItems.getMaxPrice());
    servicesSliderViewActions.setMinPrice(servicesListItems.getMinPrice());
  }
}
