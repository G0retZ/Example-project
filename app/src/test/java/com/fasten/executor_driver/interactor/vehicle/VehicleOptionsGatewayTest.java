package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.outgoing.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import com.fasten.executor_driver.gateway.VehicleOptionsGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsGatewayTest {

  private VehicleOptionsGateway vehicleOptionsGateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    vehicleOptionsGateway = new VehicleOptionsGatewayImpl(api);
    when(api.selectCarWithOptions(anyLong(), anyList())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на запрос входящего СМС с кодом
   *
   * @throws Exception error
   */
  @Test
  public void smsMeCompletableRequested() throws Exception {
    // Дано:
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", true, true)
    );

    // Действие:
    vehicleOptionsGateway.sendVehicleOptions(vehicle);

    // Результат:
    verify(api, only()).selectCarWithOptions(11, Arrays.asList(
        new ApiVehicleOptionItem(0, "10"),
        new ApiVehicleOptionItem(1, "-5"),
        new ApiVehicleOptionItem(2, "false"),
        new ApiVehicleOptionItem(3, "true")
    ));
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
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", true, true)
    );
    when(api.selectCarWithOptions(anyLong(), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Действие и Результат:
    vehicleOptionsGateway.sendVehicleOptions(vehicle).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error
   */
  @Test
  public void answerSmsSuccessful() throws Exception {
    // Дано:
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", true, true)
    );
    when(api.selectCarWithOptions(anyLong(), anyList())).thenReturn(Completable.complete());

    // Действие и Результат:
    vehicleOptionsGateway.sendVehicleOptions(vehicle).test().assertComplete();
  }
}