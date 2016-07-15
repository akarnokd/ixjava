ixjava
=================

<a href='https://travis-ci.org/akarnokd/ixjava/builds'><img src='https://travis-ci.org/akarnokd/ixjava.svg?branch=1.x'></a>
[![codecov.io](http://codecov.io/github/akarnokd/ixjava/coverage.svg?branch=1.x)](http://codecov.io/github/akarnokd/ixjava?branch=1.x)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/ixjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/ixjava)

Interactive Extensions for Java, the dual of RxJava. Originally implemented in the Reactive4Java framework, now converted to work with RxJava.

The aim is to provide pull-based datastream support with the same naming as in RxJava mainly for the pre-Java-8 world. The Stream API in Java 8 is not exactly the same thing because Streams can be only consumed once while Iterables can be consumed many times. Google Guava features a lot of Iterable operators but without method chaining support.

**This branch starts from scratch by reimplementing `ix.Ix` and all of its operators based on the +5 year experience with reactive
and interactive dataflows.**

# Releases

**gradle**

```
dependencies {
    compile "com.github.akarnokd:ixjava:1.0.0-RC1"
}
```

**ivy**

```
<dependencies>
		<dependency org="com.github.akarnokd" name="ixjava" rev="1.0.0-RC1" />
</dependencies>
```

Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)
