# CKConfig
Super lightweight YAML config library for Java.
Not intended for complex usage or categories, only simple values.


Usage:
1. Create a `ConfigBuilder`
```java
public class TestConfig implements ConfigBuilder {
    @Description("A list of options~")
    public static List<String> FOO = Arrays.asList("One", "Two", "Three");

    private static LinkedHashMap<String, Integer> createMap() {
        LinkedHashMap<String, Integer> myMap = new LinkedHashMap<>();
        myMap.put("a", 1);
        myMap.put("c", 98);
        return myMap;
    }

    public static LinkedHashMap<String, Integer> BAR = createMap();

    @Description("It's my favourite number!!")
    @IntRange(min = 20, max = 80)
    public static int myFavourite_Number = 55;

    @Description("this is a float though, not a double, it is parsed correctly :)")
    public static float butterfingers = 55.25F;

    @Override
    public String key() {
        return "mystuff/main_settings";
    }
}
```

2. Create a `ConfigManager`
```java
String configDirectoryPath = "./run/config";
ConfigManager manager = new ConfigManager(configDirectoryPath);
manager.registerConfigBuilder(new TestConfig());

# Reads if valid else uses default value
System.out.println(TestConfig.myFavourite_Number);

# Reload config files
# manager.reloadConfigs(new TestConfig());
```

Output file:

<img width="251" alt="screenshot 2023-05-18 at 10 40 08 AM" src="https://github.com/corgi-in-tights/ckconfig/assets/59304120/4980b6b8-e2b8-4ff9-a1f5-588f2228d54f">

```yaml
# A list of options that can be toggled
FOO:
- One
- Two
- Three

BAR:
    a: 1
    c: 98

# It's my favourite number!!
# Must be between 20 and 80
myFavourite_Number: 55

# this is a float though, not a double, it is parsed correctly :)
butterfingers: 55.25
```
