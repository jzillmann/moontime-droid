package moontime.droid.util;

import java.util.Arrays;

public class ToogleList<E> {

  private final E[] _elements;
  private int _currentPos;

  private ToogleList(E... elements) {
    _elements = elements;
  }

  public E getNextElement() {
    if (_elements.length == 0) {
      return null;
    }
    E nextElement = _elements[_currentPos++];
    if (_currentPos == _elements.length) {
      _currentPos = 0;
    }
    return nextElement;
  }

  @Override
  public String toString() {
    return Arrays.asList(_elements).toString();
  }

  public static <E> ToogleList<E> create(E... elements) {
    return new ToogleList<E>(elements);
  }
}
