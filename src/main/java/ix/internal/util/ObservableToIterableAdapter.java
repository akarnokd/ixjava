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

import ix.CloseableIterable;
import ix.CloseableIterator;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base class to help receive values from an observable sequence
 * through customizable iterator and observer.
 * @param <T> the observed element type
 * @param <U> the iterated element type
 */
public abstract class ObservableToIterableAdapter<T, U>
implements CloseableIterable<U> {
    /** The observable instance. */
    protected final Observable<? extends T> observable;
    /**
     * Constructor, saves the source observable.
     * @param observable the source observable
     */
    public ObservableToIterableAdapter(Observable<? extends T> observable) {
        this.observable = observable;
    }
    @Override
    public CloseableIterator<U> iterator() {
        CompositeSubscription handle = new CompositeSubscription();
        ObserverToIteratorSink<T, U> it = run(handle);
        Subscription c = observable.subscribe(it);
        // this won't add C if the handle is already closed
        handle.add(c);
        return it;
    }
    /**
     * The factory method to return an iterator and hand over the close handle
     * to the original registration to the source.
     * @param handle the close handle
     * @return the closeable iterator
     */
    protected abstract ObserverToIteratorSink<T, U> run(Subscription handle);
}
