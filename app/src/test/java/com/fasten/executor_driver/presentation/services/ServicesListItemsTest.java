package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.Service;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ServicesListItemsTest {

  private ServicesListItems servicesListItems;
  private int position;

  @Before
  public void setUp() {
    servicesListItems = new ServicesListItems();
    position = servicesListItems.setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(4, "n5", 40, false)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Проверяем правильность рассчитанной позиции ползунка.
   */
  @Test
  public void initPosition() {
    assertEquals(position, 57);
  }

  /**
   * Должен верно отобразить список для текущего положения ползунка
   */
  @Test
  public void listCurrent() {
    assertEquals(servicesListItems.getServicesListItems(),
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Должен верно отобразить список для такого же положения ползунка
   */
  @Test
  public void listSame() {
    assertEquals(servicesListItems.getServicesListItems(57),
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Должен верно отобразить список для такого новго положения ползунка. При расширении диапазона
   * вновь попадающие в него элементы должны быть автоматически выбраны.
   */
  @Test
  public void listExpanding() {
    assertEquals(servicesListItems.getServicesListItems(20),
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(4, "n5", 40, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Должен верно отобразить список для такого новго положения ползунка. При сужении диапазона
   * выбор оставшихся элементов меняться не должен.
   */
  @Test
  public void listNarrowing() {
    assertEquals(servicesListItems.getServicesListItems(80),
        Arrays.asList(
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Если ничего не выбрано, то список должен содержать 1 выбранный элемент с максимальной ценой и
   * положением ползунка на 100.
   */
  @Test
  public void nothingSelected() {
    position = servicesListItems.setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, false)),
            new ServicesListItem(new Service(4, "n5", 40, false)),
            new ServicesListItem(new Service(5, "n6", 150, false))
        )
    );
    assertEquals(position, 100);
    assertEquals(servicesListItems.getServicesListItems(),
        Collections.singletonList(
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Если все выбрано, то положение ползунка должно быть на 0.
   */
  @Test
  public void allSelected() {
    position = servicesListItems.setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, true)),
            new ServicesListItem(new Service(2, "n3", 130, true)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(4, "n5", 40, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
    assertEquals(position, 0);
    assertEquals(servicesListItems.getServicesListItems(),
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, true)),
            new ServicesListItem(new Service(2, "n3", 130, true)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(4, "n5", 40, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }
}