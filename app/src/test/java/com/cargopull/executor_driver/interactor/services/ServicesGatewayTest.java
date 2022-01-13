package com.cargopull.executor_driver.interactor.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.entity.Service;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.ServicesGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class ServicesGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ServicesGateway gateway;

  @Mock
  private ApiService api;
  @Mock
  private Mapper<ApiServiceItem, Service> mapper;

  @Before
  public void setUp() {
    gateway = new ServicesGatewayImpl(api, mapper);
    when(api.getMyServices()).thenReturn(Single.never());
    when(api.getMySelectedServices()).thenReturn(Single.never());
    when(api.setMyServices(anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ список выбранных услуг.
   */
  @Test
  public void askGatewayForSelectedServices() {
    // Action:
    gateway.getServices().test().isDisposed();

    // Effect:
    verify(api).getMySelectedServices();
    verifyNoMoreInteractions(api);
  }

  /**
   * Должен запросить у АПИ список услуг.
   */
  @Test
  public void askGatewayForServices() {
    // Given:
    when(api.getMySelectedServices()).thenReturn(Single.just("5,6"));

    // Action:
    gateway.getServices().test().isDisposed();

    // Effect:
    verify(api).getMySelectedServices();
    verify(api).getMyServices();
    verifyNoMoreInteractions(api);
  }

  /**
   * Должен запросить у АПИ completable на вход с заданными параметрами.
   */
  @Test
  public void setServicesCompletableRequested() {
    // Action:
    gateway.sendSelectedServices(
        Arrays.asList(
            new Service(0, "n", 100, false),
            new Service(3, "n", 104, true),
            new Service(65, "n", 10, false),
            new Service(1, "n", 120, true)
        )
    );

    // Effect:
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
    // Given:
    when(mapper.map(any())).thenReturn(new Service(0, "n1", 100, false));
    when(api.getMySelectedServices()).thenReturn(Single.just("0,2"));
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(0, "n1", 100),
        new ApiServiceItem(1, "n2", 10),
        new ApiServiceItem(2, "n3", 130)
    )));

    // Action:
    gateway.getServices().test().isDisposed();

    // Effect:
    verify(mapper).map(new ApiServiceItem(0, "n1", 100).setSelected(true));
    verify(mapper).map(new ApiServiceItem(1, "n2", 10).setSelected(false));
    verify(mapper).map(new ApiServiceItem(2, "n3", 130).setSelected(true));
    verifyNoMoreInteractions(mapper);
  }

  /**
   * Должен запросить только первое преобразование.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForFirstMappingOnly() throws Exception {
    // Given:
    when(mapper.map(any())).thenThrow(new DataMappingException());
    when(api.getMySelectedServices()).thenReturn(Single.just("0,1"));
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(0, "n1", 100),
        new ApiServiceItem(1, "n2", 10),
        new ApiServiceItem(2, "n3", 130)
    )));

    // Action:
    gateway.getServices().test().isDisposed();

    // Effect:
    verify(mapper, only()).map(new ApiServiceItem(0, "n1", 100).setSelected(true));
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForSelectedServices() {
    // Action:
    when(api.getMySelectedServices()).thenReturn(Single.error(new NoNetworkException()));

    // Effect:
    gateway.getServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForServices() {
    // Action:
    when(api.getMySelectedServices()).thenReturn(Single.just("0,1"));
    when(api.getMyServices()).thenReturn(Single.error(new NoNetworkException()));

    // Effect:
    gateway.getServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой преобразования.
   */
  @Test
  public void answerDataMappingErrorForServices() throws Exception {
    // Action:
    when(mapper.map(any())).thenThrow(new DataMappingException());
    when(api.getMySelectedServices()).thenReturn(Single.just("0,1"));
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Effect:
    gateway.getServices().test().assertError(DataMappingException.class);
  }

  /**
   * Должен вернуть список сервисов.
   */
  @Test
  public void answerWithServices() throws Exception {
    // Given:
    when(mapper.map(any())).thenReturn(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, true),
        new Service(2, "n3", 130, false)
    );
    when(api.getMySelectedServices()).thenReturn(Single.just("5,6"));
    when(api.getMyServices()).thenReturn(Single.just(Arrays.asList(
        new ApiServiceItem(),
        new ApiServiceItem(),
        new ApiServiceItem()
    )));

    // Action:
    TestObserver<List<Service>> testObserver = gateway.getServices().test();

    // Effect:
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
    // Given:
    when(api.setMyServices(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Action и Effect:
    gateway.sendSelectedServices(
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
    // Given:
    when(api.setMyServices(anyString())).thenReturn(Completable.complete());

    // Action и Effect:
    gateway.sendSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, true),
            new Service(2, "n3", 130, false)
        )
    ).test().assertComplete();
  }
}