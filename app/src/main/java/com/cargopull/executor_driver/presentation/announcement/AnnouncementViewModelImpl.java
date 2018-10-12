package com.cargopull.executor_driver.presentation.announcement;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.presentation.ViewState;
import javax.inject.Inject;

public class AnnouncementViewModelImpl extends ViewModel implements AnnouncementViewModel {

  @NonNull
  private final MutableLiveData<ViewState<AnnouncementStateViewActions>> messageLiveData;

  @Inject
  public AnnouncementViewModelImpl() {
    messageLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<AnnouncementStateViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void announcementConsumed() {
    messageLiveData.postValue(null);
  }

  @Override
  public void postMessage(@NonNull String message) {
    if (!message.trim().isEmpty()) {
      messageLiveData.postValue(
          updateMessageViewActions -> updateMessageViewActions.showAnnouncementMessage(message)
      );
    }
  }
}
