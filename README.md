ixjava
=================

<a href='https://github.com/akarnokd/ixjava/actions?query=workflow%3A%22Java+CI+with+Gradle%22'><img src='https://github.com/akarnokd/ixjava/workflows/Java%20CI%20with%20Gradle/badge.svg'></a>
[![codecov.io](http://codecov.io/github/akarnokd/ixjava/coverage.svg?branch=1.x)](http://codecov.io/github/akarnokd/ixjava?branch=1.x)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/ixjava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.akarnokd/ixjava)

Iterable Extensions for Java, the dual of RxJava. Originally implemented in the Reactive4Java framework, now standalone; no dependencies on any reactive library.

The aim is to provide, lazily evaluated, pull-based datastream support with the same naming as in RxJava mainly for the pre-Java-8 world. The Stream API in Java 8 is not exactly the same thing because Streams can be only consumed once while `Iterable`s can be consumed many times. Google Guava features a lot of Iterable operators, plus now they have the `FluentIterable` with similar setup
but far less operators available.

**This branch starts from scratch by reimplementing `ix.Ix` and all of its operators based on the +5 year experience with reactive
and interactive dataflows.**

# Releases

Javadoc: https://akarnokd.github.io/ixjava/javadoc/index.html

**gradle**

```groovy
dependencies {
    implementation 'com.github.akarnokd:ixjava:1.0.0'
}
```

Maven search:

[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.akarnokd%22)

# Examples

The main (and only) entry point is the class `ix.Ix` which features a bunch of static factory methods:

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

Ix<Integer> seq = Ix.from(list);
```

Now we can apply instance methods on the `seq` sequence, just like in RxJava. Not all operators are available though due to the synchronous-pull nature of IxJava.

```java
seq
.map(v -> v + 1)
.filter(v -> v % 2 == 0)
.flatMap(v -> Ix.fromArray(v * 10, v * 100)))
.subscribe(System.out::println)
;
```

Since `Ix` implements `Iterable`, you can use the for-each loop to consume it:

```java

for (Integer v : Ix.fromArray(5, 10).skip(1).concatWith(Ix.just(20))) {
    System.out.println("Value: " + v);
}
```

For further details on the possibilities, please study the javadoc of `Ix`.
