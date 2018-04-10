package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.Service;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ServicesListItemsTest {

  private ServicesListItems servicesListItems;

  @Before
  public void setUp() {
    servicesListItems = new ServicesListItems();
    servicesListItems.setServicesListItems(
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
  public void autoPosition() {
    assertEquals(servicesListItems.getCurrentPosition(), 57);
  }

  /**
   * Проверяем правильность минимальной позиции ползунка.
   */
  @Test
  public void minPrice() {
    assertEquals(servicesListItems.getMinPrice(), 10);
  }

  /**
   * Проверяем правильность максимальной позиции ползунка.
   */
  @Test
  public void maxPrice() {
    assertEquals(servicesListItems.getMaxPrice(), 150);
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
    assertEquals(servicesListItems.getServicesListItems(55),
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }

  /**
   * Должен верно отобразить список для новго положения ползунка. При расширении диапазона вновь по-
   * падающие в него элементы должны быть автоматически выбраны.
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
    servicesListItems.setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(3, "n4", 90, false)),
            new ServicesListItem(new Service(4, "n5", 40, false)),
            new ServicesListItem(new Service(5, "n6", 150, false))
        )
    );
    assertEquals(servicesListItems.getCurrentPosition(), 100);
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
    servicesListItems.setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, true)),
            new ServicesListItem(new Service(2, "n3", 130, true)),
            new ServicesListItem(new Service(3, "n4", 90, true)),
            new ServicesListItem(new Service(4, "n5", 40, true)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
    assertEquals(servicesListItems.getCurrentPosition(), 0);
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