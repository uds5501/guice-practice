package com.guice_practice.server.commons;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // I only care about managing classes with this annotation.
@ScopeAnnotation
public @interface ManagedGlobalLifecycle
{
}
