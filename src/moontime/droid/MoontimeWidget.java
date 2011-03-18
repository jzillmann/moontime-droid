package moontime.droid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonEvent.EventAllocation;
import moontime.MoonEventType;
import moontime.MoonPhaseAlgorithm;
import moontime.MoonUtil;
import moontime.alg.MoonToolPhaseAlgorithm;
import moontime.droid.util.SpannableBuilder;
import moontime.droid.util.Util;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.RemoteViews;

public class MoontimeWidget extends AppWidgetProvider {

  private static MoonPhaseAlgorithm _moonAlgorithm = new MoonToolPhaseAlgorithm();
  private static long DEBUG_ADDITIONAL_TIME = 0;
  private static long SHOW_EVENT_AFTER_PASSED_AWAY_TIME = Util.hoursToMillis(48);

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int widgetId : appWidgetIds) {
      updateView(context, appWidgetManager, widgetId);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
      Uri data = intent.getData();
      int ordinal = Integer.parseInt(data.getSchemeSpecificPart());
      Action control = Action.values()[ordinal];
      control.execute(this, context);
    }
  }

  static void updateView(Context context, AppWidgetManager appWidgetManager, int widgetId) {
    long now = System.currentTimeMillis();
    long phaseHuntStartTime = now - SHOW_EVENT_AFTER_PASSED_AWAY_TIME;
    if (true) {// debug only
      phaseHuntStartTime += DEBUG_ADDITIONAL_TIME;
      now += DEBUG_ADDITIONAL_TIME;
      System.out.println("now: " + new Date(now));
    }
    WidgetPreferences preferences = WidgetPreferences.initFromPreferences(context, widgetId);
    DateFormat datePattern = new SimpleDateFormat(preferences.getDatePattern());
    Log.d("moontime", "now=" + datePattern.format(new Date(now)));
    List<MoonEvent> moonEvents = _moonAlgorithm.getNextMoonEvents(MoonUtil.newCalender(phaseHuntStartTime), 3,
        EnumSet.of(MoonEventType.NEW_MOON, MoonEventType.FULL_MOON));

    SpannableBuilder builder = new SpannableBuilder();
    MoonEvent nextMoonEvent = null;
    // boolean passedEvent = false;
    for (int i = 0; i < moonEvents.size(); i++) {
      if (i > 0) {
        builder.append("\n");
      }
      MoonEvent moonEvent = moonEvents.get(i);
      EventAllocation eventAllocation = EventAllocation.getEventAllocation(now, moonEvent.getDate().getTime(),
          Util.hoursToMillis(12), Util.hoursToMillis(24));
      if (eventAllocation != EventAllocation.IN_PAST && nextMoonEvent == null) {
        System.out.println(moonEvent + " / " + eventAllocation);
        nextMoonEvent = moonEvent;
      }
      String moonEventString = toString(moonEvent, datePattern, eventAllocation);
      builder.startSpan().append(moonEventString);
      if (eventAllocation == EventAllocation.IN_PRESENT) {
        builder.setSpan(new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
      builder.closeSpan(getSpan(eventAllocation, moonEvent == nextMoonEvent), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    views.setTextViewText(R.id.nextMoons, builder);
    views.setTextColor(R.id.nextMoons, context.getResources().getColor(preferences.getTheme().getTextColor()));

    int moonPicId;
    if (nextMoonEvent.getType() == MoonEventType.FULL_MOON) {
      moonPicId = preferences.getTheme().getFullMoonPicId();
    } else {
      moonPicId = preferences.getTheme().getNewMoonPicId();
    }
    views.setImageViewResource(R.id.moonPic, moonPicId);

    for (Action c : Action.values()) {
      views.setOnClickPendingIntent(c.getId(), c.createIntent(context));
    }
    appWidgetManager.updateAppWidget(widgetId, views);
  }

  private static Object getSpan(EventAllocation eventAllocation, boolean isNextMoonEvent) {
    switch (eventAllocation) {
    case IN_FUTURE:
      if (!isNextMoonEvent) {
        return new StyleSpan(Typeface.ITALIC);
      }
      return new StyleSpan(Typeface.BOLD);
    case IN_PRESENT:
      return new UnderlineSpan();
    case IN_PAST:
      return new StrikethroughSpan();
    default:
      throw new UnsupportedOperationException(eventAllocation.toString());
    }
  }

  private static String toString(MoonEvent moonEvent, DateFormat datePattern, EventAllocation eventAllocation) {
    String string = datePattern.format(moonEvent.getDate());
    return string;
  }

  private static ComponentName[] WIDGET_PROVIDERS = { new ComponentName(MoontimeWidget.class.getPackage().getName(),
      MoontimeWidget.class.getName()) };

  enum Action {
    DEBUG_ADD_TIME(R.id.debug_AddTimeButton) {
      @Override
      public void execute(MoontimeWidget moontimeWidget, Context context) {
        MoontimeWidget.DEBUG_ADDITIONAL_TIME += Util.hoursToMillis(12);
        updateViews(context);
      }
    },

    DEBUG_REMOVE_TIME(R.id.debug_RemoveTimeButton) {
      @Override
      public void execute(MoontimeWidget moontimeWidget, Context context) {
        MoontimeWidget.DEBUG_ADDITIONAL_TIME -= Util.hoursToMillis(12);
        updateViews(context);
      }
    };

    private int _id;

    Action(int id) {
      _id = id;
    }

    public abstract void execute(MoontimeWidget moontimeWidget, Context context);

    public int getId() {
      return _id;
    }

    PendingIntent createIntent(Context context) {
      Intent i = new Intent(context, MoontimeWidget.class);
      i.addCategory(Intent.CATEGORY_ALTERNATIVE);
      i.setData(Uri.parse("custom:" + ordinal()));
      return PendingIntent.getBroadcast(context, 0, i, 0);
    }

    private static void updateViews(Context context) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      for (ComponentName provider : WIDGET_PROVIDERS) {
        int[] ids = appWidgetManager.getAppWidgetIds(provider);
        for (int widgetId : ids) {
          updateView(context, appWidgetManager, widgetId);
        }
      }
    }
  }

}
