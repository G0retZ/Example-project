package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.Service;
import java.util.Arrays;
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

  @Test
  public void initPosition() {
    assertEquals(position, 57);
  }

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

  @Test
  public void listNarrowing() {
    assertEquals(servicesListItems.getServicesListItems(80),
        Arrays.asList(
            new ServicesListItem(new Service(2, "n3", 130, false)),
            new ServicesListItem(new Service(5, "n6", 150, true))
        )
    );
  }
}