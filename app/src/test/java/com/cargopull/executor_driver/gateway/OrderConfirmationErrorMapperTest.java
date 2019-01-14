package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertTrue;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.ServerResponseException;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import retrofit2.HttpException;
import retrofit2.Response;

public class OrderConfirmationErrorMapperTest {

  private Mapper<Throwable, Throwable> mapper;

  @Before
  public void setUp() {
    mapper = new OrderConfirmationErrorMapper();
  }

  /**
   * Должен успешно преобразовать ошибку с кодом 410 в ошибку неудачи принятия предложения заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map410toOrderConfirmationFailedException() throws Exception {
    // Действие:
    Throwable error = mapper.map(new ServerResponseException("410", "You are slowpoke!"));

    // Результат:
    assertTrue(error instanceof OrderConfirmationFailedException);
  }

  /**
   * Не должен преобразовывать ошибку с другим кодом.
   *
   * @throws Exception ошибка
   */
  @Test
  public void map422_2toHttpException() throws Exception {
    // Действие:
    Throwable error = mapper.map(new ServerResponseException("410.0", "You are fake!"));

    // Результат:
    assertTrue(error instanceof ServerResponseException);
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
            Response.error(700, ResponseBody
                .create(MediaType.parse("application/json"), "{'message':'Error'}"))
        )
    );

    // Результат:
    assertTrue(error instanceof HttpException);
  }
}