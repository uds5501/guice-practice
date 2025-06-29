package com.guice_practice.server.commons;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LifecycleScope implements Scope
{
  private static final Logger logger = LoggerFactory.getLogger(LifecycleScope.class);

  private final Lifecycle.InternalScope internalScope;
  private Lifecycle lifecycle;

  public void setLifecycle(Lifecycle lifecycle)
  {
    synchronized (instances) {
      this.lifecycle = lifecycle;
      for (Object instance : instances) {
        lifecycle.addManagedGlobalInstance(instance, internalScope);
      }
    }
  }

  // all the classes that are managed by this particular scope.
  private final List<Object> instances = new ArrayList<>();

  public LifecycleScope(Lifecycle.InternalScope internalScope) {this.internalScope = internalScope;}

  @Override
  public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped)
  {
    // copy pasted the implementation tbh.
    return new Provider<>()
    {
      private volatile T value = null;

      @Override
      public synchronized T get()
      {
        if (value == null) {
          final T retVal = unscoped.get();

          synchronized (instances) {
            if (lifecycle == null) {
              // at start, no lifecycle will be present, so saving the instances in the list.
              instances.add(retVal);
            } else {
              try {
                lifecycle.addAndMaybeStartManagedGlobalInstance(retVal, internalScope);
              }
              catch (Exception e) {
                logger.warn("Failed to add instance to lifecycle: {}", e.getMessage(), e);
                return null;
              }
            }
          }
          value = retVal;
        }

        return value;
      }
    };
  }
}
