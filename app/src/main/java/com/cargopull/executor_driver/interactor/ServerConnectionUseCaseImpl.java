package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.DeprecatedVersionException;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class ServerConnectionUseCaseImpl implements ServerConnectionUseCase {

  @NonNull
  private final ServerConnectionGateway serverConnectionGateway;
  @Nullable
  private Flowable<Boolean> connectFlowable;

  public ServerConnectionUseCaseImpl(@NonNull ServerConnectionGateway serverConnectionGateway) {
    this.serverConnectionGateway = serverConnectionGateway;
  }

  @Override
  public Flowable<Boolean> connect() {
    if (connectFlowable == null) {
      connectFlowable = serverConnectionGateway.openSocket()
          .observeOn(Schedulers.single())
          .retryWhen(
              failed -> failed.concatMap(
                  throwable -> {
                    if (throwable instanceof AuthorizationException
                        || throwable instanceof DeprecatedVersionException) {
                      return Flowable.<Long>error(throwable);
                    } else {
                      throwable.printStackTrace();
                      return Flowable.timer(15, TimeUnit.SECONDS);
                    }
                  }
              )
          ).share()
          .doOnTerminate(() -> connectFlowable = null);
    }
    return connectFlowable;
  }
}
