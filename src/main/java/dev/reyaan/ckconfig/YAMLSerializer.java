package dev.reyaan.ckconfig;

import jdk.jfr.Description;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class YAMLSerializer {
    public static String serializeAll(List<ConfigOption> options) {
        var s = new StringBuilder();

        var dumperOptions = new DumperOptions();
        dumperOptions.setIndent(4);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        var yaml = new Yaml(dumperOptions);

        for (var option : options) {
            s.append(serialize(option, yaml)).append("\n");
        }
        return s.toString();
    }

    public static String serialize(ConfigOption option, Yaml yaml) {
        var s = new StringBuilder();
        if (option.getDescription() != null) {
            s.append("# ").append(option.getDescription()).append("\n");
        }

        if(option.getMin() != null && option.getMax() != null) {
            s.append("# Must be between ").append(option.getMin()).append(" and ").append(option.getMax()).append("\n");
        }

        s.append(yaml.dump(Map.of(option.getKey(), option.getValue())));

        return s.toString();
    }


    public static void deserialize(ConfigOption option, Object data) throws IllegalAccessException {
        var original = option.getValue();
        var field = option.getConnectedField();

        // hardcoded ze nums
        if (original instanceof Number valueNum && data instanceof Number dataNum) {
            if (valueNum instanceof Integer) {
                field.setInt(null, constrain(dataNum.intValue(), field));

            } else if (valueNum instanceof Double) {
                field.setDouble(null, constrain(dataNum.doubleValue(), field));

            } else if (valueNum instanceof Float) {
                field.setFloat(null, constrain(dataNum.floatValue(), field));

            } else if (valueNum instanceof Byte) {
                field.setByte(null, dataNum.byteValue());

            }
        } else if (data.getClass() == original.getClass()) {
            option.getConnectedField().set(null, data);
        }
    }



    public static int constrain(int value, Field field) {
        var constrained = field.isAnnotationPresent(IntRange.class);
        if (constrained) {
            var intRange = field.getAnnotation(IntRange.class);
            return Math.min(Math.max(value, intRange.min()), intRange.max());
        }
        return value;
    }

    public static double constrain(double value, Field field) {
        var constrained = field.isAnnotationPresent(IntRange.class);
        if (constrained) {
            var intRange = field.getAnnotation(IntRange.class);
            return Math.min(Math.max(value, intRange.min()), intRange.max());
        }
        return value;
    }

    public static float constrain(float value, Field field) {
        var constrained = field.isAnnotationPresent(IntRange.class);
        if (constrained) {
            var intRange = field.getAnnotation(IntRange.class);
            return Math.min(Math.max(value, intRange.min()), intRange.max());
        }
        return value;
    }
}
