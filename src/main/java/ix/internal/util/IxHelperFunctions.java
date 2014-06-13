/*
* Copyright 2011-2014 David Karnok
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

package ix.internal.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Helper class with function types.
 */
public final class IxHelperFunctions {
    /**
     * Creates a function which returns always the same value.
     * @param <Param1> the parameter type, irrelevant
     * @param <Result> the value type to return
     * @param value the value to return
     * @return the function
     */
    public static <Param1, Result> Func1<Param1, Result> constant(final Result value) {
        return new Func1<Param1, Result>() {
            @Override
            public Result call(Param1 param1) {
                return value;
            }
        };
    }
    /**
     * Creates a function which returns always the same value.
     * @param <T> the value type to return
     * @param value the value to return
     * @return the function
     */
    public static <T> Func0<T> constant0(final T value) {
        return new Func0<T>() {
            @Override
            public T call() {
                return value;
            }
        };
    }
    
    /**
     * Returns a function which returns the greater of its parameters.
     * If only one of the parameters is null, the other parameter is returned.
     * If both parameters are null, null is returned.
     * @param <T> the parameter types, which must be self-comparable
     * @return the function
     */
    public static <T extends Comparable<? super T>> Func2<T, T, T> max() {
        return new Func2<T, T, T>() {
            @Override
            public T call(T param1, T param2) {
                if (param1 == null || param2 == null) {
                    if (param2 == null) {
                        return param1;
                    }
                    return param2;
                }
                return param1.compareTo(param2) < 0 ? param2 : param1;
            }
        };
    }
    /**
     * Returns a function which returns the smaller of its parameters.
     * If only one of the parameters is null, the other parameter is returned.
     * If both parameters are null, null is returned.
     * @param <T> the parameter types, which must be self-comparable
     * @return the function
     */
    public static <T extends Comparable<? super T>> Func2<T, T, T> min() {
        return new Func2<T, T, T>() {
            @Override
            public T call(T param1, T param2) {
                if (param1 == null || param2 == null) {
                    if (param2 == null) {
                        return param1;
                    }
                    return param2;
                }
                return param1.compareTo(param2) > 0 ? param2 : param1;
            }
        };
    }
    /** A helper function which returns its first parameter. */
    private static final Func2<Object, Object, Object> IDENTITY_FIRST = new Func2<Object, Object, Object>() {
        @Override
        public Object call(Object param1, Object param2) {
            return param1;
        }
    };
    /** A helper function which returns its second parameter. */
    private static final Func2<Object, Object, Object> IDENTITY_SECOND = new Func2<Object, Object, Object>() {
        @Override
        public Object call(Object param1, Object param2) {
            return param2;
        }
    };
    /**
     * Returns a helper function of two parameters which always returns its first parameter.
     * @param <T> the result and the first parameter type
     * @param <U> the second parameter type, irrelevant
     * @return the function
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Func2<T, U, T> identityFirst() {
        return (Func2<T, U, T>)IDENTITY_FIRST;
    }
    /**
     * Returns a helper function of two parameters which always returns its second parameter.
     * @param <T> the result and the second parameter type
     * @param <U> the first parameter type, irrelevant
     * @return the function
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Func2<T, U, U> identitySecond() {
        return (Func2<T, U, U>)IDENTITY_SECOND;
    }
    /**
     * Returns a function which returns the greater of its parameters in respect to the supplied <code>Comparator</code>.
     * If only one of the parameters is null, the other parameter is returned.
     * If both parameters are null, null is returned.
     * @param <T> the parameter types, which must be self-comparable
     * @param comparator the value comparator
     * @return the function
     */
    public static <T> Func2<T, T, T> max(
            final Comparator<? super T> comparator) {
        return new Func2<T, T, T>() {
            @Override
            public T call(T param1, T param2) {
                if (param1 == null || param2 == null) {
                    if (param2 == null) {
                        return param1;
                    }
                    return param2;
                }
                return comparator.compare(param1, param2) < 0 ? param2 : param1;
            }
        };
    }
    /**
     * Returns a function which returns the smaller of its parameters in respect to the supplied <code>Comparator</code>.
     * If only one of the parameters is null, the other parameter is returned.
     * If both parameters are null, null is returned.
     * @param <T> the parameter types, which must be self-comparable
     * @param comparator the value comparator
     * @return the function
     */
    public static <T> Func2<T, T, T> min(
            final Comparator<? super T> comparator) {
        return new Func2<T, T, T>() {
            @Override
            public T call(T param1, T param2) {
                if (param1 == null || param2 == null) {
                    if (param2 == null) {
                        return param1;
                    }
                    return param2;
                }
                return comparator.compare(param1, param2) > 0 ? param2 : param1;
            }
        };
    }
    /**
     * Returns a convenience comparator which basically compares
     * objects which implement the <code>Comparable</code>
     * interface. The comparator is null safe in the manner,
     * that nulls are always less than any non-nulls.
     * To have a comparator which places nulls last, use the <code>comparator0()</code> method.
     * @param <T> the element types to compare
     * @return the comparator
     * @see IxHelperFunctions#comparator0()
     */
    public static <T extends Comparable<? super T>> Comparator<T> comparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    }
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        };
    }
    /**
     * Returns a convenience comparator which basically compares objects which implement the <code>Comparable</code>
     * interface. The comparator is null safe in the manner, that nulls are always greater than any non-nulls.
     * To have a comparator which places nulls first, use the <code>comparator()</code> method.
     * @param <T> the element types to compare
     * @return the comparator
     */
    public static <T extends Comparable<? super T>> Comparator<T> comparator0() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    }
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return o1.compareTo(o2);
            }
        };
    }
    /**
     * Creates a new comparator which reverses the order of the comparison.
     * @param <T> the element type, which must be self comparable
     * @return the new comparator
     */
    public static <T extends Comparable<? super T>> Comparator<T> comparatorReverse() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.compareTo(o1);
            }
        };
    }
    /**
     * Creates a new comparator which reverses the order produced by the given
     * normal comparator.
     * @param <T> the element type
     * @param normal the normal comparator
     * @return the new comparator
     */
    public static <T> Comparator<T> comparatorReverse(
            final Comparator<? super T> normal) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return normal.compare(o2, o1);
            }
        };
    }
    /** Function to sum integers in aggregators. */
    static final Func2<Integer, Integer, Integer> SUM_INTEGER = new Func2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer param1, Integer param2) {
            return param1 != null ? param1 + param2 : param2;
        }
    };
    /** Function to sum integers in aggregators. */
    static final Func2<Float, Float, Float> SUM_FLOAT = new Func2<Float, Float, Float>() {
        @Override
        public Float call(Float param1, Float param2) {
            return param1 != null ? param1 + param2 : param2;
        }
    };
    /** Function to sum integers in aggregators. */
    static final Func2<Double, Double, Double> SUM_DOUBLE = new Func2<Double, Double, Double>() {
        @Override
        public Double call(Double param1, Double param2) {
            return param1 != null ? param1 + param2 : param2;
        }
    };
    /** Function to sum integers in aggregators. */
    static final Func2<Long, Long, Long> SUM_LONG = new Func2<Long, Long, Long>() {
        @Override
        public Long call(Long param1, Long param2) {
            return param1 != null ? param1 + param2 : param2;
        }
    };
    /** Function to sum integers in aggregators. */
    static final Func2<BigInteger, BigInteger, BigInteger> SUM_BIGINTEGER = new Func2<BigInteger, BigInteger, BigInteger>() {
        @Override
        public BigInteger call(BigInteger param1, BigInteger param2) {
            return param1 != null ? param1.add(param2) : param2;
        }
    };
    /** Function to sum integers in aggregators. */
    static final Func2<BigDecimal, BigDecimal, BigDecimal> SUM_BIGDECIMAL = new Func2<BigDecimal, BigDecimal, BigDecimal>() {
        @Override
        public BigDecimal call(BigDecimal param1, BigDecimal param2) {
            return param1 != null ? param1.add(param2) : param2;
        }
    };
    /**
     * Retuns a function that adds two BigDecimal numbers and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<BigDecimal, BigDecimal, BigDecimal> sumBigDecimal() {
        return SUM_BIGDECIMAL;
    }
    /**
     * Retuns a function that adds two BigInteger numbers and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<BigInteger, BigInteger, BigInteger> sumBigInteger() {
        return SUM_BIGINTEGER;
    }
    /**
     * Retuns a function that adds two Double number and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<Double, Double, Double> sumDouble() {
        return SUM_DOUBLE;
    }
    /**
     * Retuns a function that adds two Float number and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<Float, Float, Float> sumFloat() {
        return SUM_FLOAT;
    }
    /**
     * Retuns a function that adds two Integer number and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<Integer, Integer, Integer> sumInteger() {
        return SUM_INTEGER;
    }
    /**
     * Retuns a function that adds two Long number and
     * returns a new one.
     * <p>If the first parameter is null, it returns the second parameter.</p>
     * @return Function to sum integers in aggregators.
     */
    public static Func2<Long, Long, Long> sumLong() {
        return SUM_LONG;
    }
    /**
     * A list creator factory.
     * @param <T> the value type
     * @return a function which creates a new empty instance of the given concrete list implementation.
     */
    public static <T> Func0<ArrayList<T>> arrayListProvider() {
        return new Func0<ArrayList<T>>() {
            @Override
            public ArrayList<T> call() {
                return new ArrayList<T>();
            }
        };
    }
    /**
     * A list creator factory for Func1 that ignores the parameter.
     * @param <T> the value type
     * @param <U> the function parameter type, ignored
     * @return a function which creates a new empty instance of
     * the given concrete list implementation.
     */
    public static <T, U> Func1<U, ArrayList<T>> arrayListProvider1() {
        return new Func1<U, ArrayList<T>>() {
            @Override
            public ArrayList<T> call(U ignored) {
                return new ArrayList<T>();
            }
        };
    }
    /**
     * A list creator factory.
     * @param <T> the value type
     * @return a function which creates a new empty instance of the given concrete list implementation.
     */
    public static <T> Func0<LinkedList<T>> linkedListProvider() {
        return new Func0<LinkedList<T>>() {
            @Override
            public LinkedList<T> call() {
                return new LinkedList<T>();
            }
        };
    }
    /**
     * A map creator factory.
     * @param <K> the key type
     * @param <V> the value type
     * @return a function which creates a new empty instance of the given concrete map implementation.
     */
    public static <K, V> Func0<HashMap<K, V>> hashMapProvider() {
        return new Func0<HashMap<K, V>>() {
            @Override
            public HashMap<K, V> call() {
                return new HashMap<K, V>();
            }
        };
    }
    /**
     * A map creator factory.
     * @param <K> the key type
     * @param <V> the value type
     * @return a function which creates a new empty instance of the given concrete map implementation.
     */
    public static <K, V> Func0<TreeMap<K, V>> treeMapProvider() {
        return new Func0<TreeMap<K, V>>() {
            @Override
            public TreeMap<K, V> call() {
                return new TreeMap<K, V>();
            }
        };
    }
    /**
     * A map creator factory.
     * @param <K> the key type
     * @param <V> the value type
     * @param keyComparator the key comparator function
     * @return a function which creates a new empty instance of the given concrete map implementation.
     */
    public static <K, V> Func0<TreeMap<K, V>> treeMapProvider(final Comparator<? super K> keyComparator) {
        return new Func0<TreeMap<K, V>>() {
            @Override
            public TreeMap<K, V> call() {
                return new TreeMap<K, V>(keyComparator);
            }
        };
    }
    /**
     * A map creator factory.
     * @param <K> the key type
     * @param <V> the value type
     * @return a function which creates a new empty instance of the given concrete map implementation.
     */
    public static <K, V> Func0<LinkedHashMap<K, V>> linkedHashMapProvider() {
        return new Func0<LinkedHashMap<K, V>>() {
            @Override
            public LinkedHashMap<K, V> call() {
                return new LinkedHashMap<K, V>();
            }
        };
    }
    /**
     * A map creator factory.
     * @param <K> the key type
     * @param <V> the value type
     * @return a function which creates a new empty instance of the given concrete map implementation.
     */
    public static <K, V> Func0<ConcurrentHashMap<K, V>> concurrentHashMapProvider() {
        return new Func0<ConcurrentHashMap<K, V>>() {
            @Override
            public ConcurrentHashMap<K, V> call() {
                return new ConcurrentHashMap<K, V>();
            }
        };
    }
    /**
     * A set creation provider.
     * @param <T> the element type
     * @return the function which creates an empty instance of the set
     */
    public static <T> Func0<HashSet<T>> hashSetProvider() {
        return new Func0<HashSet<T>>() {
            @Override
            public HashSet<T> call() {
                return new HashSet<T>();
            }
        };
    }
    /**
     * A set creation provider.
     * @param <T> the element type
     * @return the function which creates an empty instance of the set
     */
    public static <T> Func0<TreeSet<T>> treeSetProvider() {
        return new Func0<TreeSet<T>>() {
            @Override
            public TreeSet<T> call() {
                return new TreeSet<T>();
            }
        };
    }
    /**
     * A set creation provider.
     * @param <T> the element type
     * @param elementComparator the custom element comparator
     * @return the function which creates an empty instance of the set
     */
    public static <T> Func0<TreeSet<T>> treeSetProvider(final Comparator<? super T> elementComparator) {
        return new Func0<TreeSet<T>>() {
            @Override
            public TreeSet<T> call() {
                return new TreeSet<T>(elementComparator);
            }
        };
    }
    /** A helper action with one parameter which does nothing. */
    private static final Action1<Void> NO_ACTION_1 = new Action1<Void>() {
        @Override
        public void call(Void value) {
            
        }
    };
    /** A helper action without parameters which does nothing. */
    private static final Action0 NO_ACTION_0 = new Action0() {
        @Override
        public void call() {
            
        }
    };
    /** Empty action. */
    private static final Action2<Void, Void> NO_ACTION_2 = new Action2<Void, Void>() {
        @Override
        public void call(Void t, Void u) { }
    };
    /**
     * @return returns an empty action which does nothing.
     */
    public static Action0 noAction0() {
        return NO_ACTION_0;
    }
    /**
     * Returns an action which does nothing with its parameter.
     * @param <T> the type of the parameter (irrelevant)
     * @return the action
     */
    @SuppressWarnings("unchecked")
    public static <T> Action1<T> noAction1() {
        return (Action1<T>)NO_ACTION_1;
    }
    /**
     * Returns an action which does nothing with its parameter.
     * @param <T> the type of the first parameter (irrelevant)
     * @param <U> the type of the second parameter (irrelevant)
     * @return the action
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Action2<T, U> noAction2() {
        return (Action2<T, U>)NO_ACTION_2;
    }
    /**
     * @return Returns a function that negates the incoming boolean value.
     */
    public static Func1<Boolean, Boolean> negate() {
        return new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean param1) {
                return param1 == Boolean.TRUE ? Boolean.FALSE : Boolean.TRUE;
            }
        };
    }
    
    /** Utility class. */
    private IxHelperFunctions() {
    }
}
