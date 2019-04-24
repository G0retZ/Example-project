package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import javax.inject.Inject;

public class CallToClientGatewayImpl implements CallToClientGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public CallToClientGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable callToClient() {
    return apiService.callToClient(
        Collections.singletonMap("status", "CALL_TO_CLIENT")
    ).subscribeOn(Schedulers.io());
  }
}
