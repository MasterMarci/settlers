package de.hs.settlers.configuration;

public class ConfigurationHolder<T> {

    private String name;
    private T value;
    private final T defaultValue;

    public ConfigurationHolder(T value, String name) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
