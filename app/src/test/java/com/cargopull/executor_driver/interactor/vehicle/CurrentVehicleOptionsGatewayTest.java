package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItems;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.CurrentVehicleOptionsGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import io.reactivex.Completable;

@RunWith(MockitoJUnitRunner.class)
public class CurrentVehicleOptionsGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private VehicleOptionsGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    gateway = new CurrentVehicleOptionsGatewayImpl(api);
    when(api.setSelectedVehicleOptions(any())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ занять ТС с указанными опциями.
   */
  @Test
  public void askApiToOccupyVehicleWithOptions() {
    // Given:
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );

    // Action:
    gateway.sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(5, "name0", "desc0", -1, -5, 20),
        new OptionNumeric(6, "name1", "desc1", 2, -18, 5),
        new OptionBoolean(7, "name2", "desc2", true),
        new OptionBoolean(8, "name3", "desc3", false)
    ));

    // Effect:
    verify(api, only()).setSelectedVehicleOptions(new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        ),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    ));
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Given:
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(api.setSelectedVehicleOptions(eq(new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        ),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    ))))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Action и Effect:
    gateway.sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(5, "name0", "desc0", -1, -5, 20),
        new OptionNumeric(6, "name1", "desc1", 2, -18, 5),
        new OptionBoolean(7, "name2", "desc2", true),
        new OptionBoolean(8, "name3", "desc3", false)
    )).test()
        .assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerVehicleOccupiedSuccessfully() {
    // Given:
    Vehicle vehicle = new Vehicle(11, "manufacturer2", "models", "colors", "lic", true);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(api.setSelectedVehicleOptions(eq(new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        ),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    )))).thenReturn(Completable.complete());

    // Action и Effect:
    gateway.sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(5, "name0", "desc0", -1, -5, 20),
        new OptionNumeric(6, "name1", "desc1", 2, -18, 5),
        new OptionBoolean(7, "name2", "desc2", true),
        new OptionBoolean(8, "name3", "desc3", false)
    )).test().assertComplete();
  }
}