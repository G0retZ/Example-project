package com.cargopull.executor_driver.presentation.cancelledorder;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.NotificationMessageUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class CancelledOrderViewModelImpl extends ViewModel implements CancelledOrderViewModel {

  @NonNull
  private final NotificationMessageUseCase cancelledOrderMessageUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<CancelledOrderViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CancelledOrderViewModelImpl(
      @NonNull NotificationMessageUseCase cancelledOrderMessageUseCase) {
    this.cancelledOrderMessageUseCase = cancelledOrderMessageUseCase;
    messageLiveData = new SingleLiveEvent<>();
    loadMissedOrderMessages();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CancelledOrderViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  private void loadMissedOrderMessages() {
    disposable.dispose();
    disposable = cancelledOrderMessageUseCase.getNotificationMessages()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    cancelledOrderViewActions -> cancelledOrderViewActions
                        .showCancelledOrderMessage(message)
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
