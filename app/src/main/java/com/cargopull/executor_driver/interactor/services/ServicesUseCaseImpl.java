package com.cargopull.executor_driver.interactor.services;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServicesUseCaseImpl implements ServicesUseCase {

  @NonNull
  private final ServicesGateway gateway;

  @Inject
  public ServicesUseCaseImpl(@NonNull ServicesGateway gateway) {
    this.gateway = gateway;
  }

  @Override
  public Completable autoAssignServices() {
    return gateway.getServices().observeOn(Schedulers.single())
        .map(services -> {
          if (services.isEmpty()) {
            throw new EmptyListException("Нет доступных услуг.");
          }
          for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            if (!service.isSelected()) {
              services.set(i, service.setSelected(true));
            }
          }
          return services;
        }).flatMapCompletable(gateway::sendSelectedServices)
        .observeOn(Schedulers.single());
  }
}
