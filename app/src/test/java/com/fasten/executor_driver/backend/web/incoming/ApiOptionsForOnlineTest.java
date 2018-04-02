package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class ApiOptionsForOnlineTest {

  private ApiOptionsForOnline apiOptionsForOnline;

  @Before
  public void setUp() {
    apiOptionsForOnline = new ApiOptionsForOnline(
        Arrays.asList(
            new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
            new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
            new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
        ),
        Arrays.asList(
            new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
            new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
            new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
            new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
        )
    );
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOptionsForOnline.getCars(), Arrays.asList(
        new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
        new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
        new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
    ));
    assertEquals(apiOptionsForOnline.getDriverOptions(), Arrays.asList(
        new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
        new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
        new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
        new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
    ));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
                new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            new ArrayList<>(),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>())
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
                new ApiVehicle(3, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
                new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
            ),
            new ArrayList<>()
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
                new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiOptionsForOnline,
        new ApiOptionsForOnline(
            Arrays.asList(
                new ApiVehicle(0, "mark1", "model1", "lic1", "col1", false, new ArrayList<>()),
                new ApiVehicle(1, "mark2", "model2", "lic2", "col2", false, new ArrayList<>()),
                new ApiVehicle(2, "mark3", "model3", "lic3", "col3", false, new ArrayList<>())
            ),
            Arrays.asList(
                new ApiOptionItem(32, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
  }
}