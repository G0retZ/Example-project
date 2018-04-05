package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasten.executor_driver.entity.OptionBoolean;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class VehicleOptionsListItemsTest {

  private VehicleOptionsListItems vehicleOptionsListItems;

  @Before
  public void setUp() {
    vehicleOptionsListItems = new VehicleOptionsListItems(
        Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(11, "name1", "description1", false, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(12, "name2", "description2", false, true)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(13, "name3", "description3", true, false)
            )
        ),
        Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(14, "name4", "description4", true, true)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(15, "name5", "description5", false, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(16, "name6", "description6", false, true)
            )
        )
    );
  }

  @Test
  public void testGetters() {
    assertEquals(vehicleOptionsListItems.size(), 6);
    assertEquals(vehicleOptionsListItems.getVehicleOptions(), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(11, "name1", "description1", false, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(12, "name2", "description2", false, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(13, "name3", "description3", true, false)
        )
    ));
    assertEquals(vehicleOptionsListItems.getDriverOptions(), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(14, "name4", "description4", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(15, "name5", "description5", false, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(16, "name6", "description6", false, true)
        )
    ));
    assertEquals(vehicleOptionsListItems.get(0), new VehicleOptionsListItem<>(
        new OptionBoolean(11, "name1", "description1", false, false)
    ));
    assertEquals(vehicleOptionsListItems.get(1), new VehicleOptionsListItem<>(
        new OptionBoolean(12, "name2", "description2", false, true)
    ));
    assertEquals(vehicleOptionsListItems.get(2), new VehicleOptionsListItem<>(
        new OptionBoolean(13, "name3", "description3", true, false)
    ));
    assertEquals(vehicleOptionsListItems.get(3), new VehicleOptionsListItem<>(
        new OptionBoolean(14, "name4", "description4", true, true)
    ));
    assertEquals(vehicleOptionsListItems.get(4), new VehicleOptionsListItem<>(
        new OptionBoolean(15, "name5", "description5", false, false)
    ));
    assertEquals(vehicleOptionsListItems.get(5), new VehicleOptionsListItem<>(
        new OptionBoolean(16, "name6", "description6", false, true)
    ));
  }

  @Test
  public void testEquals() {
    assertEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(11, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(12, "name2", "description2", false, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(15, "name5", "description5", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(16, "name6", "description6", false, true)
                )
            )
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            new ArrayList<>(),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(15, "name5", "description5", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(16, "name6", "description6", false, true)
                )
            )
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(11, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(15, "name5", "description5", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(16, "name6", "description6", false, true)
                )
            )
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(10, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(12, "name2", "description2", false, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(15, "name5", "description5", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(16, "name6", "description6", false, true)
                )
            )
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(11, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(12, "name2", "description2", false, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            new ArrayList<>()
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(11, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(12, "name2", "description2", false, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(16, "name6", "description6", false, true)
                )
            )
        )
    );
    assertNotEquals(vehicleOptionsListItems,
        new VehicleOptionsListItems(
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(11, "name1", "description1", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(12, "name2", "description2", false, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(13, "name3", "description3", true, false)
                )
            ),
            Arrays.asList(
                new VehicleOptionsListItem<>(
                    new OptionBoolean(14, "name4", "description4", true, true)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(15, "name5", "description5", false, false)
                ),
                new VehicleOptionsListItem<>(
                    new OptionBoolean(17, "name6", "description6", false, true)
                )
            )
        )
    );
  }
}