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
package ix.internal.operators;

import ix.internal.util.ObservableToIterableAdapter;
import ix.internal.util.ObserverToIteratorSink;
import ix.internal.util.SingleOption;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import rx.Notification;
import rx.Observable;
import rx.Subscription;

/**
 * Convert the given observable instance into a classical iterable instance.
 * <p>The resulting iterable does not support the {@code remove()} method.</p>
 * @param <T> the element type to iterate
 */
public final class ToIterable<T> extends ObservableToIterableAdapter<T, T> {
	/**
	 * Constructor.
	 * @param observable the observable to convert
	 */
	public ToIterable(Observable<? extends T> observable) {
		super(observable);
	}

	@Override
	protected ObserverToIteratorSink<T, T> run(Subscription handle) {
		return new ObserverToIteratorSink<T, T>(handle) {
			/** The queue. */
			final BlockingQueue<Notification<T>> queue = new LinkedBlockingQueue<Notification<T>>();
			@Override
			public void onNext(T value) {
				queue.add(Notification.createOnNext(value));
			}

			@Override
			public void onError(Throwable ex) {
				done();
				
				queue.add(Notification.<T>createOnError(ex));
			}

			@Override
			public void onCompleted() {
				done();

				queue.add(Notification.<T>createOnCompleted());
			}

			@Override
			public boolean tryNext(SingleOption<? super T> out) {
				try {
					Notification<T> o = queue.take();
					
					if (o.isOnCompleted()) {
						return false;
					}
					out.addOption(o);
				} catch (InterruptedException ex) {
					out.addError(ex);
				}
				return true;
			}
			
		};
	}
}
