package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertTrue;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import retrofit2.HttpException;
import retrofit2.Response;

public class ErrorMapperTest {

  private Mapper<Throwable, Throwable> mapper;

  @Before
  public void setUp() {
    mapper = new ErrorMapper();
  }

  /**
   * Должен успешно преобразовать 422 ошибку с кодом 422.1 в ошибку заблокированного водителя.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map422_1toDriverBlockedException() throws Exception {
    // Дано:
    Response response = Response.error(
        ResponseBody
            .create(MediaType.parse("application/json"), "{'message':'Error'}"),
        new okhttp3.Response.Builder() //
            .code(422)
            .message("Response.error()")
            .protocol(Protocol.HTTP_1_1)
            .header("Code", "422.1")
            .request(new Request.Builder().url("http://localhost/").build())
            .build()
    );

    // Действие:
    Throwable error = mapper.map(new HttpException(response));

    // Результат:
    assertTrue(error instanceof DriverBlockedException);
  }

  /**
   * Не должен преобразовывать 422 ошибку с другим кодом.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map422_2toHttpException() throws Exception {
    // Дано:
    Response response = Response.error(
        ResponseBody
            .create(MediaType.parse("application/json"), "{'message':'Error'}"),
        new okhttp3.Response.Builder() //
            .code(422)
            .message("Response.error()")
            .protocol(Protocol.HTTP_1_1)
            .header("Code", "422.2")
            .request(new Request.Builder().url("http://localhost/").build())
            .build()
    );

    // Действие:
    Throwable error = mapper.map(new HttpException(response));

    // Результат:
    assertTrue(error instanceof HttpException);
  }

  /**
   * Не должен преобразовывать ошибку сети.
   *
   * @throws Exception ошибка
   */
  @Test
  public void noMappingForNoNetworkException() throws Exception {
    // Дано и Действие:
    Throwable error = mapper.map(new NoNetworkException());

    // Результат:
    assertTrue(error instanceof NoNetworkException);
  }

  /**
   * Не должен преобразовывать прочие ошибки.
   *
   * @throws Exception ошибка
   */
  @Test
  public void noMappingForOtherExceptions() throws Exception {
    // Дано и Действие:
    Throwable error = mapper.map(
        new HttpException(
            Response.error(418, ResponseBody
                .create(MediaType.parse("application/json"), "{'message':'Error'}"))
        )
    );

    // Результат:
    assertTrue(error instanceof HttpException);
  }
}