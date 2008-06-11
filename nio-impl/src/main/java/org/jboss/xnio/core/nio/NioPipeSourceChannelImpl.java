package org.jboss.xnio.core.nio;

import java.nio.channels.Pipe;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.Collections;
import org.jboss.xnio.channels.StreamSourceChannel;
import org.jboss.xnio.channels.UnsupportedOptionException;
import org.jboss.xnio.channels.Configurable;
import org.jboss.xnio.IoHandler;
import org.jboss.xnio.spi.SpiUtils;
import org.jboss.xnio.log.Logger;

/**
 *
 */
public final class NioPipeSourceChannelImpl implements StreamSourceChannel {
    private static final Logger log = Logger.getLogger(NioPipeSourceChannelImpl.class);

    private final Pipe.SourceChannel channel;
    private final NioHandle handle;
    private final IoHandler<? super StreamSourceChannel> handler;
    private final AtomicBoolean callFlag = new AtomicBoolean(false);

    public NioPipeSourceChannelImpl(final Pipe.SourceChannel channel, final IoHandler<? super StreamSourceChannel> handler, final NioProvider nioProvider) throws IOException {
        this.channel = channel;
        this.handler = handler;
        handle = nioProvider.addReadHandler(channel, new Handler());
    }

    public int read(final ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    public long read(final ByteBuffer[] dsts) throws IOException {
        return channel.read(dsts);
    }

    public long read(final ByteBuffer[] dsts, final int offset, final int length) throws IOException {
        return channel.read(dsts, offset, length);
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() throws IOException {
        try {
            channel.close();
        } finally {
            handle.cancelKey();
            if (! callFlag.getAndSet(true)) {
                SpiUtils.<StreamSourceChannel>handleClosed(handler, this);
            }
        }
    }

    public void suspendReads() {
        try {
            handle.getSelectionKey().interestOps(0).selector().wakeup();
        } catch (CancelledKeyException ex) {
            // ignore
        }
    }

    public void resumeReads() {
        try {
            handle.getSelectionKey().interestOps(SelectionKey.OP_READ).selector().wakeup();
        } catch (CancelledKeyException ex) {
            // ignore
        }
    }

    public void shutdownReads() throws IOException {
        channel.close();
    }

    public Object getOption(final String name) throws UnsupportedOptionException, IOException {
        throw new UnsupportedOptionException("No options supported");
    }

    public Map<String, Class<?>> getOptions() {
        return Collections.emptyMap();
    }

    public Configurable setOption(final String name, final Object value) throws IllegalArgumentException, IOException {
        throw new UnsupportedOptionException("No options supported");
    }

    private final class Handler implements Runnable {
        public void run() {
            SpiUtils.<StreamSourceChannel>handleReadable(handler, NioPipeSourceChannelImpl.this); 
        }
    }
}
