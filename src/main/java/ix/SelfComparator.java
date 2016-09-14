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

import java.util.Comparator;

@SuppressWarnings("rawtypes")
enum SelfComparator implements Comparator<Comparable> {
    INSTANCE
    ;

    @SuppressWarnings("unchecked")
    @Override
    public int compare(Comparable o1, Comparable o2) {
        return o1.compareTo(o2);
    }
}
