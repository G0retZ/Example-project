package com.cargopull.executor_driver.presentation.updatemessage;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpdateMessageViewModelImpl extends ViewModel implements
    UpdateMessageViewModel {

  @NonNull
  private final UpdateMessageUseCase updateMessageUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<UpdateMessageViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public UpdateMessageViewModelImpl(@NonNull UpdateMessageUseCase updateMessageUseCase) {
    this.updateMessageUseCase = updateMessageUseCase;
    messageLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<UpdateMessageViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void initializeUpdateMessages() {
    disposable.dispose();
    disposable = updateMessageUseCase.getUpdateMessages()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    updateMessageViewActions -> updateMessageViewActions.showUpdateMessage(message)
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
