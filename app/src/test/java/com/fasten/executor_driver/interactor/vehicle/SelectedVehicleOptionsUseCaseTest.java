package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Vehicle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SelectedVehicleOptionsUseCaseTest {

  private VehicleOptionsUseCase useCase;

  @Mock
  private VehicleOptionsGateway gateway;

  @Mock
  private SelectedVehicleOptionsGateway selectedVehicleOptionsGateway;

  @Before
  public void setUp() {
    useCase = new SelectedVehicleOptionsUseCaseImpl(gateway,
        selectedVehicleOptionsGateway);
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList())).thenReturn(Completable.never());
    when(selectedVehicleOptionsGateway.getVehicleOptions()).thenReturn(Single.never());
    when(selectedVehicleOptionsGateway.getExecutorOptions()).thenReturn(Single.never());
  }

  /* Проверяем работу с гейтвеем опций текущего ТС и исполнителя */

  /**
   * Должен запросить у гейтвея опции текущего ТС.
   */
  @Test
  public void askSelectedVehicleOptionsGatewayForVehicleOptions() {
    // Действие:
    useCase.getVehicleOptions().test();

    // Результат:
    verify(selectedVehicleOptionsGateway, only()).getVehicleOptions();
  }

  /**
   * Должен запросить у гейтвея опции исполнителя.
   */
  @Test
  public void askSelectedVehicleOptionsGatewayForExecutorOptions() {
    // Действие:
    useCase.getDriverOptions().test();

    // Результат:
    verify(selectedVehicleOptionsGateway, only()).getExecutorOptions();
  }

  /**
   * Не должен трогать гейтвей опций ТС и исполнителя.
   */
  @Test
  public void doNotTouchSelectedVehicleOptionsGateway() {
    // Действие:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), new ArrayList<>()
    ).test();

    // Результат:
    verifyZeroInteractions(selectedVehicleOptionsGateway);
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен ответить успехом и только динамическими опциями.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithSelectedVehicleOptionsList() {
    // Дано:
    when(selectedVehicleOptionsGateway.getVehicleOptions()).thenReturn(Single.just(Arrays.asList(
        new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false, false),
        new OptionBoolean(3, "name3", "desc3", true, true)
    )));

    // Действие:
    TestObserver<List<Option>> testObserver = useCase.getVehicleOptions().test();

    // Результат:
    testObserver.assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
            new OptionBoolean(3, "name3", "desc3", true, true)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerNoVehicleOptionsAvailableError() {
    // Дано:
    when(selectedVehicleOptionsGateway.getVehicleOptions()).thenReturn(Single.just(Arrays.asList(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    )));

    // Действие
    TestObserver<List<Option>> testObserver =
        useCase.getVehicleOptions().test();

    // Результат:
    testObserver.assertValues(new ArrayList<>());
  }

  /* Проверяем ответы на запрос опций исполнителя */

  /**
   * Должен ответить успехом и только динамическими опциями.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithExecutorOptionsList() {
    // Дано:
    when(selectedVehicleOptionsGateway.getExecutorOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false),
            new OptionBoolean(3, "name3", "desc3", false, true)
        )
    ));

    // Действие и Результат:
    useCase.getDriverOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerNoExecutorOptionsAvailableError() {
    // Дано:
    when(selectedVehicleOptionsGateway.getExecutorOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false, false),
            new OptionBoolean(3, "name3", "desc3", false, true)
        )
    ));

    // Действие
    TestObserver<List<Option>> testObserver = useCase.getDriverOptions().test();

    // Результат:
    testObserver.assertValues(
        new ArrayList<>()
    );
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен передать гейтвею опции исполнителя и ТС с установленными опциями.
   */
  @Test
  public void askGatewayToSetVehicleAndDriverOptions() {
    // Действие:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )
    ).test();

    // Результат:
    Vehicle vehicle = new Vehicle(0, "", "", "", "", false);
    vehicle.setOptions(
        new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
        new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
        new OptionBoolean(2, "name2", "desc2", true, false)
    );
    verify(gateway, only()).sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false)
    ));
  }

  /* Проверяем ответы на передачу опций ТС для выхода на линию */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    TestObserver<Void> testObserver = useCase
        .setSelectedVehicleAndOptions(new ArrayList<>(), new ArrayList<>()).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSetSelectedVehicleOptionsSuccessful() {
    // Дано:
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = useCase
        .setSelectedVehicleAndOptions(new ArrayList<>(), new ArrayList<>()).test();

    // Результат:
    testObserver.assertComplete();
  }
}