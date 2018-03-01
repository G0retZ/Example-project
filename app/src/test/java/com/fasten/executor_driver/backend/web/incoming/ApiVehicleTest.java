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
        "mark",
        "model",
        "plate",
        "color",
        false,
        Arrays.asList(
            new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
            new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
            new ApiOptionItem(523, "option3", "description3", false, true, "value3", null, null),
            new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
        )
    );
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiVehicle.getId(), 2190);
    assertEquals(apiVehicle.getMarkName(), "mark");
    assertEquals(apiVehicle.getModelName(), "model");
    assertEquals(apiVehicle.getLicensePlate(), "plate");
    assertEquals(apiVehicle.getColor(), "color");
    assertFalse(apiVehicle.isBusy());
    assertEquals(apiVehicle.getVehicleOptionItems(), Arrays.asList(
        new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
        new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
        new ApiOptionItem(523, "option3", "description3", false, true, "value3", null, null),
        new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
    ));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            210,
            "mark",
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            null,
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "marks",
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            null,
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "models",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            null,
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plates",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            null,
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "colors",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            true,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            false,
            null
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            false,
            Collections.emptyList()
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 2100),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
    assertNotEquals(apiVehicle,
        new ApiVehicle(
            2190,
            "mark",
            "model",
            "plate",
            "color",
            false,
            Arrays.asList(
                new ApiOptionItem(324, "option1", "description1", true, false, "value1", -5, 123),
                new ApiOptionItem(31, "option2", "description2", true, true, "value2", 50, 210),
                new ApiOptionItem(523, "option3", "description3", false, true, "value3", null,
                    null),
                new ApiOptionItem(42, "option4", "description4", false, false, "value4", null, null)
            )
        )
    );
  }
}