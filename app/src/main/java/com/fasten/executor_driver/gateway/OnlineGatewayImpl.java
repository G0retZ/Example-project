package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.interactor.online.OnlineGateway;
import io.reactivex.Completable;
import javax.inject.Inject;

public class OnlineGatewayImpl implements OnlineGateway {

  private int count;

  @Inject
  public OnlineGatewayImpl() {
  }

  @NonNull
  @Override
  public Completable goOnline() {
    Completable completable;
    switch (count % 3) {
      case 0:
        completable = Completable.error(new InsufficientCreditsException("Мало бабок, подкинь чутка."));
        break;
      case 1:
        completable = Completable.error(new DriverBlockedException("Тебя забанил модератор."));
        break;
      case 2:
        completable = Completable.complete();
        break;
      default:
        completable = Completable.error(new NoNetworkException());
    }
    count++;
    return completable;
  }
}
