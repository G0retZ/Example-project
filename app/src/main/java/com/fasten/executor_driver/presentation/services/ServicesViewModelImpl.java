package com.fasten.executor_driver.presentation.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.NoServicesAvailableException;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.interactor.services.ServicesUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServicesViewModelImpl extends ViewModel implements ServicesViewModel {

  private final ServicesUseCase servicesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ServicesViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private final ServicesSliderViewModel servicesSliderViewModel;
  @NonNull
  private final ServicesListItems servicesListItems;
  @Nullable
  private Disposable servicesDisposable;
  @Nullable
  private Disposable setServicesDisposable;

  @Inject
  public ServicesViewModelImpl(ServicesUseCase servicesUseCase,
      @NonNull ServicesSliderViewModel servicesSliderViewModel,
      @NonNull ServicesListItems servicesListItems) {
    this.servicesUseCase = servicesUseCase;
    this.servicesSliderViewModel = servicesSliderViewModel;
    this.servicesListItems = servicesListItems;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new ServicesViewStatePending());
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ServicesViewActions>> getViewStateLiveData() {
    if (servicesDisposable == null) {
      loadServices();
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setServices(List<ServicesListItem> servicesListItems) {
    if (setServicesDisposable != null && !setServicesDisposable.isDisposed()) {
      return;
    }
    ArrayList<Service> services = new ArrayList<>();
    for (ServicesListItem servicesListItem : servicesListItems) {
      services.add(servicesListItem.getService());
    }
    viewStateLiveData.postValue(new ServicesViewStatePending());
    setServicesDisposable = servicesUseCase
        .setSelectedServices(services)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
              ServicesViewStateResolvableError servicesViewStateError;
              if (throwable instanceof NoServicesAvailableException) {
                servicesViewStateError = new ServicesViewStateResolvableError(
                    R.string.no_services_selected,
                    this.servicesListItems.getServicesListItems()
                );
              } else {
                servicesViewStateError = new ServicesViewStateResolvableError(
                    R.string.no_network_connection,
                    this.servicesListItems.getServicesListItems()
                );
              }
              viewStateLiveData.postValue(servicesViewStateError);
            }
        );
  }

  @Override
  public void setSliderPosition(int position) {
    viewStateLiveData.postValue(new ServicesViewStateReady(
        servicesListItems.getServicesListItems(position)
    ));
  }

  @Override
  public void errorConsumed() {
    viewStateLiveData.postValue(new ServicesViewStateReady(
        this.servicesListItems.getServicesListItems()
    ));
  }

  private void loadServices() {
    if (servicesDisposable != null && !servicesDisposable.isDisposed()) {
      return;
    }
    servicesDisposable = servicesUseCase.loadServices()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .flattenAsObservable(services -> services)
        .map(ServicesListItem::new)
        .toList()
        .subscribe(
            items -> {
              servicesListItems.setServicesListItems(items);
              servicesSliderViewModel.refresh();
              viewStateLiveData.postValue(new ServicesViewStateReady(
                  servicesListItems.getServicesListItems()
              ));
            },
            throwable -> {
              ServicesViewStateError servicesViewStateError;
              if (throwable instanceof NoServicesAvailableException) {
                servicesViewStateError = new ServicesViewStateError(R.string.no_services_available);
              } else {
                servicesViewStateError = new ServicesViewStateError(R.string.no_network_connection);
              }
              viewStateLiveData.postValue(servicesViewStateError);
            }
        );

  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (setServicesDisposable != null) {
      setServicesDisposable.dispose();
    }
    if (servicesDisposable != null) {
      servicesDisposable.dispose();
    }
  }
}
