ixjava
=================

Interactive Extensions for Java, the dual of RxJava. Originally implemented in the Reactive4Java framework, now converted to work with RxJava.

The aim is to provide pull-based datastream support with the same naming as in RxJava mainly for the pre-Java-8 world. The Stream API in Java 8 is not exactly the same thing because Streams can be only consumed once while Iterables can be consumed many times. Google Guava features a lot of Iterable operators but without method chaining support.

# Releases

You can download any necessary JAR files manually from

https://oss.sonatype.org/content/groups/public/com/github/akarnokd/ixjava/

Alternatively, you can use the usual maven dependency management to get the files:

**gradle**

```
dependencies {
    compile "com.github.akarnokd:ixjava:0.90.0"
}
```

**ivy**

```
<dependencies>
		<dependency org="com.github.akarnokd" name="ixjava" rev="0.90.0" />
</dependencies>
```

or the maven search facility

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)
