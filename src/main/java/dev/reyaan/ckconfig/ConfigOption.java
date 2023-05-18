package dev.reyaan.ckconfig;


import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ConfigOption {
    private String key;
    private Object value;
    private String description;
    private Field connectedField;
    private Integer max;
    private Integer min;

    public ConfigOption(@NotNull String key, @NotNull Object value, @NotNull Field connectedField) {
        this.setKey(key);
        this.setValue(value);
        this.setConnectedField(connectedField);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Field getConnectedField() {
        return connectedField;
    }

    public void setConnectedField(Field connectedField) {
        this.connectedField = connectedField;
    }

    public Number getMax() {
        return max;
    }

    public void setMax(int max) {
        if (value instanceof Number) this.max = max;
    }

    public Number getMin() {
        return min;
    }

    public void setMin(int min) {
        if (value instanceof Number) this.min = min;
    }
}