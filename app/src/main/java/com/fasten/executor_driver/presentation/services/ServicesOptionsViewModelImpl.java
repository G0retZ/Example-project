package com.fasten.executor_driver.presentation.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.interactor.services.ServicesUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.presentation.options.OptionsListItem;
import com.fasten.executor_driver.presentation.options.OptionsListItems;
import com.fasten.executor_driver.presentation.options.OptionsViewActions;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.options.OptionsViewStateError;
import com.fasten.executor_driver.presentation.options.OptionsViewStateInitial;
import com.fasten.executor_driver.presentation.options.OptionsViewStatePending;
import com.fasten.executor_driver.presentation.options.OptionsViewStateReady;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

public class ServicesOptionsViewModelImpl extends ViewModel implements OptionsViewModel {

  private final ServicesUseCase servicesUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OptionsViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @Nullable
  private Disposable servicesDisposable;
  @Nullable
  private Disposable setServicesDisposable;

  @Inject
  public ServicesOptionsViewModelImpl(ServicesUseCase servicesUseCase) {
    this.servicesUseCase = servicesUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OptionsViewStateInitial());
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OptionsViewActions>> getViewStateLiveData() {
    if (servicesDisposable == null) {
      loadOptions();
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setOptions(OptionsListItems optionsListItems) {
    if (setServicesDisposable != null && !setServicesDisposable.isDisposed()) {
      return;
    }
    ArrayList<Service> services = new ArrayList<>();
    for (OptionsListItem optionsListItem : optionsListItems.getVehicleOptions()) {
      OptionBoolean optionBoolean = (OptionBoolean) optionsListItem.getOption();
      services.add(
          new Service(
              optionBoolean.getId(),
              optionBoolean.getName(),
              0,//Long.parseLong(optionBoolean.getDescription()),
              optionBoolean.getValue()
          )
      );
    }
    viewStateLiveData.postValue(new OptionsViewStatePending());
    setServicesDisposable = servicesUseCase
        .setSelectedServices(services)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(ServicesOptionsNavigate.READY_FOR_ORDERS),
            throwable -> viewStateLiveData
                .postValue(new OptionsViewStateError(R.string.no_network_connection))
        );
  }

  private void loadOptions() {
    if (servicesDisposable != null && !servicesDisposable.isDisposed()) {
      return;
    }
    servicesDisposable = servicesUseCase.loadServices()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .map(this::map)
        .subscribe(
            items -> viewStateLiveData.postValue(new OptionsViewStateReady(items)),
            Throwable::printStackTrace
        );

  }

  private OptionsListItems map(List<Service> services) {
    ArrayList<OptionsListItem<?>> optionsListItems = new ArrayList<>();
    for (Service service : services) {
      optionsListItems
          .add(new OptionsListItem<>(
              new OptionBoolean(service.getId(), service.getName(),
                  String.format(Locale.getDefault(), "%d рублей за первый час", service.getPrice()),
                  true, service.getValue())
          ));
    }
    return new OptionsListItems(optionsListItems, new ArrayList<>());
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
