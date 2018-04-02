package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

// TODO: дописать тесты
@RunWith(MockitoJUnitRunner.class)
public class VehiclesAndOptionsUseCaseTest {

  private VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;

  @Mock
  private VehiclesAndOptionsGateway gateway;

  @Mock
  private Observer<List<Vehicle>> vehiclesObserver;

  @Mock
  private Observer<List<Option>> driverOptionsObserver;

  @Mock
  private Observer<Vehicle> vehicleChoiceObserver;

  @Mock
  private DataReceiver<Vehicle> lastUsedVehicleReceiver;

  private PublishSubject<Vehicle> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(gateway.getExecutorVehicles()).thenReturn(Single.never());
    when(gateway.getExecutorOptions()).thenReturn(Single.never());
    when(lastUsedVehicleReceiver.get()).thenReturn(publishSubject);
    vehiclesAndOptionsUseCase = new VehiclesAndOptionsUseCaseImpl(gateway, vehiclesObserver,
        driverOptionsObserver, vehicleChoiceObserver, lastUsedVehicleReceiver);
  }

  /* Проверяем работу с публикатором последнего использованного ТС */

  /**
   * Должен запросить у публикатора последнее использованное ТС при создании и сразу.
   */
  @Test
  public void askLastUsedVehiclesDataSharerForLastUsedVehicleInitially() {
    // Результат:
    verify(lastUsedVehicleReceiver, only()).get();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея выход на линию.
   */
  @Test
  public void askGatewayForAuth() {
    // Дано:
    when(gateway.getExecutorOptions()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new OptionBoolean(12, "name", "desc", false, true),
            new OptionBoolean(13, "names", "descriptions", true, false),
            new OptionNumeric(14, "nam", "script", true, 5, 0, 10),
            new OptionNumeric(15, "man fact", "sky", false, 0, 3, 6)
        ))
    ));

    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(gateway).getExecutorVehicles();
    verify(gateway).getExecutorOptions();
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем работу с публикатором списка ТС */

  /**
   * Не должен трогать публикатор.
   */
  @Test
  public void doNotTouchVehiclesSharer() {
    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verifyZeroInteractions(vehiclesObserver);
  }

  /**
   * Должен опубликовать список полученных ТС.
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehicles() {
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
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehiclesObserver, only())
        .onNext(
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
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesWith1FreeVehicle() {
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
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehiclesObserver, only())
        .onNext(
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
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesWithoutFree() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehiclesObserver, only())
        .onNext(
            new ArrayList<>(Arrays.asList(
                new Vehicle(12, "manufacturer", "model", "color", "license", true),
                new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
            ))
        );
  }

  /**
   * Должен опубликовать список полученных ТС с из одной свободной тачки.
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesOfTheOnlyFreeVehicle() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehiclesObserver, only())
        .onNext(
            new ArrayList<>(Collections.singletonList(
                new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
            ))
        );
  }

  /**
   * Должен опубликовать пустой список полученных ТС.
   */
  @Test
  public void askVehiclesSharerToShareLoadedVehiclesEmpty() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));

    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehiclesObserver, only()).onNext(new ArrayList<>());
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   */
  @Test
  public void doNotTouchVehicleChoiceSharer() {
    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceObserver);
  }

  /**
   * Должен опубликовать первую свободную ТС из списка.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicle() {
    // Действие:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первую свободную ТС из списка, если последняя использовавшаяся возвращает
   * ошибку.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsError() {
    // Дано:
    when(lastUsedVehicleReceiver.get()).thenReturn(publishSubject, PublishSubject.never());
    vehiclesAndOptionsUseCase = new VehiclesAndOptionsUseCaseImpl(gateway, vehiclesObserver,
        driverOptionsObserver, vehicleChoiceObserver, lastUsedVehicleReceiver);

    // Действие:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    publishSubject.onError(new IllegalArgumentException());
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первую свободную ТС из списка, если последняя использовавшаяся больше не в
   * списке.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsOutOfRange() {
    // Действие:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    publishSubject.onNext(
        new Vehicle(105, "m", "m", "c", "l", false)
    );
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать первую свободную ТС из списка, если последняя использовавшаяся занята.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheFirstFreeVehicleIfLastUsedIsBusy() {
    // Действие:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    publishSubject.onNext(
        new Vehicle(15, "m", "m", "c", "l", false)
    );
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(12, "manufacturer", "model", "color", "license", false));
  }

  /**
   * Должен опубликовать последнее использовавшееся ТС, если оно свободно и есть в списке.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheLastUsedVehicle() {
    // Действие:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    publishSubject.onNext(
        new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
    );
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /**
   * Должен опубликовать единственную свободную ТС из списка.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheOnlyFreeVehicle() {
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
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /**
   * Должен опубликовать единственную ТС, если она свободна.
   */
  @Test
  public void askVehicleChoiceSharerToShareTheVehicleIfItsFree() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));

    // Действие:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false));
  }

  /* Проверяем ответы на запрос загрузки списка ТС */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new NoNetworkException()));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой блокировки.
   */
  @Test
  public void answerDriverBlockedError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.error(new DriverBlockedException()));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test()
        .assertError(DriverBlockedException.class);
  }

  /**
   * Должен ответить ошибкой недостаточности средств.
   */
  @Test
  public void answerInsufficientCreditsError() {
    // Дано:
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new InsufficientCreditsException()));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test()
        .assertError(InsufficientCreditsException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия доступных ТС.
   */
  @Test
  public void answerNoVehiclesAvailableError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(new ArrayList<>()));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test()
        .assertError(NoVehiclesAvailableException.class);
  }

  /**
   * Должен ответить ошибкой отсуствствия свободных ТС.
   */
  @Test
  public void answerNoFreeVehiclesError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test()
        .assertError(NoFreeVehiclesException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfOnlyOneFreeVehicleAvailableAndFree() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    when(gateway.getExecutorOptions()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new OptionBoolean(12, "name", "desc", false, true),
            new OptionBoolean(13, "names", "descriptions", true, false),
            new OptionNumeric(14, "nam", "script", true, 5, 0, 10),
            new OptionNumeric(15, "man fact", "sky", false, 0, 3, 6)
        ))
    ));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test().assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccessIfOnlyOneVehicleAvailableFree() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false)
        ))
    ));
    when(gateway.getExecutorOptions()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new OptionBoolean(12, "name", "desc", false, true),
            new OptionBoolean(13, "names", "descriptions", true, false),
            new OptionNumeric(14, "nam", "script", true, 5, 0, 10),
            new OptionNumeric(15, "man fact", "sky", false, 0, 3, 6)
        ))
    ));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test().assertComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerLoadSuccessful() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensing", false),
            new Vehicle(15, "man fact", "modelers", "colo", "licensee", true)
        ))
    ));
    when(gateway.getExecutorOptions()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new OptionBoolean(12, "name", "desc", false, true),
            new OptionBoolean(13, "names", "descriptions", true, false),
            new OptionNumeric(14, "nam", "script", true, 5, 0, 10),
            new OptionNumeric(15, "man fact", "sky", false, 0, 3, 6)
        ))
    ));

    // Действие и Результат:
    vehiclesAndOptionsUseCase.loadVehiclesAndOptions().test().assertComplete();
  }
}