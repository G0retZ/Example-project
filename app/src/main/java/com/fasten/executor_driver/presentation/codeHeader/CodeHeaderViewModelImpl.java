package com.fasten.executor_driver.presentation.codeHeader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.DataSharer;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Named;

public class CodeHeaderViewModelImpl extends ViewModel implements CodeHeaderViewModel {

  @NonNull
  private final DataSharer<String> loginSharer;
  @NonNull
  private final MutableLiveData<ViewState<CodeHeaderViewActions>> viewStateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  CodeHeaderViewModelImpl(@Named("loginSharer") @NonNull DataSharer<String> loginSharer) {
    this.loginSharer = loginSharer;
    viewStateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CodeHeaderViewActions>> getViewStateLiveData() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = loginSharer.get()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumePhoneNumber, throwable -> {
          });
    }
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }

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
