csv
===

csv is a basic CSV parser written in pure Java, licensed under the 3-clause BSD license.

### Usage

csv is such a small library that it is best demonstrated with an example.

```java
try (final CsvParser parser = 
        new CsvParser(Files.newBufferedReader(Files.get("example.csv")))) {
    for (final CsvRow row : parser.parse()) {
        System.out.println(row.getValue("name"));
    }
} catch (final IOException ex) {
    ex.printStackTrace();
}
```
