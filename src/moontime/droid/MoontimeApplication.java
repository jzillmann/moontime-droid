package moontime.droid;

import java.util.List;

import moontime.droid.store.GlobalPreferences;
import roboguice.application.RoboApplication;
import roboguice.inject.SharedPreferencesName;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class MoontimeApplication extends RoboApplication {

  public static final MoontimeService _moontimeServiceInstance = new MoontimeService();

  @Override
  protected void addApplicationModules(List<Module> modules) {
    modules.add(new MoontimeModule());
  }

  static class MoontimeModule extends AbstractModule {

    @Override
    public void configure() {
      bindConstant().annotatedWith(SharedPreferencesName.class).to(GlobalPreferences.GLOBAL_PREFERENCES);
      bind(MoontimeService.class).toInstance(_moontimeServiceInstance);
      // requestStaticInjection(MoontimeWidget.class);
    }
  }

}
