package moontime.droid.util;

import android.text.SpannableStringBuilder;

import com.google.common.base.Preconditions;

public class SpannableBuilder extends SpannableStringBuilder {

  private int _currentSpanStart = -1;

  public SpannableBuilder startSpan() {
    _currentSpanStart = length();
    return this;
  }

  public SpannableBuilder closeSpan(Object span, int spanFlag) {
    Preconditions.checkState(_currentSpanStart > -1, "span not started yet");
    if (span != null) {
      setSpan(span, _currentSpanStart, length(), spanFlag);
    }
    _currentSpanStart = -1;
    return this;
  }

  public void setSpan(Object span, int spanFlag) {
    setSpan(span, _currentSpanStart, length(), spanFlag);
  }
}
