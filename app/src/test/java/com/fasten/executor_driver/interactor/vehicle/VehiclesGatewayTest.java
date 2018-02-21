package com.fasten.executor_driver.interactor.vehicle;

import static org.junit.Assert.assertTrue;
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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehiclesGatewayTest {

  private VehiclesGateway vehiclesGateway;

  @Mock
  private ApiService api;

  @Mock
  private Mapper<ApiVehicle, Vehicle> vehicleMapper;

  @Mock
  private Mapper<Throwable, Throwable> errorMapper;

  @Captor
  private ArgumentCaptor<Throwable> throwableCaptor;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    vehiclesGateway = new VehiclesGatewayImpl(api, vehicleMapper, errorMapper);
    when(api.getCars()).thenReturn(Single.never());
    when(vehicleMapper.map(any(ApiVehicle.class))).thenReturn(
        new Vehicle(1, "m", "m", "c", "l", false)
    );
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ список ТС исполнителя.
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

  /* Проверяем работу с преобразователем данных */

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleMapperForMapping() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие:
    vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    verify(vehicleMapper, times(3)).map(new ApiVehicle());
    verifyNoMoreInteractions(vehicleMapper);
  }

  /**
   * Должен запросить только первое преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleMapperForFirstMappingOnly() throws Exception {
    // Дано:
    when(vehicleMapper.map(any(ApiVehicle.class))).thenThrow(new DataMappingException());
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие:
    vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    verify(vehicleMapper, only()).map(new ApiVehicle());
  }

  /* Проверяем работу с преобразователем ошибок */

  /**
   * Должен запросить преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askErrorMapperForMapping() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    verify(errorMapper, only()).map(throwableCaptor.capture());
    assertTrue(throwableCaptor.getValue() instanceof NoNetworkException);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new NoNetworkException());

    // Действие и Результат:
    vehiclesGateway.getExecutorVehicles().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить преобразованной ошибкой аргумента.
   *
   * @throws Exception error
   */
  @Test
  public void answerIllegalArgumentError() throws Exception {
    // Дано:
    when(api.getCars()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new IllegalArgumentException());

    // Действие и Результат:
    vehiclesGateway.getExecutorVehicles().test().assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить ошибкой преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Дано:
    when(vehicleMapper.map(any(ApiVehicle.class))).thenThrow(new DataMappingException());
    when(errorMapper.map(any())).thenReturn(new DataMappingException());
    when(api.getCars()).thenReturn(Single.just(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    )));

    // Действие:
    TestObserver testObserver = vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    System.out.println(testObserver.errors());
    testObserver.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список бизнес ТС.
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

    // Действие:
    TestObserver<List<Vehicle>> testObserver = vehiclesGateway.getExecutorVehicles().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(1, "m", "m", "c", "l", false)
    ));
  }
}
