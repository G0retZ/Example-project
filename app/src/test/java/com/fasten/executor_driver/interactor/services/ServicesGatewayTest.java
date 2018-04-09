package com.fasten.executor_driver.interactor.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.incoming.ApiServiceItem;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.ServicesGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesGatewayTest {

  private ServicesGateway servicesGateway;

  @Mock
  private ApiService api;
  @Mock
  private Mapper<ApiServiceItem, Service> mapper;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    servicesGateway = new ServicesGatewayImpl(api, mapper);
    when(api.getMyServices()).thenReturn(Single.never());
    when(api.setMyServices(anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ список услуг.
   */
  @Test
  public void askGatewayForServices() {
    // Действие:
    servicesGateway.getServices();

    // Результат:
    verify(api, only()).getMyServices();
  }

  /**
   * Должен запросить у АПИ completable на вход с заданными параметрами.
   */
  @Test
  public void setServicesCompletableRequested() {
    // Действие:
    servicesGateway.sendSelectedServices(
        Arrays.asList(
            new Service(0, "n", 100, false),
            new Service(3, "n", 104, true),
            new Service(65, "n", 10, false),
            new Service(1, "n", 120, true)
        )
    );

    // Результат:
    verify(api, only()).setMyServices("0,3,65,1");
  }

  /* Проверяем работу с преобразователем данных услуги */

  /**
   * Должен запросить все преобразования.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForMapping() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(new Service(0, "n1", 100, false));
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Действие:
    servicesGateway.getServices().test();

    // Результат:
    verify(mapper, times(3)).map(new ApiServiceItem());
    verifyNoMoreInteractions(mapper);
  }

  /**
   * Должен запросить только первое преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForFirstMappingOnly() throws Exception {
    // Дано:
    when(mapper.map(any())).thenThrow(new DataMappingException());
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Действие:
    servicesGateway.getServices().test();

    // Результат:
    verify(mapper, only()).map(new ApiServiceItem());
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForServices() {
    // Действие:
    when(api.getMyServices()).thenReturn(Single.error(new NoNetworkException()));

    // Результат:
    servicesGateway.getServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой преобразования.
   */
  @Test
  public void answerDataMappingErrorForServices() throws Exception {
    // Действие:
    when(mapper.map(any())).thenThrow(new DataMappingException());
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Результат:
    servicesGateway.getServices().test().assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список сервисов.
   */
  @Test
  public void answerWithServices() throws Exception {
    // Дано:
    when(mapper.map(any())).thenReturn(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, true),
        new Service(2, "n3", 130, false)
    );
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Действие:
    TestObserver<List<Service>> testObserver = servicesGateway.getServices().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertValue(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, true),
        new Service(2, "n3", 130, false)
    ));
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForSetServices() {
    // Дано:
    when(api.setMyServices(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Действие и Результат:
    servicesGateway.sendSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, true),
            new Service(2, "n3", 130, false)
        )
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerServicesSetSuccessful() {
    // Дано:
    when(api.setMyServices(anyString())).thenReturn(Completable.complete());

    // Действие и Результат:
    servicesGateway.sendSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, true),
            new Service(2, "n3", 130, false)
        )
    ).test().assertComplete();
  }
}