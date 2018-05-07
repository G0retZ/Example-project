package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class ApiSelectedOptionsForOnlineTest {

  private ApiSelectedOptionsForOnline apiSelectedOptionsForOnline;

  @Before
  public void setUp() {
    apiSelectedOptionsForOnline = new ApiSelectedOptionsForOnline(
        Arrays.asList(
            new ApiOptionItem(765, "option1", "description1", false, true, "value1", null, null),
            new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
            new ApiOptionItem(876, "option3", "description3", false, false, "value3", null, null)
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
    assertEquals(apiSelectedOptionsForOnline.getVehicleOptions(), Arrays.asList(
        new ApiOptionItem(765, "option1", "description1", false, true, "value1", null, null),
        new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
        new ApiOptionItem(876, "option3", "description3", false, false, "value3", null, null)
    ));
    assertEquals(apiSelectedOptionsForOnline.getDriverOptions(), Arrays.asList(
        new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
        new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
        new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
        new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
    ));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
                new ApiOptionItem(876, "option3", "description3", false, false, "value3", null,
                    null)
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            new ArrayList<>(),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20)
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
                new ApiOptionItem(886, "option3", "description3", false, false, "value3", null,
                    null)
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(31, "opt2", "desc2", true, true, "val2", 5, 20),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
                new ApiOptionItem(876, "option3", "description3", false, false, "value3", null,
                    null)
            ),
            new ArrayList<>()
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
                new ApiOptionItem(876, "option3", "description3", false, false, "value3", null,
                    null)
            ),
            Arrays.asList(
                new ApiOptionItem(324, "opt1", "desc1", true, false, "val1", -5, 3),
                new ApiOptionItem(523, "opt3", "desc3", false, true, "val3", null, null),
                new ApiOptionItem(42, "opt4", "desc4", false, false, "val4", null, null)
            )
        )
    );
    assertNotEquals(apiSelectedOptionsForOnline,
        new ApiSelectedOptionsForOnline(
            Arrays.asList(
                new ApiOptionItem(765, "option1", "description1", false, true, "value1", null,
                    null),
                new ApiOptionItem(456, "option2", "description2", true, true, "value2", 5, 20),
                new ApiOptionItem(876, "option3", "description3", false, false, "value3", null,
                    null)
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