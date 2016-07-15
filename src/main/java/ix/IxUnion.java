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

import java.util.*;

final class IxUnion<T> extends IxSource<T, T> {

    final Iterable<? extends T> other;

    public IxUnion(Iterable<T> source, Iterable<? extends T> other) {
        super(source);
        this.other = other;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new UnionIterator<T>(source.iterator(), other.iterator());
    }

    static final class UnionIterator<T> extends IxSourceIterator<T, T> {
        
        final Iterator<? extends T> other;

        final Set<T> set;
        
        boolean second;
        
        public UnionIterator(Iterator<T> it, Iterator<? extends T> other) {
            super(it);
            this.other = other;
            this.set = new HashSet<T>();
        }
        
        @Override
        protected boolean moveNext() {
            Set<T> set = this.set;
            for (;;) {
                if (second) {
                    Iterator<? extends T> other = this.other;
                    while (other.hasNext()) {
                        T v = other.next();
                        if (set.add(v)) {
                            value = v;
                            hasValue = true;
                            return true;
                        }
                    }
                    
                    done = true;
                    return false;
                }
                
                Iterator<T> it = this.it;
                while (it.hasNext()) {
                    T v = it.next();
                    if (set.add(v)) {
                        value = v;
                        hasValue = true;
                        return true;
                    }
                }
                
                second = true;
            }
        }
    }
}
