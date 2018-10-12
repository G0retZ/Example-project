package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ValidationException;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.EventLogger;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.HashMap;
import javax.inject.Inject;

public class CodeViewModelImpl extends ViewModel implements CodeViewModel {

  @NonNull
  private final PasswordUseCase passwordUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CodeViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final EventLogger eventLogger;
  private final long timeStamp;

  @Inject
  public CodeViewModelImpl(@NonNull PasswordUseCase passwordUseCase,
      @NonNull TimeUtils timeUtils, @NonNull EventLogger eventLogger) {
    this.passwordUseCase = passwordUseCase;
    this.timeUtils = timeUtils;
    this.eventLogger = eventLogger;
    timeStamp = timeUtils.currentTimeMillis();
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new CodeViewStateInitial());
  }

  @NonNull
  @Override
  public LiveData<ViewState<CodeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setCode(@NonNull String code) {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = passwordUseCase.authorize(
        code.replaceAll("[^\\d]", ""),
        Completable.fromAction(() -> viewStateLiveData.postValue(new CodeViewStatePending())
        ).subscribeOn(AndroidSchedulers.mainThread())
    ).observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              HashMap<String, String> params = new HashMap<>();
              params.put("login_delay", String.valueOf(timeUtils.currentTimeMillis() - timeStamp));
              eventLogger.reportEvent("executor_login", params);
              navigateLiveData.postValue(CodeNavigate.ENTER_APP);
            },
            throwable -> {
              if (throwable instanceof NoNetworkException) {
                viewStateLiveData.postValue(new CodeViewStateNetworkError());
              } else if (throwable instanceof ValidationException) {
                if (!(viewStateLiveData.getValue() instanceof CodeViewStateInitial)) {
                  viewStateLiveData.postValue(new CodeViewStateInitial());
                }
              } else {
                viewStateLiveData.postValue(new CodeViewStateError());
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
