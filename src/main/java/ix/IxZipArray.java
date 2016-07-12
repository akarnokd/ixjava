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

import rx.functions.FuncN;

final class IxZipArray<T, R> extends Ix<R> {

    final Iterable<? extends T>[] sources;
    
    final FuncN<R> zipper;
    
    public IxZipArray(Iterable<? extends T>[] sources, FuncN<R> zipper) {
        this.sources = sources;
        this.zipper = zipper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator() {
        
        Iterable<? extends T>[] src = sources;
        
        Iterator<T>[] itors = new Iterator[src.length];
        for (int i = 0; i < itors.length; i++) {
            itors[i] = (Iterator<T>)src[i].iterator();
        }
        
        return new ZipArrayIterator<T, R>(itors, zipper);
    }
    
    static final class ZipArrayIterator<T, R> extends IxBaseIterator<R> {

        final Iterator<T>[] sources;

        final FuncN<R> zipper;

        public ZipArrayIterator(Iterator<T>[] sources, FuncN<R> zipper) {
            this.sources = sources;
            this.zipper = zipper;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Iterator<T>[] itors = sources;
            int n = itors.length;
            T[] a = (T[])new Object[n];
            
            for (int i = 0; i < n; i++) {
                Iterator<T> it = itors[i];
                if (it.hasNext()) {
                    a[i] = it.next();
                } else {
                    done = true;
                    return false;
                }
            }
            
            value = zipper.call(a);
            hasValue = true;
            
            return true;
        }
        
    }

}
