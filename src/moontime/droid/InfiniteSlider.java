package moontime.droid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class InfiniteSlider extends Gallery {

  public InfiniteSlider(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(InfiniteAdapter adapter) {
    setAdapter(adapter);
  }

  public static abstract class InfiniteAdapter extends BaseAdapter {

    public static final int COUNT = Integer.MAX_VALUE;
    public static final int MID_POSITION = COUNT / 2;
    private final Context _context;
    private final ColorStateList _sliderItemColors;

    public InfiniteAdapter(Context context) {
      _context = context;
      _sliderItemColors = context.getResources().getColorStateList(R.color.calender_slider_item);
    }

    @Override
    public int getCount() {
      return COUNT;
    }

    @Override
    public abstract String getItem(int position);

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView textView = new TextView(_context);
      textView.setText(getItem(position));
      textView.setTextSize(20);
      textView.setTextColor(_sliderItemColors);
      return textView;
    }
  }

}
