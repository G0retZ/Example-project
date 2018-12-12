package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Problem;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class ReportProblemUseCaseImpl implements ReportProblemUseCase {

  @NonNull
  private final ReportProblemGateway gateway;
  @Nullable
  private Single<List<Problem>> listSingle;

  @Inject
  public ReportProblemUseCaseImpl(@NonNull ReportProblemGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Single<List<Problem>> getAvailableProblems() {
    if (listSingle == null) {
      return listSingle = gateway.getProblems().observeOn(Schedulers.single()).cache();
    }
    return listSingle;
  }

  @NonNull
  @Override
  public Completable reportProblem(@NonNull Problem problem) {
    return getAvailableProblems()
        .map(cancelOrderReasons -> {
          if (!cancelOrderReasons.contains(problem)) {
            throw new IndexOutOfBoundsException(
                "Невереная причина отказа: " + problem + "."
                    + "Доступные причины отказа: " + cancelOrderReasons);
          }
          return problem;
        }).subscribeOn(Schedulers.single())
        .flatMapCompletable(gateway::reportProblem)
        .observeOn(Schedulers.single());
  }
}
