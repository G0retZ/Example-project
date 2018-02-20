package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.OnlyOneVehicleAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehiclesUseCaseTest {

  private VehiclesUseCase vehiclesUseCase;

  @Mock
  private VehiclesGateway gateway;

  @Mock
  private DataSharer<List<Vehicle>> vehiclesSharer;

  @Mock
  private DataSharer<Vehicle> vehicleChoiceSharer;

  @Before
  public void setUp() throws Exception {
    when(gateway.getExecutorVehicles()).thenReturn(Single.never());
    vehiclesUseCase = new VehiclesUseCaseImpl(gateway, vehiclesSharer, vehicleChoiceSharer);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея выход на линию
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForAuth() throws Exception {
    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(gateway, only()).getExecutorVehicles();
  }

  /* Проверяем работу с публикатором списка ТС */

  /**
   * Не должен трогать публикатор.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchVehiclesSharer() throws Exception {
    // Действие:
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verifyZeroInteractions(vehiclesSharer);
  }

  /**
   * Должен опубликовать список полученных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehicles() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehiclesSharer, only())
        .share(
            new ArrayList<>(Arrays.asList(
                new Vehicle(12, "manufacturer", "model", "color", "license", false),
                new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
                new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
                new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
            ))
        );
  }

  /**
   * Должен опубликовать список полученных ТС с одной свободной.
   *
   * @throws Exception error
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesWith1FreeVehicle() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehiclesSharer, only())
        .share(
            new ArrayList<>(Arrays.asList(
                new Vehicle(12, "manufacturer", "model", "color", "license", true),
                new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
                new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
                new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
            ))
        );
  }

  /**
   * Должен опубликовать список полученных ТС без свободных.
   *
   * @throws Exception error
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesWithoutFree() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehiclesSharer, only())
        .share(
            new ArrayList<>(Arrays.asList(
                new Vehicle(12, "manufacturer", "model", "color", "license", true),
                new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
            ))
        );
  }

  /**
   * Должен опубликовать список полученных ТС с из одной свободной тачки.
   *
   * @throws Exception error
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesOfTheOnlyFreeVehicle() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehiclesSharer, only())
        .share(
            new ArrayList<>(Collections.singletonList(
                new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
            ))
        );
  }

  /**
   * Должен опубликовать пустой список полученных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesEmpty() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehiclesSharer, only()).share(new ArrayList<>());
  }

   /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchVehicleChoiceSharer() throws Exception {
    // Действие:
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    vehiclesUseCase.loadVehicles().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceSharer);
  }

  /**
   * Должен опубликовать единственную свободную ТС из списка.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleChoiceSharerToShareTheOnlyFreeVehicle() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehicleChoiceSharer, only())
        .share(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /**
   * Должен опубликовать единственную ТС, если она свободна.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleChoiceSharerToShareTheVehicleIfItsFree() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Действие:
    vehiclesUseCase.loadVehicles().test();

    // Результат:
    verify(vehicleChoiceSharer, only())
        .share(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /* Проверяем ответы на запрос загрузки списка ТС */

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой блокировки.
   *
   * @throws Exception error
   */
  @Test
  public void answerDriverBlockedError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(DriverBlockedException.class);
  }

  /**
   * Должен ответить ошибкой недостаточности средств.
   *
   * @throws Exception error
   */
  @Test
  public void answerInsufficientCreditsError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(InsufficientCreditsException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия доступных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoVehiclesAvailableError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(NoVehiclesAvailableException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия свободных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoFreeVehiclesError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(NoFreeVehiclesException.class);
  }

  /**
   * Должен ответить ошибкой единственной свободной ТС.
   *
   * @throws Exception error
   */
  @Test
  public void answerOnlyOneFreeVehicleAvailableError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(OnlyOneVehicleAvailableException.class);
  }

  /**
   * Должен ответить ошибкой единственной и свободной ТС.
   *
   * @throws Exception error
   */
  @Test
  public void answerOnlyOneVehicleAvailableFreeError() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertError(OnlyOneVehicleAvailableException.class);
  }

  /**
   * Должен ответить успехом
   *
   * @throws Exception error
   */
  @Test
  public void answerLoadSuccessful() throws Exception {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehiclesUseCase.loadVehicles().test().assertComplete();
  }
}