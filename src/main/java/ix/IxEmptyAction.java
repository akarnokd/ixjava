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

enum IxEmptyAction implements IxConsumer<Object>, Runnable {
    INSTANCE;

    @SuppressWarnings("unchecked")
    public static <T> IxConsumer<T> instance1() {
        return (IxConsumer<T>)INSTANCE;
    }

    public static Runnable instance0() {
        return INSTANCE;
    }

    @Override
    public void accept(Object t) {
        // deliberately no op
    }

    @Override
    public void run() {
        // deliberately no op
    }

}
