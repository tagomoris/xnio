/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.xnio.XnioIoThread;

/**
 * An abstract synchronized source conduit.  All conduit operations are wrapped in synchronization blocks for simplified
 * thread safety.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractSynchronizedSourceConduit<D extends SourceConduit> extends AbstractSynchronizedConduit<D> implements SourceConduit {

    /**
     * Construct a new instance.  A new lock object is created.
     *
     * @param next the next conduit in the chain
     */
    protected AbstractSynchronizedSourceConduit(final D next) {
        super(next);
    }

    /**
     * Construct a new instance.
     *
     * @param next the next conduit in the chain
     * @param lock the lock object to use
     */
    protected AbstractSynchronizedSourceConduit(final D next, final Object lock) {
        super(next, lock);
    }

    public void terminateReads() throws IOException {
        synchronized (lock) {
            next.terminateReads();
        }
    }

    public boolean isReadShutdown() {
        synchronized (lock) {
            return next.isReadShutdown();
        }
    }

    public void resumeReads() {
        synchronized (lock) {
            next.resumeReads();
        }
    }

    public void suspendReads() {
        synchronized (lock) {
            next.suspendReads();
        }
    }

    public void wakeupReads() {
        synchronized (lock) {
            next.wakeupReads();
        }
    }

    public boolean isReadResumed() {
        synchronized (lock) {
            return next.isReadResumed();
        }
    }

    public void awaitReadable() throws IOException {
        synchronized (lock) {
            next.awaitReadable();
        }
    }

    public void awaitReadable(final long time, final TimeUnit timeUnit) throws IOException {
        synchronized (lock) {
            next.awaitReadable(time, timeUnit);
        }
    }

    public XnioIoThread getReadThread() {
        return next.getReadThread();
    }

    public void setReadReadyHandler(final ReadReadyHandler handler) {
        synchronized (lock) {
            next.setReadReadyHandler(handler);
        }
    }
}
