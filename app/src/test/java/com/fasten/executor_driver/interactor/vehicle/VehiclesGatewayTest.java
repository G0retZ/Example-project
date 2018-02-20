package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.VehiclesGatewayImpl;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehiclesGatewayTest {

  private VehiclesGateway vehiclesGateway;

  @Mock
  private ApiService api;

  @Mock
  private Mapper<ApiVehicle, Vehicle> mapper;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    vehiclesGateway = new VehiclesGatewayImpl(api, mapper);
    when(api.getCars()).thenReturn(Single.never());
    when(mapper.map(any(ApiVehicle.class))).thenReturn(
        new Vehicle(1, "m", "m", "c", "l", false)
    );
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ список ТС исполнителя
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForHeatMap() throws Exception {
    // Действие:
    vehiclesGateway.getExecutorVehicles();

    // Результат:
    verify(api, only()).getCars();
  }

  /* Проверяем работу с преобразователем */

  /**
   * Должен запросить все преобразования
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForMapping() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие:
    vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    verify(mapper, times(3)).map(new ApiVehicle());
    verifyNoMoreInteractions(mapper);
  }

  /**
   * Должен запросить только первое преобразование
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForFirstMappingOnly() throws Exception {
    // Дано:
    when(mapper.map(any(ApiVehicle.class))).thenThrow(new DataMappingException());
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие:
    vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    verify(mapper, only()).map(new ApiVehicle());
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.error(new NoNetworkException()));

    // Действие и Результат:
    vehiclesGateway.getExecutorVehicles().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой преобразования
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    when(mapper.map(any(ApiVehicle.class))).thenThrow(new DataMappingException());
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие и Результат:
    vehiclesGateway.getExecutorVehicles().test().assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список бизнес ТС
   *
   * @throws Exception error
   */
  @Test
  public void answerWithHeatMapData() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие и Результат:
    vehiclesGateway.getExecutorVehicles().test().assertComplete();
    vehiclesGateway.getExecutorVehicles().test().assertValue(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(1, "m", "m", "c", "l", false)
    ));
  }
}
