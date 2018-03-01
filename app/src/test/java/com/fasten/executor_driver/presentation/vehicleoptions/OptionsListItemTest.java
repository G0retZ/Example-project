package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import org.junit.Before;
import org.junit.Test;

// TODO: написать недостающие тесты.
public class OptionsListItemTest {

  private OptionsListItem optionsListItem;

  @Before
  public void setUp() throws Exception {
    optionsListItem = new OptionsListItem<>(
        new OptionBoolean(11, "name", "description", false, false)
    );
  }

  @Test
  public void testGetters() throws Exception {
    assertEquals(optionsListItem.getName(), "name");
    assertEquals(optionsListItem.getDescription(), "description");
    assertEquals(optionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(optionsListItem.getValue(), false);
    assertEquals(optionsListItem.getMinValue(), false);
    assertEquals(optionsListItem.getMaxValue(), true);
    optionsListItem = new OptionsListItem<>(
        new OptionNumeric(11, "emacs", "desc", true, 5, 0, 10)
    );
    assertEquals(optionsListItem.getName(), "emacs");
    assertEquals(optionsListItem.getDescription(), "desc");
    assertEquals(optionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(optionsListItem.getValue(), 5);
    assertEquals(optionsListItem.getMinValue(), 0);
    assertEquals(optionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testSetters() throws Exception {
    optionsListItem.setValue(true);
    assertEquals(optionsListItem.getName(), "name");
    assertEquals(optionsListItem.getDescription(), "description");
    assertEquals(optionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_boolean);
    assertEquals(optionsListItem.getValue(), true);
    assertEquals(optionsListItem.getMinValue(), false);
    assertEquals(optionsListItem.getMaxValue(), true);
    optionsListItem = new OptionsListItem<>(
        new OptionNumeric(11, "emacs", "desc", true, 5, 0, 10)
    );
    optionsListItem.setValue(7);
    assertEquals(optionsListItem.getName(), "emacs");
    assertEquals(optionsListItem.getDescription(), "desc");
    assertEquals(optionsListItem.getLayoutType(),
        R.layout.fragment_vehicle_options_list_item_numeric);
    assertEquals(optionsListItem.getValue(), 7);
    assertEquals(optionsListItem.getMinValue(), 0);
    assertEquals(optionsListItem.getMaxValue(), 10);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(11, "name", "description", false, false)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(12, "name", "description", false, false)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(11, "names", "description", false, false)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(11, "name", "descriptions", false, false)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(11, "name", "description", true, false)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionBoolean(11, "name", "description", false, true)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
        )
    );
    optionsListItem = new OptionsListItem<>(
        new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
    );
    assertEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(12, "emacs", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "name", "description", true, 5, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "descriptions", true, 5, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", false, 5, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 0, 0, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 5, 10)
        )
    );
    assertNotEquals(optionsListItem,
        new OptionsListItem<>(
            new OptionNumeric(11, "emacs", "description", true, 5, 0, 5)
        )
    );
  }
}