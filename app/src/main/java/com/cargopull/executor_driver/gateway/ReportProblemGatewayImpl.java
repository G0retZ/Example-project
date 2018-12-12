package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiProblem;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.interactor.ReportProblemGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class ReportProblemGatewayImpl implements ReportProblemGateway {

  @NonNull
  private final ApiService apiService;
  @NonNull
  private final Mapper<ApiProblem, Problem> mapper;

  @Inject
  public ReportProblemGatewayImpl(@NonNull ApiService apiService,
      @NonNull Mapper<ApiProblem, Problem> mapper) {
    this.apiService = apiService;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Single<List<Problem>> getProblems() {
    return apiService.getReportProblems()
        .subscribeOn(Schedulers.io())
        .flattenAsFlowable(apiProblems -> apiProblems)
        .map(mapper::map)
        .toList();
  }

  @NonNull
  @Override
  public Completable reportProblem(@NonNull Problem problem) {
    return apiService.reportProblem((new ApiProblem(problem)))
        .subscribeOn(Schedulers.io());
  }
}
