package com.cargopull.executor_driver.presentation.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class MenuViewModelImpl extends ViewModel implements
    MenuViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<FragmentViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public MenuViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.errorReporter = errorReporter;
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadExecutorStates();
  }

  @NonNull
  @Override
  public LiveData<ViewState<FragmentViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadExecutorStates() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new MenuViewStateFilterUnAvailable());
      disposable = executorStateUseCase.getExecutorStates()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              state -> {
                if (state == ExecutorState.SHIFT_OPENED || state == ExecutorState.SHIFT_CLOSED
                    || state == ExecutorState.ONLINE) {
                  viewStateLiveData.postValue(new MenuViewStateFilterAvailable(
                      () -> navigateLiveData.postValue(MenuNavigate.ORDERS_FILTER)
                  ));
                } else {
                  viewStateLiveData.postValue(new MenuViewStateFilterUnAvailable());
                }
              },
              throwable -> {
                errorReporter.reportError(throwable);
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
