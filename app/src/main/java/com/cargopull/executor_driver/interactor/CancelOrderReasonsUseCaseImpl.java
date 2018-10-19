package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class CancelOrderReasonsUseCaseImpl implements CancelOrderReasonsUseCase {

  @NonNull
  private final CommonGateway<List<CancelOrderReason>> gateway;
  @Nullable
  private Flowable<List<CancelOrderReason>> cancelOrderReasonsFlowable;

  @Inject
  public CancelOrderReasonsUseCaseImpl(@NonNull CommonGateway<List<CancelOrderReason>> gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> getCancelOrderReasons() {
    if (cancelOrderReasonsFlowable == null) {
      cancelOrderReasonsFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .replay(1)
          .refCount();
    }
    return cancelOrderReasonsFlowable;
  }
}
