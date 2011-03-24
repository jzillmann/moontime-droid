package moontime.droid;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class CalenderActivity extends RoboActivity {

  static final int DATE_DIALOG_ID = 0;

  @InjectView(R.id.pickDate)
  Button _pickDateButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calender_layout);

    // add a click listener to the button
    _pickDateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(DATE_DIALOG_ID);
      }
    });

  }

  // the callback received when the user "sets" the date in the dialog
  private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      System.out.println(year + " / " + monthOfYear);
      // updateDisplay();
    }
  };

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case DATE_DIALOG_ID:
      return new DatePickerDialog(this, mDateSetListener, 1, 1, -1);
    }
    return null;
  }
}
