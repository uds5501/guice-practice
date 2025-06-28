package com.guice_practice.server.commons;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

// Custom scoping from -> https://github.com/google/guice/wiki/CustomScopes
public class LifecycleModule implements Module
{
  private final LifecycleScope globalScope = new LifecycleScope(Lifecycle.InternalScope.GLOBAL);
  private final LifecycleScope requestScope = new LifecycleScope(Lifecycle.InternalScope.REQUEST);

  @Override
  public void configure(Binder binder)
  {
    // here we bind the Annotations for Guice to the Scopes built for Lifecycle.
    // In druid i can see ManagedLifecycle binds to LifecycleScope.NORMAL.
    // lemme try global + per request.
    binder.bindScope(ManagedGlobalLifecycle.class, globalScope);
    binder.bindScope(ManagedRequestLifecycle.class, requestScope);
  }

  @Provides
  @Singleton
  public Lifecycle lifecycleProvider(final Injector injector)
  {
    Lifecycle lifecycle = new Lifecycle();
    globalScope.setLifecycle(lifecycle);
    requestScope.setLifecycle(lifecycle);

    return lifecycle;
  }
}
