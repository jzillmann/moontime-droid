package moontime.droid;

public enum WidgetTheme {

  /***/
  DEFAULT(R.drawable.background_fullmoon_default, R.drawable.background_newmoon_default,
      android.R.color.primary_text_dark),
  /***/
  YELLOW(R.drawable.background_fullmoon_yellow, R.drawable.background_newmoon_yellow, android.R.color.white);

  private final int _fullMoonPicId;
  private final int _newMoonPicId;
  private final int _textColor;

  private WidgetTheme(int fullMoonPicId, int newMoonPicId, int textColor) {
    _fullMoonPicId = fullMoonPicId;
    _newMoonPicId = newMoonPicId;
    _textColor = textColor;

  }

  public int getFullMoonPicId() {
    return _fullMoonPicId;
  }

  public int getNewMoonPicId() {
    return _newMoonPicId;
  }

  public int getTextColor() {
    return _textColor;
  }
}
