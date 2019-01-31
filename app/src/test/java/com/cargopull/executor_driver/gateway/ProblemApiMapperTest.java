package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.web.incoming.ApiProblem;
import com.cargopull.executor_driver.entity.Problem;
import org.junit.Before;
import org.junit.Test;

public class ProblemApiMapperTest {

  private Mapper<ApiProblem, Problem> mapper;

  @Before
  public void setUp() {
    mapper = new ProblemApiMapper();
  }

  /**
   * Должен успешно преобразовать апи объект в проблему.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingApiProblemToProblemSuccess() throws Exception {
    // Дано и Действие:
    Problem problem = mapper.map(new ApiProblem(1, "one", "unused1"));

    // Результат:
    assertEquals(problem.getId(), 1);
    assertEquals(problem.getName(), "one");
    assertEquals(problem.getUnusedName(), "unused1");
  }

  /**
   * Должен успешно преобразовать апи объект с пустым описанием в проблему.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingApiProblemWithEmptyDescriptionToProblemSuccess() throws Exception {
    // Дано и Действие:
    Problem problem = mapper.map(new ApiProblem(1, "", "unused1"));

    // Результат:
    assertEquals(problem.getId(), 1);
    assertEquals(problem.getName(), "");
    assertEquals(problem.getUnusedName(), "unused1");
  }

  /**
   * Должен дать ошибку, если описание в апи объекте null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingApiProblemWithNullDescriptionToProblemFail() throws Exception {
    // Дано, Действие и Результат:
    mapper.map(new ApiProblem(1, null, "unused1"));
  }
}