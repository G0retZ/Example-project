package com.fasten.executor_driver.presentation.codeHeader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CodeHeaderViewModelImpl extends ViewModel implements CodeHeaderViewModel {

  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final MutableLiveData<ViewState<CodeHeaderViewActions>> viewStateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public CodeHeaderViewModelImpl(@NonNull DataReceiver<String> loginReceiver) {
    this.loginReceiver = loginReceiver;
    viewStateLiveData = new MutableLiveData<>();
    consumePhoneNumber("00000000000");
  }

  @NonNull
  @Override
  public LiveData<ViewState<CodeHeaderViewActions>> getViewStateLiveData() {
    loadLogin();
    return viewStateLiveData;
  }

  private void loadLogin() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = loginReceiver.get()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .doAfterTerminate(this::loadLogin)
          .subscribe(this::consumePhoneNumber, throwable -> {
          });
    }
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @SuppressWarnings("SameParameterValue")
  private void consumePhoneNumber(@NonNull String phoneNumber) {
    viewStateLiveData.postValue(
        new CodeHeaderViewState(
            phoneNumber.replaceFirst("(\\d)", "+$1 (")
                .replaceFirst("(\\(\\d{3})", "$1) ")
                .replaceFirst("( \\d{3})", "$1-")
                .replaceFirst("(-\\d{2})", "$1-")
        )
    );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
