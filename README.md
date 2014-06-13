ixjava
=================

Interactive Extensions for Java, the dual of RxJava. Originally implemented in the Reactive4Java framework, now converted to work with RxJava.

The aim is to provide pull-based datastream support with the same naming as in RxJava mainly for the pre-Java-8 world. The Stream API in Java 8 is not exactly the same thing because Streams can be only consumed once while Iterables can be consumed many times. Google Guava features a lot of Iterable operators but without method chaining support.
