package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataUpdateUseCase;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import io.reactivex.Single;

@RunWith(MockitoJUnitRunner.class)
public class VehiclesAndOptionsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private VehiclesAndOptionsUseCase useCase;

  @Mock
  private VehiclesAndOptionsGateway gateway;
  @Mock
  private DataUpdateUseCase<Vehicle> vehicleChoiceObserver;
  @Mock
  private LastUsedVehicleGateway lastUsedVehicleGateway;

  @Before
  public void setUp() {
    when(gateway.getExecutorVehicles()).thenReturn(Single.never());
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.never());
    useCase = new VehiclesAndOptionsUseCaseImpl(gateway, vehicleChoiceObserver,
        lastUsedVehicleGateway);
  }

  /* Проверяем работу с публикатором последнего использованного ТС */

  /**
   * Должен запросить у публикатора последнее использованное ТС при создании и сразу.
   */
  @Test
  public void askLastUsedVehiclesDataSharerForLastUsedVehicleInitially() {
    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(lastUsedVehicleGateway, only()).getLastUsedVehicleId();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея попытку загрузить список ТС.
   */
  @Test
  public void askGatewayForVehicles() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(10L));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(gateway).getExecutorVehicles();
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   */
  @Test
  public void doNotTouchVehicleChoiceSharer() {
    // Action:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(10L));
    useCase.loadVehiclesAndOptions().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));
    useCase.loadVehiclesAndOptions().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));
    useCase.loadVehiclesAndOptions().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    useCase.loadVehiclesAndOptions().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verifyNoInteractions(vehicleChoiceObserver);
  }

  /**
   * Должен опубликовать первое свободное ТС из списка.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicle() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первое свободное ТС из списка, если последняя использовавшаяся возвращает
   * ошибку.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsError() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId())
        .thenReturn(Single.error(new IllegalArgumentException()));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первое свободное ТС из списка, если последняя использовавшаяся больше не в
   * списке.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsOutOfRange() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(105L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первое свободное ТС из списка, если последняя использовавшаяся занята.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsBusy() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(15L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать последнее использовавшееся ТС, если оно свободно и есть в списке.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheLastUsedVehicle() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(14L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /**
   * Должен опубликовать единственную свободную ТС из списка.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheOnlyFreeVehicle() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /**
   * Должен опубликовать единственную ТС, если она свободна.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheVehicleIfItsFree() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Action:
    useCase.loadVehiclesAndOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /* Проверяем ответы на запрос загрузки списка ТС */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой блокировки.
   */
  @Test
  public void answerDriverBlockedError() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test()
        .assertError(DriverBlockedException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия доступных ТС.
   */
  @Test
  public void answerNoVehiclesAvailableError() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test()
        .assertError(EmptyListException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия свободных ТС.
   */
  @Test
  public void answerNoFreeVehiclesError() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test()
        .assertError(NoSuchElementException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfOnlyOneFreeVehicleAvailable() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test().assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfOnlyOneVehicleAvailableAndFree() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test().assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerLoadSuccessful() {
    // Given:
    when(lastUsedVehicleGateway.getLastUsedVehicleId()).thenReturn(Single.just(-1L));
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Action и Effect:
    useCase.loadVehiclesAndOptions().test().assertComplete();
  }
}