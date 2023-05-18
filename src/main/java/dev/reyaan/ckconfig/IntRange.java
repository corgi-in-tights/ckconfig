package dev.reyaan.ckconfig;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntRange {
    int min();
    int max();
}