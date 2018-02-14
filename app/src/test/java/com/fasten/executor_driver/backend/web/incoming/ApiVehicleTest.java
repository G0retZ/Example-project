package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ApiVehicleTest {

  private ApiVehicle apiVehicle;

  @Before
  public void setUp() throws Exception {
    apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
        false,
        Arrays.asList(
            new ApiVehicleOptionItem(324, "value1",
                new ApiVehicleOptionItemLimits(-5, 123),
                new ApiVehicleOption("option1", false, true)),
            new ApiVehicleOptionItem(31, "value2",
                new ApiVehicleOptionItemLimits(50, 2100),
                new ApiVehicleOption("option2", true, true)),
            new ApiVehicleOptionItem(523, "value3",
                null,
                new ApiVehicleOption("option3", true, false)),
            new ApiVehicleOptionItem(42, "value4",
                null,
                new ApiVehicleOption("option4", false, false))
        )
    );
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicle.getId(), 2190);
    assertEquals(apiVehicle.getMark(), new ApiParam("mark"));
    assertEquals(apiVehicle.getModel(), new ApiParam("model"));
    assertEquals(apiVehicle.getLicensePlate(), "plate");
    assertEquals(apiVehicle.getColor(), new ApiParam("color"));
    assertFalse(apiVehicle.isBusy());
    assertEquals(apiVehicle.getVehicleOptionItems(), Arrays.asList(
        new ApiVehicleOptionItem(324, "value1",
            new ApiVehicleOptionItemLimits(-5, 123),
            new ApiVehicleOption("option1", false, true)),
        new ApiVehicleOptionItem(31, "value2",
            new ApiVehicleOptionItemLimits(50, 2100),
            new ApiVehicleOption("option2", true, true)),
        new ApiVehicleOptionItem(523, "value3",
            null,
            new ApiVehicleOption("option3", true, false)),
        new ApiVehicleOptionItem(42, "value4",
            null,
            new ApiVehicleOption("option4", false, false))
    ));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            210,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            null,
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("marks"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            null,
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("models"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plates",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            null,
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("colors"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            true,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            null
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Collections.emptyList()
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 2100),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            new ApiParam("mark"),
            new ApiParam("model"),
            "plate",
            new ApiParam("color"),
            false,
            Arrays.asList(
                new ApiVehicleOptionItem(324, "value1",
                    new ApiVehicleOptionItemLimits(-5, 123),
                    new ApiVehicleOption("option1", false, true)),
                new ApiVehicleOptionItem(31, "value2",
                    new ApiVehicleOptionItemLimits(50, 210),
                    new ApiVehicleOption("option2", true, true)),
                new ApiVehicleOptionItem(523, "value3",
                    null,
                    new ApiVehicleOption("option3", true, false)),
                new ApiVehicleOptionItem(42, "value4",
                    null,
                    new ApiVehicleOption("option4", false, false))
            )
        )
    );
  }
}