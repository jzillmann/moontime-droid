package moontime.droid;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonEventType;
import moontime.MoonPhaseAlgorithm;
import moontime.MoonUtil;
import moontime.alg.MoonToolPhaseAlgorithm;
import moontime.droid.util.Util;
import android.util.Log;

import com.google.common.collect.Iterables;
import com.google.inject.Singleton;

@Singleton
public class MoontimeService {

  public static final MoonPhaseAlgorithm MOON_PHASE_ALGORITHM = new MoonToolPhaseAlgorithm();
  private static final EnumSet<MoonEventType> MOON_EVENT_TYPES = EnumSet.of(MoonEventType.NEW_MOON,
      MoonEventType.FULL_MOON);
  private final static long SHOW_EVENT_AFTER_PASSED_AWAY_TIME = Util.hoursToMillis(48);
  private long _debugAdditionalTime = 0;

  public long getDebugAdditionalTime() {
    return _debugAdditionalTime;
  }

  public void setDebugAdditionalTime(long debugAdditionalTime) {
    _debugAdditionalTime = debugAdditionalTime;
  }

  public MoonEvent getNextMoonEvent() {
    return Iterables.getOnlyElement(getNextMoonEvents(1));
  }

  public List<MoonEvent> getNextMoonEvents(int count) {
    long now = System.currentTimeMillis();
    long phaseHuntStartTime = now - SHOW_EVENT_AFTER_PASSED_AWAY_TIME;

    // debug only
    phaseHuntStartTime += _debugAdditionalTime;
    now += _debugAdditionalTime;
    Log.d("debug", "now: " + new Date(now) + " (debug time: " + _debugAdditionalTime + " ms)");

    return MOON_PHASE_ALGORITHM.getNextMoonEvents(MoonUtil.newCalender(phaseHuntStartTime), count, MOON_EVENT_TYPES);
  }

}
