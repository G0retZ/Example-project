package com.cargopull.executor_driver.di;

import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.annotations.NonNull;
import ua.naiksoftware.stomp.client.StompClient;

interface BackendComponent {

  @NonNull
  EventLogger getEventLogger();

  @NonNull
  ErrorReporter getErrorReporter();

  @NonNull
  ApiService getApiService();

  @NonNull
  StompClient getStompClient();

  @NonNull
  TopicListener getPersonalTopicListener(@NonNull DataReceiver<String> loginReceiver);

  @NonNull
  AppSettingsService getAppSettingsService();

  @NonNull
  GeolocationCenter getGeolocationCenter();
}
