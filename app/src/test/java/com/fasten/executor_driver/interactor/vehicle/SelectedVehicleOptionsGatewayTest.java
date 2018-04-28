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
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.web.incoming.ApiSelectedOptionsForOnline;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.SelectedVehicleOptionsGatewayImpl;
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
public class SelectedVehicleOptionsGatewayTest {

  private SelectedVehicleOptionsGateway vehiclesAndOptionsGateway;

  @Mock
  private ApiService api;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Mock
  private Mapper<Throwable, Throwable> errorMapper;

  @Captor
  private ArgumentCaptor<Throwable> throwableCaptor;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    vehiclesAndOptionsGateway = new SelectedVehicleOptionsGatewayImpl(api, apiOptionMapper,
        errorMapper);
    when(api.getSelectedOptionsForOnline()).thenReturn(Single.never());
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
    vehiclesAndOptionsGateway.getVehicleOptions();

    // Результат:
    verify(api, only()).getSelectedOptionsForOnline();
  }

  /**
   * Должен запросить у АПИ список опций исполнителя.
   */
  @Test
  public void askApiForOptionsForOnlineOnGetOptions() {
    // Действие:
    vehiclesAndOptionsGateway.getExecutorOptions();

    // Результат:
    verify(api, only()).getSelectedOptionsForOnline();
  }

  /**
   * Не должен запрашивать у АПИ список ТС исполнителя дважды.
   */
  @Test
  public void doNotAskApiForOptionsForOnlineTwiceAfterVehiclesRequested() {
    // Действие:
    vehiclesAndOptionsGateway.getVehicleOptions();
    vehiclesAndOptionsGateway.getExecutorOptions();

    // Результат:
    verify(api, only()).getSelectedOptionsForOnline();
  }

  /**
   * Не должен запрашивать у АПИ список ТС исполнителя дважды.
   */
  @Test
  public void doNotAskApiForOptionsForOnlineTwiceAfterOptionsRequested() {
    // Действие:
    vehiclesAndOptionsGateway.getExecutorOptions();
    vehiclesAndOptionsGateway.getVehicleOptions();

    // Результат:
    verify(api, only()).getSelectedOptionsForOnline();
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
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    vehiclesAndOptionsGateway.getVehicleOptions().test();

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
  public void askVehicleMapperForFirstMappingOnly() throws Exception {
    // Дано:
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenThrow(new DataMappingException());
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    vehiclesAndOptionsGateway.getVehicleOptions().test();

    // Результат:
    verify(apiOptionMapper, only()).map(new ApiOptionItem());
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
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    vehiclesAndOptionsGateway.getExecutorOptions().test();

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
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    vehiclesAndOptionsGateway.getExecutorOptions().test();

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
    when(api.getSelectedOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    vehiclesAndOptionsGateway.getVehicleOptions().test();

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
    when(api.getSelectedOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    vehiclesAndOptionsGateway.getExecutorOptions().test();

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
    when(api.getSelectedOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new NoNetworkException());

    // Действие и Результат:
    vehiclesAndOptionsGateway.getVehicleOptions().test().assertError(NoNetworkException.class);
    vehiclesAndOptionsGateway.getExecutorOptions().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить преобразованной ошибкой аргумента.
   *
   * @throws Exception error
   */
  @Test
  public void answerIllegalArgumentError() throws Exception {
    // Дано:
    when(api.getSelectedOptionsForOnline()).thenReturn(Single.error(new NoNetworkException()));
    when(errorMapper.map(any())).thenReturn(new IllegalArgumentException());

    // Действие и Результат:
    vehiclesAndOptionsGateway.getVehicleOptions().test()
        .assertError(IllegalArgumentException.class);
    vehiclesAndOptionsGateway.getExecutorOptions().test()
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
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenThrow(new DataMappingException());
    when(errorMapper.map(any())).thenReturn(new DataMappingException());
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    TestObserver testObserver = vehiclesAndOptionsGateway.getVehicleOptions().test();

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
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    TestObserver testObserver = vehiclesAndOptionsGateway.getExecutorOptions().test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список бизнес ТС.
   */
  @Test
  public void answerWithVehicleEntities() {
    // Дано:
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    TestObserver<List<Option>> testObserver = vehiclesAndOptionsGateway.getVehicleOptions()
        .test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(Arrays.asList(
        new OptionBoolean(1, "n", "d", false, true),
        new OptionNumeric(2, "na", "de", false, 100, 0, 200),
        new OptionBoolean(3, "nam", "des", true, false)
    ));
  }

  /**
   * Должен вернуть список бизнес опций.
   */
  @Test
  public void answerWithOptionEntities() {
    // Дано:
    when(api.getSelectedOptionsForOnline())
        .thenReturn(Single.just(new ApiSelectedOptionsForOnline(Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ), Arrays.asList(
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem(),
            new ApiOptionItem()
        ))));

    // Действие:
    TestObserver<List<Option>> testObserver = vehiclesAndOptionsGateway.getExecutorOptions()
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