package com.cargopull.executor_driver.presentation.announcement;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.DialogViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;

public class AnnouncementViewModelImpl extends ViewModel implements AnnouncementViewModel {

  @NonNull
  private final MutableLiveData<ViewState<DialogViewActions>> messageLiveData;
  @NonNull
  private final Disposable disposable;

  @Inject
  public AnnouncementViewModelImpl(@NonNull CommonGateway<String> gateway) {
    messageLiveData = new MutableLiveData<>();
    disposable = gateway.getData()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::consumeMessage, Throwable::printStackTrace);
  }

  @NonNull
  @Override
  public LiveData<ViewState<DialogViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  private void consumeMessage(@NonNull String message) {
    if (!message.trim().isEmpty()) {
      messageLiveData.postValue(updateMessageViewActions ->
          updateMessageViewActions.showPersistentDialog(
              message, () -> {
                messageLiveData.postValue(null);
              }
          )
      );
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
