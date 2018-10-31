package com.cargopull.executor_driver.presentation.upcomingpreordermessage;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.interactor.NotificationMessageUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class UpcomingPreOrderMessageViewModelImpl extends ViewModel implements
    UpcomingPreOrderMessageViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final NotificationMessageUseCase upcomingPreOrderMessagesUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<UpcomingPreOrderMessageViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public UpcomingPreOrderMessageViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull NotificationMessageUseCase upcomingPreOrderMessagesUseCase) {
    this.errorReporter = errorReporter;
    this.upcomingPreOrderMessagesUseCase = upcomingPreOrderMessagesUseCase;
    messageLiveData = new SingleLiveEvent<>();
    loadMissedOrderMessages();
  }

  @NonNull
  @Override
  public LiveData<ViewState<UpcomingPreOrderMessageViewActions>> getViewStateLiveData() {
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
                    upcomingPreOrderMessageViewActions -> upcomingPreOrderMessageViewActions
                        .showUpcomingPreOrderMessage(message)
                );
              }
            },
            errorReporter::reportError
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
