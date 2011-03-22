package moontime.droid;

import moontime.droid.store.WidgetPreferences;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;

public class WidgetConfigurationActivity extends RoboActivity {

  @Inject
  private MoontimeService _moontimeService;
  private int _widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  @InjectView(R.id.datePattern)
  private EditText _datePatternText;
  @InjectView(R.id.themeSpinner)
  private Spinner _themeSpinner;

  public int getWidgetId() {
    return _widgetId;
  }

  public EditText getDatePatternText() {
    return _datePatternText;
  }

  public Spinner getThemeSpinner() {
    return _themeSpinner;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Set the result to CANCELED. This will cause the widget host to cancel
    // out of the widget placement if they press the back button.
    setResult(RESULT_CANCELED);

    // Find the widget id from the intent.
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      _widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // If they gave us an intent without the widget id, just bail.
    if (_widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
    setContentView(R.layout.configuration_layout);
    ArrayAdapter<WidgetTheme> adapter = new ArrayAdapter<WidgetTheme>(this, android.R.layout.simple_spinner_item,
        WidgetTheme.values());
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    _themeSpinner.setAdapter(adapter);
  }

  public void submit(View view) {
    Context context = this;
    WidgetPreferences widgetPreferences = WidgetPreferences.initFromView(this);
    widgetPreferences.storeToPreferences(context);

    // Push widget update to surface with newly set prefix
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    MoontimeWidget.updateView(_moontimeService, context, appWidgetManager, _widgetId);

    // Make sure we pass back the original appWidgetId
    Intent resultValue = new Intent();
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _widgetId);
    setResult(RESULT_OK, resultValue);
    finish();
    Log.i("config", "finished configuration of widget '" + _widgetId + "'");

  }

}
