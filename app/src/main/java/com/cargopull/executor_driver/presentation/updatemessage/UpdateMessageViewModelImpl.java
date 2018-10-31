package com.cargopull.executor_driver.presentation.updatemessage;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class UpdateMessageViewModelImpl extends ViewModel implements
    UpdateMessageViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final UpdateMessageUseCase updateMessageUseCase;
  @NonNull
  private final MutableLiveData<ViewState<UpdateMessageViewActions>> messageLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public UpdateMessageViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull UpdateMessageUseCase updateMessageUseCase) {
    this.errorReporter = errorReporter;
    this.updateMessageUseCase = updateMessageUseCase;
    messageLiveData = new MutableLiveData<>();
    loadUpdateMessages();
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
  public void messageConsumed() {
    messageLiveData.postValue(null);
  }

  private void loadUpdateMessages() {
    disposable.dispose();
    disposable = updateMessageUseCase.getUpdateMessages()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> {
              if (message != null && !message.trim().isEmpty()) {
                messageLiveData.postValue(
                    updateMessageViewActions -> updateMessageViewActions.showUpdateMessage(message)
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
