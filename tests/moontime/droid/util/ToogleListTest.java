package moontime.droid.util;

import junit.framework.TestCase;

public class ToogleListTest extends TestCase {

  public void testToogle() throws Exception {
    ToogleList<String> toogleList = ToogleList.create("A", "B", "C");
    assertEquals("A", toogleList.getNextElement());
    assertEquals("B", toogleList.getNextElement());
    assertEquals("C", toogleList.getNextElement());
    assertEquals("A", toogleList.getNextElement());
  }

  public void testEmptyList() throws Exception {
    ToogleList<String> toogleList = ToogleList.create();
    assertNull(toogleList.getNextElement());
    assertNull(toogleList.getNextElement());
  }
}
