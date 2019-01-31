package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiProblem;
import com.cargopull.executor_driver.entity.Problem;
import javax.inject.Inject;

/**
 * Преобразуем проблему заказа из ответа сервера в бизнес объект проблемы.
 */
public class ProblemApiMapper implements Mapper<ApiProblem, Problem> {

  @Inject
  public ProblemApiMapper() {
  }

  @NonNull
  @Override
  public Problem map(@NonNull ApiProblem from) throws Exception {
    if (from.getDescription() == null) {
      throw new DataMappingException(
          "Ошибка маппинга: нет причины проблемы для ИД = " + from.getId() + " !");
    }
    return new Problem(from.getId(), from.getDescription(), from.getName());
  }
}
