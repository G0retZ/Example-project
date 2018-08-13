package com.cargopull.executor_driver.interactor.vehicle;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.cargopull.executor_driver.backend.web.incoming.ApiVehicle;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
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
public class VehiclesAndOptionsGatewayTest {

  private VehiclesAndOptionsGateway gateway;

  @Mock
  private ApiService api;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;

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
    gateway = new VehiclesAndOptionsGatewayImpl(api, apiOptionMapper, vehicleMapper, errorMapper);
    when(api.getOptionsForOnline()).thenReturn(Single.never());
    when(vehicleMapper.map(any(ApiVehicle.class))).thenReturn(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(2, "ma", "mo", "co", "li", true),
        new Vehicle(3, "man", "mod", "col", "lic", false)
    );
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenReturn(
        new OptionBoolean(1, "n", "d", false, true),
        new OptionNumeric(2, "na", "de", false, 100, 0, 200),
        new OptionBoolean(3, "nam", "des", true, false),
        new OptionNumeric(4, "name", "desc", true, 2, 1, 3)
    );
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ список ТС исполнителя.
   */
  @Test
  public void askApiForOptionsForOnlineOnGetVehicles() {
    // Действие:
    gateway.getExecutorVehicles();

    // Результат:
    verify(api, only()).getOptionsForOnline();
  }

  /**
   * Должен запросить у АПИ список опций исполнителя.
   */
  @Test
  public void askApiForOptionsForOnlineOnGetOptions() {
    // Действие:
    gateway.getExecutorOptions();

    // Результат:
    verify(api, only()).getOptionsForOnline();
  }

  /**
   * Не должен запрашивать у АПИ список ТС исполнителя дважды.
   */
  @Test
  public void doNotAskApiForOptionsForOnlineTwiceAfterVehiclesRequested() {
    // Действие:
    gateway.getExecutorVehicles();
    gateway.getExecutorOptions();

    // Результат:
    verify(api, only()).getOptionsForOnline();
  }

  /**
   * Не должен запрашивать у АПИ список ТС исполнителя дважды.
   */
  @Test
  public void doNotAskApiForOptionsForOnlineTwiceAfterOptionsRequested() {
    // Действие:
    gateway.getExecutorOptions();
    gateway.getExecutorVehicles();

    // Результат:
    verify(api, only()).getOptionsForOnline();
  }

  /* Проверяем работу с преобразователем данных ТС */

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleMapperForMapping() throws Exception {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    gateway.getExecutorVehicles().test();

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
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    gateway.getExecutorVehicles().test();

    // Результат:
    verify(vehicleMapper, only()).map(new ApiVehicle());
  }

  /* Проверяем работу с преобразователем опций исполнителя */

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askOptionsMapperForMapping() throws Exception {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    gateway.getExecutorOptions().test();

    // Результат:
    verify(apiOptionMapper, times(3)).map(new ApiOptionItem());
    verifyNoMoreInteractions(apiOptionMapper);
  }

  /**
   * Должен запросить только первое преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askOptionsMapperForFirstMappingOnly() throws Exception {
    // Дано:
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenThrow(new DataMappingException());
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    gateway.getExecutorOptions().test();

    // Результат:
    verify(apiOptionMapper, only()).map(new ApiOptionItem());
  }

  /* Проверяем работу с преобразователем ошибок */

  /**
   * Должен запросить преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askErrorMapperForMappingAfterVehicleRequest() throws Exception {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    gateway.getExecutorVehicles().test();

    // Результат:
    verify(errorMapper, only()).map(throwableCaptor.capture());
    assertTrue(throwableCaptor.getValue() instanceof NoNetworkException);
  }

  /**
   * Должен запросить преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askErrorMapperForMappingAfterOptionsRequest() throws Exception {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    gateway.getExecutorOptions().test();

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
    when(api.getOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new NoNetworkException());

    // Действие и Результат:
    gateway.getExecutorVehicles().test().assertError(NoNetworkException.class);
    gateway.getExecutorOptions().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить преобразованной ошибкой аргумента.
   *
   * @throws Exception error
   */
  @Test
  public void answerIllegalArgumentError() throws Exception {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new IllegalArgumentException());

    // Действие и Результат:
    gateway.getExecutorVehicles().test()
        .assertError(IllegalArgumentException.class);
    gateway.getExecutorOptions().test()
        .assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить ошибкой преобразования авто.
   *
   * @throws Exception error
   */
  @Test
  public void answerVehicleDataMappingError() throws Exception {
    // Дано:
    when(vehicleMapper.map(any(ApiVehicle.class))).thenThrow(new DataMappingException());
    when(errorMapper.map(any())).thenReturn(new DataMappingException());
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    TestObserver testObserver = gateway.getExecutorVehicles().test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
  }

  /**
   * Должен ответить ошибкой преобразования опции.
   *
   * @throws Exception error
   */
  @Test
  public void answerOptionDataMappingError() throws Exception {
    // Дано:
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenThrow(new DataMappingException());
    when(errorMapper.map(any())).thenReturn(new DataMappingException());
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    TestObserver testObserver = gateway.getExecutorOptions().test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список бизнес ТС.
   */
  @Test
  public void answerWithVehicleEntities() {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    TestObserver<List<Vehicle>> testObserver = gateway.getExecutorVehicles()
        .test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(2, "ma", "mo", "co", "li", true),
        new Vehicle(3, "man", "mod", "col", "lic", false)
    ));
  }

  /**
   * Должен вернуть список бизнес опций.
   */
  @Test
  public void answerWithOptionEntities() {
    // Дано:
    when(api.getOptionsForOnline()).thenReturn(Single.just(new ApiOptionsForOnline(Arrays.asList(
        new ApiVehicle(),
        new ApiVehicle(),
        new ApiVehicle()
    ), Arrays.asList(
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem(),
        new ApiOptionItem()
    ))));

    // Действие:
    TestObserver<List<Option>> testObserver = gateway.getExecutorOptions()
        .test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(Arrays.asList(
        new OptionBoolean(1, "n", "d", false, true),
        new OptionNumeric(2, "na", "de", false, 100, 0, 200),
        new OptionBoolean(3, "nam", "des", true, false),
        new OptionNumeric(4, "name", "desc", true, 2, 1, 3)
    ));
  }
}