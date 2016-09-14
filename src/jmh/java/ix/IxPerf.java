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
public class IxPerf {

//    @Param({"1", "10", "100", "1000", "10000", "100000", "1000000"})
//    public int count;

    @Setup
    public void setup(Blackhole bh) {

    }

    @Benchmark
    public Object just() {
        return new IxJust<Integer>(1).iterator().next();
    }

}
