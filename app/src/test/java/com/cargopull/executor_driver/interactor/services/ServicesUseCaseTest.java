package com.cargopull.executor_driver.interactor.services;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ServicesUseCase useCase;

  @Mock
  private ServicesGateway gateway;

  @Before
  public void setUp() {
    when(gateway.getServices()).thenReturn(Single.never());
    when(gateway.sendSelectedServices(anyList())).thenReturn(Completable.never());
    useCase = new ServicesUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея загрузить список услуг.
   */
  @Test
  public void askGatewayForServices() {
    // Действие:
    useCase.loadServices().test().isDisposed();

    // Результат:
    verify(gateway, only()).getServices();
  }

  /**
   * Не должен просить у гейтвея сохранять услуги, если список пуст.
   */
  @Test
  public void doNotTouchGatewayIfNoServices() {
    // Действие:
    useCase.setSelectedServices(new ArrayList<>()).test().isDisposed();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен просить у гейтвея сохранять услуги, если нет выбранных услуг.
   */
  @Test
  public void doNotTouchGatewayIfNoSelectedServices() {
    // Действие:
    useCase.setSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    ).test().isDisposed();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея сохранение выбранных услуг.
   */
  @Test
  public void askGatewaySetSelectedServices() {
    // Действие:
    useCase.setSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, true)
        )
    ).test().isDisposed();

    // Результат:
    verify(gateway, only()).sendSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(2, "n3", 130, true)
        )
    );
  }

  /* Проверяем ответы на запрос загрузки списка услуг */

  /**
   * Должен ответить ошибкой сети на запрос списка услуг.
   */
  @Test
  public void answerNoNetworkErrorForServices() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.error(new NoNetworkException()));

    // Действие и Результат:
    useCase.loadServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия доступных услуг.
   */
  @Test
  public void answerNoVehiclesAvailableError() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(new ArrayList<>()));

    // Действие и Результат:
    useCase.loadServices().test().assertError(EmptyListException.class);
  }

  /**
   * Должен ответить списком услуг.
   */
  @Test
  public void answerWithServicesList() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, true)
        )
    ));

    // Действие и Результат:
    useCase.loadServices().test().assertComplete();
    useCase.loadServices().test().assertValue(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, true)
        )
    );
  }

  /**
   * Должен ответить ошибкой отсуствствия выбранных услуг.
   */
  @Test
  public void answerNoVehiclesAvailableErrorForEmptyList() {
    // Действие и Результат:
    useCase.setSelectedServices(new ArrayList<>()).test()
        .assertError(EmptyListException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия выбранных услуг.
   */
  @Test
  public void answerNoVehiclesAvailableErrorForNoSelectedServices() {
    // Действие и Результат:
    useCase.setSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    ).test().assertError(EmptyListException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForSetServices() {
    // Дано:
    when(gateway.sendSelectedServices(anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие и Результат:
    useCase.setSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, true)
        )
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом задания услуг.
   */
  @Test
  public void answerSuccessToSetServices() {
    // Дано:
    when(gateway.sendSelectedServices(anyList())).thenReturn(Completable.complete());

    // Действие и Результат:
    useCase.setSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, true)
        )
    ).test().assertComplete();
  }
}