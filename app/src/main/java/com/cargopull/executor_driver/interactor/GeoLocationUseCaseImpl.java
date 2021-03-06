package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.GeoLocation;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class GeoLocationUseCaseImpl implements GeoLocationUseCase {

  @NonNull
  private static final Map<ExecutorState, Integer> delays;

  static {
    delays = new HashMap<>();
    delays.put(ExecutorState.BLOCKED, 3600000);
    delays.put(ExecutorState.SHIFT_CLOSED, 3600000);
    delays.put(ExecutorState.SHIFT_OPENED, 180000);
    delays.put(ExecutorState.ONLINE, 15000);
    delays.put(ExecutorState.DRIVER_ORDER_CONFIRMATION, 15000);
    delays.put(ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION, 15000);
    delays.put(ExecutorState.CLIENT_ORDER_CONFIRMATION, 15000);
    delays.put(ExecutorState.MOVING_TO_CLIENT, 15000);
    delays.put(ExecutorState.WAITING_FOR_CLIENT, 15000);
    delays.put(ExecutorState.ORDER_FULFILLMENT, 15000);
    delays.put(ExecutorState.PAYMENT_CONFIRMATION, 15000);
  }

  @NonNull
  private final GeoLocationGateway geoLocationGateway;
  @NonNull
  private final GeoTrackingGateway geoTrackingGateway;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;

  @Nullable
  private Flowable<GeoLocation> geoLocationFlowable;

  @Inject
  public GeoLocationUseCaseImpl(@NonNull GeoLocationGateway geoLocationGateway,
      @NonNull GeoTrackingGateway geoTrackingGateway,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.geoLocationGateway = geoLocationGateway;
    this.geoTrackingGateway = geoTrackingGateway;
    this.executorStateUseCase = executorStateUseCase;
  }

  @Override
  public Flowable<GeoLocation> getGeoLocations() {
    if (geoLocationFlowable == null) {
      geoLocationFlowable = executorStateUseCase.getExecutorStates()
          .onErrorResumeNext(Flowable.empty())
          .map(delays::get)
          .switchMap(geoLocationGateway::getGeoLocations)
          .subscribeOn(Schedulers.single())
          .doOnTerminate(
              () -> geoLocationFlowable = null
          ).switchMap(
              geoLocation -> Flowable.just(geoLocation)
                  .startWith(geoTrackingGateway.sendGeoLocation(geoLocation)
                      .observeOn(Schedulers.single())
                      .onErrorComplete()
                      .toFlowable()
                  )
          ).replay(1)
          .refCount();
    }
    return geoLocationFlowable;
  }
}
