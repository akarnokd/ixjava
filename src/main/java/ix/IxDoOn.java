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

import java.util.Iterator;

import rx.functions.*;

final class IxDoOn<T> extends IxSource<T, T> {

    final Action1<? super T> onNext;
    
    final Action0 onCompleted;
    
    public IxDoOn(Iterable<T> source, Action1<? super T> onNext, Action0 onCompleted) {
        super(source);
        this.onNext = onNext;
        this.onCompleted = onCompleted;
    }

    @Override
    public Iterator<T> iterator() {
        return new DoOnIterator<T>(source.iterator(), onNext, onCompleted);
    }

    static final class DoOnIterator<T> extends IxSourceIterator<T, T> {
        final Action1<? super T> onNext;
        
        final Action0 onCompleted;

        public DoOnIterator(Iterator<T> it, Action1<? super T> onNext, Action0 onCompleted) {
            super(it);
            this.onNext = onNext;
            this.onCompleted = onCompleted;
        }

        @Override
        protected boolean moveNext() {
            if (it.hasNext()) {
                T v = it.next();
                value = v;
                hasValue = true;
                onNext.call(v);
                return true;
            }
            onCompleted.call();
            done = true;
            return false;
        }
    }
}
