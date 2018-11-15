package com.cargopull.executor_driver.interactor.services;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    useCase.autoAssignServices().test().isDisposed();

    // Результат:
    verify(gateway, only()).getServices();
  }

  /**
   * Не должен просить у гейтвея сохранять услуги, если список пуст.
   */
  @Test
  public void doNotTouchGatewayIfNoServices() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(new ArrayList<>()));

    // Действие:
    useCase.autoAssignServices().test().isDisposed();

    // Результат:
    verify(gateway, only()).getServices();
  }

  /**
   * Не должен просить у гейтвея сохранять все услуги как выбранные.
   */
  @Test
  public void askGatewaySetSelectedServices() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 20, true),
            new Service(3, "n4", 40, false),
            new Service(4, "n5", 70, false),
            new Service(5, "n6", 130, true)
        )
    ));

    // Действие:
    useCase.autoAssignServices().test().isDisposed();

    // Результат:
    verify(gateway).getServices();
    verify(gateway).sendSelectedServices(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, true),
            new Service(2, "n3", 20, true),
            new Service(3, "n4", 40, true),
            new Service(4, "n5", 70, true),
            new Service(5, "n6", 130, true)
        )
    );
    verifyNoMoreInteractions(gateway);
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
    useCase.autoAssignServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия доступных услуг.
   */
  @Test
  public void answerNoVehiclesAvailableError() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(new ArrayList<>()));

    // Действие и Результат:
    useCase.autoAssignServices().test().assertError(EmptyListException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkErrorForSetServices() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 20, true),
            new Service(3, "n4", 40, false),
            new Service(4, "n5", 70, false),
            new Service(5, "n6", 130, true)
        )
    ));
    when(gateway.sendSelectedServices(anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие и Результат:
    useCase.autoAssignServices().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом задания услуг.
   */
  @Test
  public void answerSuccessToSetServices() {
    // Дано:
    when(gateway.getServices()).thenReturn(Single.just(
        Arrays.asList(
            new Service(0, "n1", 100, true),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 20, true),
            new Service(3, "n4", 40, false),
            new Service(4, "n5", 70, false),
            new Service(5, "n6", 130, true)
        )
    ));
    when(gateway.sendSelectedServices(anyList())).thenReturn(Completable.complete());

    // Действие и Результат:
    useCase.autoAssignServices().test().assertComplete();
  }
}