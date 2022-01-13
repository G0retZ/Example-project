package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertTrue;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.ServerResponseException;
import com.cargopull.executor_driver.entity.DriverBlockedException;

import org.junit.Before;
import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class VehiclesAndOptionsErrorMapperTest {

  private Mapper<Throwable, Throwable> mapper;

  @Before
  public void setUp() {
    mapper = new VehiclesAndOptionsErrorMapper();
  }

  /**
   * Должен успешно преобразовать ошибку с кодом 422.1 в ошибку заблокированного водителя.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map422_1toDriverBlockedException() throws Exception {
    // Action:
    Throwable error = mapper.map(new ServerResponseException("422.1", "You are scum!"));

    // Effect:
    assertTrue(error instanceof DriverBlockedException);
  }

  /**
   * Не должен преобразовывать ошибку с другим кодом.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map422_2toHttpException() throws Exception {
    // Action:
    Throwable error = mapper.map(new ServerResponseException("422.2", "You are fake!"));

    // Effect:
    assertTrue(error instanceof ServerResponseException);
  }

  /**
   * Не должен преобразовывать ошибку сети.
   *
   * @throws Exception ошибка
   */
  @Test
  public void noMappingForNoNetworkException() throws Exception {
    // Given и Action:
    Throwable error = mapper.map(new NoNetworkException());

    // Effect:
    assertTrue(error instanceof NoNetworkException);
  }

  /**
   * Не должен преобразовывать прочие ошибки.
   *
   * @throws Exception ошибка
   */
  @Test
  public void noMappingForOtherExceptions() throws Exception {
    // Given и Action:
    Throwable error = mapper.map(
        new HttpException(
            Response.error(700, ResponseBody
                .create(MediaType.parse("application/json"), "{'message':'Error'}"))
        )
    );

    // Effect:
    assertTrue(error instanceof HttpException);
  }
}