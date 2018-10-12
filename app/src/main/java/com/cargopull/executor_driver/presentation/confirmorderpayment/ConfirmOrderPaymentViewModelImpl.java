package com.cargopull.executor_driver.presentation.confirmorderpayment;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class ConfirmOrderPaymentViewModelImpl extends ViewModel implements
    ConfirmOrderPaymentViewModel {

  @NonNull
  private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ConfirmOrderPaymentViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ConfirmOrderPaymentViewModelImpl(
      @NonNull ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase) {
    this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(confirmOrderPaymentViewActions ->
        confirmOrderPaymentViewActions.ConfirmOrderPaymentPending(false));
  }

  @NonNull
  @Override
  public LiveData<ViewState<ConfirmOrderPaymentViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void confirmPayment() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(confirmOrderPaymentViewActions ->
        confirmOrderPaymentViewActions.ConfirmOrderPaymentPending(true));
    disposable = confirmOrderPaymentUseCase.confirmPayment()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
              viewStateLiveData.postValue(confirmOrderPaymentViewActions ->
                  confirmOrderPaymentViewActions.ConfirmOrderPaymentPending(false));
              navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
