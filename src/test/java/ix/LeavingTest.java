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

import java.io.*;
import java.util.*;

import org.junit.*;

public class LeavingTest {

    @Test
    public void normal() {
        List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).into(list);

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void print() throws Exception {
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout));
        try {
            Ix.range(1, 30).print();

            System.out.flush();
        } finally {
            System.setOut(old);
        }

        String[] s = bout.toString("UTF-8").split(System.getProperty("line.separator"));

        Assert.assertEquals(Ix.range(1, 23).join().first() + ", ", s[0]);
        Assert.assertEquals(Ix.range(24, 7).join().first(), s[1]);
    }

    @Test
    public void print40() throws Exception {
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout));
        try {
            Ix.range(1, 30).print(",", 40);

            System.out.flush();
        } finally {
            System.setOut(old);
        }

        String[] s = bout.toString("UTF-8").split(System.getProperty("line.separator"));

        Assert.assertEquals(Ix.range(1, 17).join(",").first() + ",", s[0]);
        Assert.assertEquals(Ix.range(18, 13).join(",").first(), s[1]);
    }

    @Test
    public void println() throws Exception {
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout));
        try {
            Ix.range(1, 3).println();

            System.out.flush();
        } finally {
            System.setOut(old);
        }

        String[] s = bout.toString("UTF-8").split(System.getProperty("line.separator"));

        Assert.assertEquals("1", s[0]);
        Assert.assertEquals("2", s[1]);
        Assert.assertEquals("3", s[2]);
    }

    @Test
    public void printlnPrefix() throws Exception {
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout));
        try {
            Ix.range(1, 3).println("--");

            System.out.flush();
        } finally {
            System.setOut(old);
        }

        String[] s = bout.toString("UTF-8").split(System.getProperty("line.separator"));

        Assert.assertEquals("--1", s[0]);
        Assert.assertEquals("--2", s[1]);
        Assert.assertEquals("--3", s[2]);
    }

    @Test
    public void run() {
        final List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).doOnNext(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                list.add(v);
            }
        }).run();

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void subscribe() {
        final List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).doOnNext(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                list.add(v);
            }
        }).subscribe();

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void subscribeAction1() {
        final List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).subscribe(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                list.add(v);
            }
        });

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void subscribeAction1Action1() {
        final List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).subscribe(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                list.add(v);
            }
        }, new IxConsumer<Throwable>() {
            @Override
            public void accept(Throwable t) {

            }
        });

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void subscribeAction1Action1Throws() {
        final List<Integer> list = new ArrayList<Integer>();

        final Throwable[] error = { null };

        Ix.range(1, 5).subscribe(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                if (v == 5) {
                    throw new IllegalStateException();
                }
                list.add(v);
            }
        }, new IxConsumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                error[0] = t;
            }
        });

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list);
        Assert.assertTrue(String.valueOf(error[0]), error[0] instanceof IllegalStateException);
    }

    @Test
    public void subscribeAction1Action1Action0() {
        final List<Integer> list = new ArrayList<Integer>();

        Ix.range(1, 5).subscribe(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                list.add(v);
            }
        }, new IxConsumer<Throwable>() {
            @Override
            public void accept(Throwable t) {

            }
        }, new Runnable() {
            @Override
            public void run() {
                list.add(100);
            }
        });

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 100), list);
    }

    @Test
    public void subscribeAction1Action1Action0Throws() {
        final List<Integer> list = new ArrayList<Integer>();

        final Throwable[] error = { null };

        Ix.range(1, 5).subscribe(new IxConsumer<Integer>() {
            @Override
            public void accept(Integer v) {
                if (v == 5) {
                    throw new IllegalStateException();
                }
                list.add(v);
            }
        }, new IxConsumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                error[0] = t;
            }
        }, new Runnable() {
            @Override
            public void run() {
                list.add(100);
            }
        });

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), list);
        Assert.assertTrue(String.valueOf(error[0]), error[0] instanceof IllegalStateException);
    }

    @Test
    public void retainAll() {
        List<Integer> list = Ix.range(1, 10).collectToList().first();

        Ix.from(list).retainAll(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) == 0;
            }
        });

        Assert.assertEquals(Arrays.asList(2, 4, 6, 8, 10), list);
    }

    @Test
    public void removeAll() {
        List<Integer> list = Ix.range(1, 10).collectToList().first();

        Ix.from(list).removeAll(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) == 1;
            }
        });

        Assert.assertEquals(Arrays.asList(2, 4, 6, 8, 10), list);
    }

    @Test
    public void single() {
        Assert.assertEquals(1, Ix.just(1).single().intValue());
    }

    @Test
    public void singleDefault() {
        Assert.assertEquals(1, Ix.just(1).single(2).intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void singleEmpty() {
        Assert.assertEquals(1, Ix.<Integer>empty().single().intValue());
    }

    @Test
    public void singleEmptyDefault() {
        Assert.assertEquals(2, Ix.<Integer>empty().single(2).intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void singleLonger() {
        Ix.range(1, 5).single();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void singleLongerDefault() {
        Ix.range(1, 5).single(10);
    }
}
