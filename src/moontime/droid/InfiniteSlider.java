package moontime.droid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class InfiniteSlider extends Gallery {

  private TypedArray _styleAttributes;

  public InfiniteSlider(Context context, AttributeSet attrs) {
    super(context, attrs);
    _styleAttributes = context.obtainStyledAttributes(attrs, R.styleable.InfiniteSlider);
  }

  public void init(InfiniteAdapter adapter) {
    setAdapter(adapter);
    adapter.initAttributes(_styleAttributes);
  }

  public static abstract class InfiniteAdapter extends BaseAdapter {

    public static final int COUNT = Integer.MAX_VALUE;
    public static final int MID_POSITION = COUNT / 2;
    private final Context _context;
    private final ColorStateList _sliderItemColors;
    private int _textSize;

    public InfiniteAdapter(Context context) {
      _context = context;
      _sliderItemColors = context.getResources().getColorStateList(R.color.calendar_slider_item);
    }

    public void initAttributes(TypedArray styleAttributes) {
      _textSize = styleAttributes.getInt(R.styleable.InfiniteSlider_text_size, 30);
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
      textView.setTextSize(_textSize);
      textView.setTextColor(_sliderItemColors);
      return textView;
    }
  }

}
