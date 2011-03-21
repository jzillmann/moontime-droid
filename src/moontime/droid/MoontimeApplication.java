package moontime.droid;

import java.util.List;

import moontime.MoonPhaseAlgorithm;
import moontime.alg.MoonToolPhaseAlgorithm;
import moontime.droid.store.GlobalPreferences;
import roboguice.application.RoboApplication;
import roboguice.inject.SharedPreferencesName;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class MoontimeApplication extends RoboApplication {

  public static final MoonPhaseAlgorithm MOON_PHASE_ALGORITHM = new MoonToolPhaseAlgorithm();

  @Override
  protected void addApplicationModules(List<Module> modules) {
    modules.add(new MoontimeModule());
  }

  static class MoontimeModule extends AbstractModule {

    @Override
    public void configure() {
      bindConstant().annotatedWith(SharedPreferencesName.class).to(GlobalPreferences.GLOBAL_PREFERENCES);
      bind(MoonPhaseAlgorithm.class).toInstance(MOON_PHASE_ALGORITHM);
      requestStaticInjection(MoontimeWidget.class);
    }
  }
}
