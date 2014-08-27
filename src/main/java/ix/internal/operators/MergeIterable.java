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

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Notification;
import rx.Scheduler;
import rx.functions.Action0;
import rx.internal.util.SubscriptionList;

public final class MergeIterable<T> implements Iterable<T> {
	private final Scheduler scheduler;
	/** The source sequences. */
	private final Iterable<? extends Iterable<? extends T>> sources;

	public MergeIterable(Scheduler scheduler,
			Iterable<? extends Iterable<? extends T>> sources) {
		this.scheduler = scheduler;
		this.sources = sources;
	}

	@Override
	public Iterator<T> iterator() {
	    final BlockingQueue<Notification<T>> queue = new LinkedBlockingQueue<Notification<T>>();
	    final AtomicInteger wip = new AtomicInteger(1);
	    final SubscriptionList handlers = new SubscriptionList();
	    final Scheduler.Worker worker = scheduler.createWorker();
	    handlers.add(worker);
	    for (final Iterable<? extends T> iter : sources) {
	        Action0 r = new Action0() {
	            @Override
	            public void call() {
	                try {
	                    final Iterator<? extends T> fiter = iter.iterator();
	                    try {
	                        while (fiter.hasNext()) {
	                            T t = fiter.next();
	                            if (!Thread.currentThread().isInterrupted()) {
	                                queue.add(Interactive.some(t));
	                            }
	                        }
	                    } finally {
	                        Interactive.unsubscribe(fiter);
	                    }
	                    if (wip.decrementAndGet() == 0) {
	                        if (!Thread.currentThread().isInterrupted()) {
	                            queue.add(Interactive.<T>none());
	                        }
	                    }
	                } catch (Throwable t) {
	                    queue.add(Interactive.<T>err(t));
	                }
	            }
	        };
	        wip.incrementAndGet();
	        handlers.add(worker.schedule(r));
	    }
	    if (wip.decrementAndGet() == 0) {
	        queue.add(Interactive.<T>none());
	    }
	    return new Iterator<T>() {
	        final SingleContainer<Notification<T>> peek = new SingleContainer<Notification<T>>();
	        /** Are we broken? */
	        boolean broken;
	        @Override
	        public boolean hasNext() {
	            if (!broken) {
	                if (peek.isEmpty()) {
	                    try {
	                        Notification<T> t = queue.take();
	                        if (t.isOnNext()) {
	                            peek.add(t);
	                        } else
	                            if (t.isOnError()) {
	                                peek.add(t);
	                                broken = true;
	                            }
	                    } catch (InterruptedException ex) {
	                        Thread.currentThread().interrupt();
	                        return false; // FIXME not sure about this
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                try {
	                    return Interactive.value(peek.take());
	                } catch (RuntimeException ex) {
	                	handlers.unsubscribe();
	                    throw ex;
	                }
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	        
	    };
	}
}