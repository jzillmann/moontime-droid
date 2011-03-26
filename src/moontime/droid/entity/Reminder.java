package moontime.droid.entity;

public class Reminder {

  private String _text;
  private boolean _checked;

  public Reminder(String text) {
    _text = text;
  }

  protected Reminder() {
    // for jackson
  }

  public String getText() {
    return _text;
  }

  public void setText(String text) {
    _text = text;
  }

  public boolean isChecked() {
    return _checked;
  }

  public void setChecked(boolean checked) {
    _checked = checked;
  }

  @Override
  public String toString() {
    return _text;
  }

}
