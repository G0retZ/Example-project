package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class GeoLocationUseCaseImpl implements GeoLocationUseCase {

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
          .switchMap(executorState -> {
            switch (executorState) {
              case SHIFT_CLOSED:
                return geoLocationGateway.getGeoLocations(3600000);
              case SHIFT_OPENED:
                return geoLocationGateway.getGeoLocations(180000);
              case ONLINE:
                return geoLocationGateway.getGeoLocations(15000);
              default:
                return Flowable.empty();
            }
          }).doOnTerminate(
              () -> geoLocationFlowable = null
          ).doOnNext(
              geoLocation -> geoTrackingGateway.sendGeoLocation(geoLocation)
                  .subscribe(() -> {
                  }, throwable -> {
                  })
          ).replay(1)
          .refCount();
    }
    return geoLocationFlowable;
  }
}
