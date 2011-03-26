package moontime.droid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonEvent.EventAllocation;
import moontime.MoonEventType;
import moontime.droid.store.WidgetPreferences;
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
import android.widget.RemoteViews;

import com.google.inject.Inject;

public class MoontimeWidget extends AppWidgetProvider {

  private static final Class<?> ON_CLICK_ACTIVITY = MoontimeTabActivity.class;

  @Inject
  protected MoontimeService _moontimeService;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    updateService();
    for (int widgetId : appWidgetIds) {
      updateView(_moontimeService, context, appWidgetManager, widgetId);
    }
  }

  private void updateService() {
    if (_moontimeService == null) {
      _moontimeService = MoontimeApplication._moontimeServiceInstance;
      // don't know how to bind here
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    updateService();
    if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
      Uri data = intent.getData();
      int ordinal = Integer.parseInt(data.getSchemeSpecificPart());
      Action control = Action.values()[ordinal];
      control.execute(_moontimeService, this, context);
    }
  }

  static void updateView(MoontimeService moontimeService, Context context, AppWidgetManager appWidgetManager,
      int widgetId) {
    long now = System.currentTimeMillis();
    WidgetPreferences preferences = WidgetPreferences.initFromPreferences(context, widgetId);
    DateFormat datePattern = new SimpleDateFormat(preferences.getDatePattern());
    List<MoonEvent> moonEvents = moontimeService.getNextMoonEvents(3);

    SpannableBuilder builder = new SpannableBuilder();
    MoonEvent nextMoonEvent = null;
    for (int i = 0; i < moonEvents.size(); i++) {
      if (i > 0) {
        builder.append("\n");
      }
      MoonEvent moonEvent = moonEvents.get(i);
      EventAllocation eventAllocation = EventAllocation.getEventAllocation(now, moonEvent.getDate().getTime(),
          Util.hoursToMillis(12), Util.hoursToMillis(24));
      if (eventAllocation != EventAllocation.IN_PAST && nextMoonEvent == null) {
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

    Intent intent = new Intent(context, ON_CLICK_ACTIVITY);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    views.setOnClickPendingIntent(R.id.moonPic,
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

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
        // return new StyleSpan(Typeface.ITALIC);
        return null;
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
      public void execute(MoontimeService moontimeService, MoontimeWidget moontimeWidget, Context context) {
        moontimeService.setDebugAdditionalTime(moontimeService.getDebugAdditionalTime() + Util.hoursToMillis(12));
        updateViews(moontimeService, context);
      }
    },

    DEBUG_REMOVE_TIME(R.id.debug_RemoveTimeButton) {
      @Override
      public void execute(MoontimeService moontimeService, MoontimeWidget moontimeWidget, Context context) {
        moontimeService.setDebugAdditionalTime(moontimeService.getDebugAdditionalTime() - Util.hoursToMillis(12));
        updateViews(moontimeService, context);
      }
    },

    RESET_ADD_TIME(R.id.debug_ResetTimeButton) {
      @Override
      public void execute(MoontimeService moontimeService, MoontimeWidget moontimeWidget, Context context) {
        moontimeService.setDebugAdditionalTime(0);
        updateViews(moontimeService, context);
      }
    };

    private int _id;

    Action(int id) {
      _id = id;
    }

    public abstract void execute(MoontimeService moontimeService, MoontimeWidget moontimeWidget, Context context);

    public int getId() {
      return _id;
    }

    PendingIntent createIntent(Context context) {
      Intent i = new Intent(context, MoontimeWidget.class);
      i.addCategory(Intent.CATEGORY_ALTERNATIVE);
      i.setData(Uri.parse("custom:" + ordinal()));
      return PendingIntent.getBroadcast(context, 0, i, 0);
    }

    private static void updateViews(MoontimeService moontimeService, Context context) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      for (ComponentName provider : WIDGET_PROVIDERS) {
        int[] ids = appWidgetManager.getAppWidgetIds(provider);
        for (int widgetId : ids) {
          updateView(moontimeService, context, appWidgetManager, widgetId);
        }
      }
    }
  }

}
