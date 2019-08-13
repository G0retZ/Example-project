package com.cargopull.executor_driver.presentation.missedorder;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.interactor.NotificationMessageUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class MissedOrderViewModelImpl extends ViewModel implements MissedOrderViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final NotificationMessageUseCase missedOrderUseCase;
  @NonNull
  private final ShakeItPlayer shakeItPlayer;
  @NonNull
  private final RingTonePlayer ringTonePlayer;
  @NonNull
  private final SingleLiveEvent<ViewState<MissedOrderViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public MissedOrderViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull NotificationMessageUseCase missedOrderUseCase,
      @NonNull ShakeItPlayer shakeItPlayer,
      @NonNull RingTonePlayer ringTonePlayer) {
    this.errorReporter = errorReporter;
    this.missedOrderUseCase = missedOrderUseCase;
    this.shakeItPlayer = shakeItPlayer;
    this.ringTonePlayer = ringTonePlayer;
    messageLiveData = new SingleLiveEvent<>();
    loadMissedOrderMessages();
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

  private void loadMissedOrderMessages() {
    disposable.dispose();
    disposable = missedOrderUseCase.getNotificationMessages()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              shakeItPlayer.shakeIt(R.raw.skip_order_vibro);
              ringTonePlayer.playRingTone(R.raw.skip_order);
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    missedOrderViewActions -> missedOrderViewActions.showMissedOrderMessage(message)
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
