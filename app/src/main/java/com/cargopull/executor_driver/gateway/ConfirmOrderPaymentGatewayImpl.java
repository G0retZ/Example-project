package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import javax.inject.Inject;

public class ConfirmOrderPaymentGatewayImpl implements ConfirmOrderPaymentGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public ConfirmOrderPaymentGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable confirmOrderPayment() {
    return apiService.changeOrderStatus(
        Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")
    ).subscribeOn(Schedulers.io());
  }
}
