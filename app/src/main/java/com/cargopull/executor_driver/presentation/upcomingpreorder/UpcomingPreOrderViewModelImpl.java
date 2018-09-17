package com.cargopull.executor_driver.presentation.upcomingpreorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.NotificationMessageUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class UpcomingPreOrderViewModelImpl extends ViewModel implements UpcomingPreOrderViewModel {

  @NonNull
  private final NotificationMessageUseCase upcomingPreOrderMessagesUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<UpcomingPreOrderViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public UpcomingPreOrderViewModelImpl(
      @NonNull NotificationMessageUseCase upcomingPreOrderMessagesUseCase) {
    this.upcomingPreOrderMessagesUseCase = upcomingPreOrderMessagesUseCase;
    messageLiveData = new SingleLiveEvent<>();
    loadMissedOrderMessages();
  }

  @NonNull
  @Override
  public LiveData<ViewState<UpcomingPreOrderViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  private void loadMissedOrderMessages() {
    disposable.dispose();
    disposable = upcomingPreOrderMessagesUseCase.getNotificationMessages()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    upcomingPreOrderViewActions -> upcomingPreOrderViewActions
                        .showUpcomingPreOrderMessage(message)
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
