package com.cargopull.executor_driver.presentation.missedorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.MissedOrderUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class MissedOrderViewModelImpl extends ViewModel implements
    MissedOrderViewModel {

  @NonNull
  private final MissedOrderUseCase missedOrderUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<MissedOrderViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public MissedOrderViewModelImpl(@NonNull MissedOrderUseCase missedOrderUseCase) {
    this.missedOrderUseCase = missedOrderUseCase;
    messageLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<MissedOrderViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void initializeMissedOrderMessages() {
    disposable.dispose();
    disposable = missedOrderUseCase.getMissedOrders()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    missedOrderViewActions -> missedOrderViewActions.showMissedOrderMessage(message)
                );
              }
            },
            throwable -> {
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
