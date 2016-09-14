/*
 * Copyright 2011-2016 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ix;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Example benchmark. Run from command line as
 * <br>
 * gradle jmh -Pjmh='IxPerf'
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class FlattenIterableArrayPerf implements IxFunction<Integer, Iterable<Integer>> {

    @Param({"1", "10", "100", "1000", "10000", "100000", "1000000"})
    public int count;

    Ix<Integer> source;

    Ix<Integer> inner;

    Ix<Integer> flatMapJust;

    @Override
    public Iterable<Integer> apply(Integer t) {
        return inner;
    }

    @Setup
    public void setup(Blackhole bh) {

        int d = 1000000 / count;

        Integer[] countArray = new Integer[count];
        Arrays.fill(countArray, 777);

        inner = Ix.fromArray(countArray);

        Integer[] sourceArray = new Integer[d];
        Arrays.fill(sourceArray, 777);

        source = Ix.fromArray(sourceArray).flatMap(this);

        flatMapJust = inner.flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.just(v);
            }
        });
    }

    @Benchmark
    public Object xrangeLast() {
        return source.last();
    }

    @Benchmark
    public void xrangeEach(Blackhole bh) {
        for (Integer i : source) {
            bh.consume(i);
        }
    }

    @Benchmark
    public void flatMapJust(Blackhole bh) {
        for (Integer i : flatMapJust) {
            bh.consume(i);
        }
    }
}
