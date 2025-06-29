package com.guice_practice.server.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lifecycle
{
  private static final Logger logger = LoggerFactory.getLogger(Lifecycle.class);

  // each state will have a handler.
  private NavigableMap<InternalScope, ConcurrentSkipListSet<Handler>> handlers;
  private AtomicReference<State> lifecycleState = new AtomicReference<>(State.NEW);
  private final String name;
  private InternalScope currentScope = null;
  private final Lock starStopLock = new ReentrantLock();

  public Lifecycle()
  {
    this("defaultInit");
  }

  public Lifecycle(String name)
  {
    this.name = name;
    this.handlers = new TreeMap<>();
    for (InternalScope internalScope : InternalScope.values()) {
      handlers.put(internalScope, new ConcurrentSkipListSet<>());
    }
  }

  public <T> T addManagedGlobalInstance(T instance, InternalScope internalScope)
  {
    addHandler(internalScope, new AnnotatedHandlers(instance));
    return instance;
  }

  public <T> T addAndMaybeStartManagedGlobalInstance(T instance, InternalScope internalScope) throws Exception
  {
    addAndMaybeStartHandler(internalScope, new AnnotatedHandlers(instance));
    return instance;
  }

  private void addAndMaybeStartHandler(InternalScope internalScope, Handler handler) throws Exception
  {
    if (lifecycleState.get().equals(State.STOPPED)) {
      logger.error(
          "Cannot add handler to lifecycle {} in scope {}: lifecycle is already stopped.",
          name,
          internalScope
      );
      return;
    }
    starStopLock.lock();
    try {
      if (lifecycleState.get().equals(State.STOPPED)) {
        logger.error(
            "Cannot add handler to lifecycle {} in scope {}: lifecycle is already stopped.",
            name,
            internalScope
        );
        return;
      }
      // if started and already ahead, start this bad boy
      if (lifecycleState.get().equals(State.STARTED)) {
        if (internalScope.compareTo(currentScope) <= 0) {
          handler.start();
        }
      }
      handlers.get(internalScope).add(handler);
    }
    finally {
      starStopLock.unlock();
    }
  }


  public enum InternalScope
  {
    GLOBAL,
    REQUEST
  }

  public enum State
  {
    NEW,
    STARTED,
    STOPPED
  }

  // now to start this lifecycle.
  // i gotta check if it's already started? if not, then attempt to start it, go through all the handlers for STARTED.

  public void start() throws Exception
  {
    starStopLock.lock();
    try {
      if (!lifecycleState.get().equals(State.NEW)) {
        logger.error("Lifecycle {} is already started or stopped, cannot start again.", name);
        throw new Exception("Lifecycle not in NEW state, cannot start.");
      }
      if (!lifecycleState.compareAndSet(State.NEW, State.STARTED)) {
        logger.error("Lifecycle {} is already started or stopped, cannot start again.", name);
        throw new Exception("Lifecycle not in NEW state, cannot start.");
      }
      // set the state value to STARTED.
      for (Map.Entry<InternalScope, ConcurrentSkipListSet<Handler>> entry : handlers.entrySet()) {
        currentScope = entry.getKey();
        logger.info("Starting handlers for lifecycle {} in scope {}", name, entry.getKey());
        for (Handler handler : entry.getValue()) {
          try {
            handler.start();
          }
          catch (Exception e) {
            logger.error("Error starting handler in lifecycle {}: {}", name, e.getMessage(), e);
            throw e; // rethrow to indicate failure
          }
        }
      }
    }
    finally {
      starStopLock.unlock();
    }
  }

  public void stop()
  {
    starStopLock.lock();
    try {
      if (!lifecycleState.compareAndSet(State.STARTED, State.STOPPED)) {
        logger.error("Lifecycle {} is not in STARTED state, cannot stop.", name);
        return; // or throw an exception if you prefer
      }
      // gotta shut down all the handlers in all the scopes.
      // not caring about the order for now.
      for (InternalScope internalScope : InternalScope.values()) {
        logger.info("Stopping handlers for lifecycle {} in scope {}", name, internalScope);
        for (Handler handler : handlers.get(internalScope)) {
          try {
            handler.stop();
          }
          catch (Exception e) {
            logger.error("Error stopping handler in lifecycle {}: {}", name, e.getMessage(), e);
          }
        }
      }
    }
    finally {
      starStopLock.unlock();
    }
  }

  public void join() throws InterruptedException
  {
    Thread.currentThread().join();
  }

  // 2 orders possible.
  // try acquiring the lifecycle lock, if it fails, you shouldn't add
  // try checking for lifecycle state, if it's new and you try add, it's possible that the lifecycle might shut down later [RACE]
  public void addHandler(InternalScope internalScope, Handler handler)
  {
    if (!starStopLock.tryLock()) {
      logger.error(
          "Cannot add handler to lifecycle {} in scope {}: lifecycle is currently being started or stopped.",
          name,
          internalScope
      );
      return;
    }
    try {
      if (!lifecycleState.get().equals(State.NEW)) {
        logger.error(
            "Cannot add handler to lifecycle {} in scope {}: lifecycle is not in NEW state.", name,
            internalScope
        );
        return;
      }
      this.handlers.get(internalScope).add(handler);
    }
    finally {
      starStopLock.unlock();
    }
  }

  public interface Handler extends Comparable<Handler>
  {
    void start() throws Exception;

    void stop();

    // default priority is 0, higher number means higher priority.
    default int getPriority()
    {
      return 0;
    }

    @Override
    default public int compareTo(Handler o)
    {
      return Integer.compare(getPriority(), o.getPriority());
    }
  }

  private static class AnnotatedHandlers implements Handler
  {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedHandlers.class);
    private final Object o;

    private AnnotatedHandlers(Object o) {this.o = o;}

    @Override
    public void start() throws Exception
    {
      int annotatedMethods = 0;
      Method invokationMethod = null;
      for (Method method : o.getClass().getMethods()) {
        for (Annotation annotation : method.getAnnotations()) {
          if (LifecycleBegins.class.equals(annotation.annotationType())) {
            annotatedMethods++;
            invokationMethod = method;
          }
        }
      }
      if (annotatedMethods == 0) {
        logger.warn("No methods annotated with @LifecycleBegins found in class {}", o.getClass().getName());
        return;
      } else if (annotatedMethods > 1) {
        logger.warn(
            "Multiple methods annotated with @LifecycleBegins found in class {}, only the first will be invoked.",
            o.getClass().getName()
        );
        throw new Exception("Multiple methods annotated with @LifecycleBegins found in class " + o.getClass()
                                                                                                  .getName());
      }
      logger.info("Starting lifecycle handler for class {}", o.getClass().getName());
      invokationMethod.invoke(o);
    }

    @Override
    public void stop()
    {
      int annotatedMethods = 0;
      Method invokationMethod = null;
      for (Method method : o.getClass().getMethods()) {
        for (Annotation annotation : method.getAnnotations()) {
          if (ALifeEnds.class.equals(annotation.annotationType())) {
            annotatedMethods++;
            invokationMethod = method;
          }
        }
      }
      if (annotatedMethods == 0) {
        logger.warn("No methods annotated with @ALifeEnds found in class {}", o.getClass().getName());
        return;
      } else if (annotatedMethods > 1) {
        logger.warn(
            "Multiple methods annotated with @ALifeEnds found in class {}, only the first will be invoked.",
            o.getClass().getName()
        );
        return;
      }
      logger.info("Ending lifecycle handler for class {}", o.getClass().getName());
      try {
        invokationMethod.invoke(o);
      }
      catch (Exception e) {
        logger.error(
            "Error invoking method annotated with @ALifeEnds in class {}: {}",
            o.getClass().getName(),
            e.getMessage(),
            e
        );
      }
    }
  }
}
