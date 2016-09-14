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

final class IxReplay<T> extends IxSource<T, T> {

    List<T> list;

    Iterator<T> it;

    IxReplay(Iterable<T> source) {
        super(source);
    }

    @Override
    public Iterator<T> iterator() {
        if (it == null) {
            it = source.iterator();
        }
        return new ReplayIterator<T>(this);
    }

    boolean moveNext() {
        if (!it.hasNext()) {
            return false;
        }

        List<T> list = this.list;
        if (list == null) {
            list = new ArrayList<T>();
            this.list = list;
        }

        list.add(it.next());
        return true;
    }

    static final class ReplayIterator<T> extends IxBaseIterator<T> {

        final IxReplay<T> parent;

        int index;

        ReplayIterator(IxReplay<T> parent) {
            this.parent = parent;
        }

        @Override
        protected boolean moveNext() {
            int i = index;
            List<T> list = parent.list;

            if (list == null || i == list.size()) {
                if (!parent.moveNext()) {
                    done = true;
                    return false;
                }
                if (list == null) {
                    list = parent.list;
                }
            }
            index = i + 1;
            value = list.get(i);
            hasValue = true;
            return true;
        }
    }
}
