package com.cargopull.executor_driver.backend.web.outgoing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class ApiOptionItemsTest {

  private ApiOptionItems apiOptionItems;

  @Before
  public void setUp() {
    apiOptionItems = new ApiOptionItems(
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
    );
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOptionItems.getVehicleOptions(),
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        )
    );
    assertEquals(apiOptionItems.getDriverOptions(),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    );
  }

  @Test
  public void testEquals() {
    assertEquals(apiOptionItems, new ApiOptionItems(
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
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        new ArrayList<>(),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    ));
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(3, "true")
        ),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(6, "2"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    ));
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "11"),
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
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        ), new ArrayList<>()
    ));
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-5"),
            new ApiOptionItem(2, "false"),
            new ApiOptionItem(3, "true")
        ),
        Arrays.asList(
            new ApiOptionItem(5, "-1"),
            new ApiOptionItem(7, "true"),
            new ApiOptionItem(8, "false")
        )
    ));
    assertNotEquals(apiOptionItems, new ApiOptionItems(
        Arrays.asList(
            new ApiOptionItem(0, "10"),
            new ApiOptionItem(1, "-53"),
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
}