package moontime.droid.util;

import junit.framework.TestCase;

public class YearMonthAdapterPositioningTest extends TestCase {

  private YearMonthAdapterPositioning positioning = new YearMonthAdapterPositioning(2010, 3, Integer.MAX_VALUE / 2,
      Integer.MAX_VALUE / 2);

  public void testYear() throws Exception {
    assertEquals(2010, positioning.getCurrentYear());
    for (int i = 0; i < 20; i++) {
      positioning.setCurrentYearPosition(positioning.getInitialYearPosition() + i);
      assertEquals(2010 + i, positioning.getCurrentYear());
    }
    for (int i = 0; i < 20; i++) {
      positioning.setCurrentYearPosition(positioning.getInitialYearPosition() - i);
      assertEquals(2010 - i, positioning.getCurrentYear());
    }
  }

  public void testMonth() throws Exception {
    assertEquals(3, positioning.getCurrentMonth());

    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 3);
    assertEquals(3 + 3, positioning.getCurrentMonth());

    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 12);
    assertEquals(3, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 25);
    assertEquals(4, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 32);
    assertEquals(11, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 33);
    assertEquals(0, positioning.getCurrentMonth());

    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 12);
    assertEquals(3, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 25);
    assertEquals(2, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 26);
    assertEquals(1, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 27);
    assertEquals(0, positioning.getCurrentMonth());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 28);
    assertEquals(11, positioning.getCurrentMonth());
  }

  public void testYearMonthTogether() throws Exception {
    // change year -> should change month(-position) as well
    positioning.setCurrentYearPosition(positioning.getInitialYearPosition() + 1);
    assertEquals(3, positioning.getCurrentMonth());
    assertEquals(positioning.getInitialMonthPosition() + 12, positioning.getCurrentMonthPosition());
    positioning.setCurrentYearPosition(positioning.getInitialYearPosition() + -1);
    assertEquals(3, positioning.getCurrentMonth());
    assertEquals(positioning.getInitialMonthPosition() - 12, positioning.getCurrentMonthPosition());
    positioning.setCurrentYearPosition(positioning.getInitialYearPosition());
    assertEquals(positioning.getInitialMonthPosition(), positioning.getCurrentMonthPosition());

    // change month -> should change year in case of overflow
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() + 12);
    assertEquals(2011, positioning.getCurrentYear());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 12);
    assertEquals(2009, positioning.getCurrentYear());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition());
    assertEquals(2010, positioning.getCurrentYear());

    // single steps
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition());
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 1);
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 2);
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 3);
    positioning.setCurrentMonthPosition(positioning.getInitialMonthPosition() - 4);
    assertEquals(11, positioning.getCurrentMonth());
    assertEquals(2009, positioning.getCurrentYear());
  }
}
