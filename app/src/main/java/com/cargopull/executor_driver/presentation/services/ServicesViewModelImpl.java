package com.cargopull.executor_driver.presentation.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Service;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServicesViewModelImpl extends ViewModel implements ServicesViewModel {

  private final ServicesUseCase servicesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ServicesViewActions>> viewStateLiveData;
  @NonNull
  private final ServicesSliderViewModel servicesSliderViewModel;
  @NonNull
  private final ServicesListItems servicesListItems;
  @NonNull
  private Disposable servicesDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable setServicesDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public ServicesViewModelImpl(ServicesUseCase servicesUseCase,
      @NonNull ServicesSliderViewModel servicesSliderViewModel,
      @NonNull ServicesListItems servicesListItems) {
    this.servicesUseCase = servicesUseCase;
    this.servicesSliderViewModel = servicesSliderViewModel;
    this.servicesListItems = servicesListItems;
    viewStateLiveData = new MutableLiveData<>();
    loadServices();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ServicesViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void setServices(List<ServicesListItem> servicesListItems) {
    if (!setServicesDisposable.isDisposed()) {
      return;
    }
    ArrayList<Service> services = new ArrayList<>();
    for (ServicesListItem servicesListItem : servicesListItems) {
      services.add(servicesListItem.getService());
    }
    viewStateLiveData.postValue(new ServicesViewStatePending());
    setServicesDisposable = servicesUseCase
        .setSelectedServices(services)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
              ServicesViewStateResolvableError servicesViewStateError;
              if (throwable instanceof EmptyListException) {
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
    servicesSliderViewModel.refresh();
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
    if (!servicesDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new ServicesViewStatePending());
    servicesDisposable = servicesUseCase.loadServices()
        .observeOn(AndroidSchedulers.mainThread())
        .flattenAsObservable(services -> services)
        .map(ServicesListItem::new)
        .toList()
        .subscribe(
            items -> {
              if (items.size() == 1) {
                servicesListItems.setServicesListItems(items);
                viewStateLiveData.postValue(new ServicesViewStateReady(
                    servicesListItems.getServicesListItems(0)
                ));
                servicesSliderViewModel.refresh();
                setServices(servicesListItems.getServicesListItems(0));
              } else {
                servicesListItems.setServicesListItems(items);
                servicesSliderViewModel.refresh();
                viewStateLiveData.postValue(new ServicesViewStateReady(
                    servicesListItems.getServicesListItems()
                ));
              }
            },
            throwable -> {
              ServicesViewStateError servicesViewStateError;
              if (throwable instanceof EmptyListException) {
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
    setServicesDisposable.dispose();
    servicesDisposable.dispose();
  }
}
