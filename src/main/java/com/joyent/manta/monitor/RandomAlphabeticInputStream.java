/*
 * Copyright (c) 2018, Joyent, Inc. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.joyent.manta.monitor;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link InputStream} implementation that generates random data.
 */
public class RandomAlphabeticInputStream extends InputStream {
    /**
     * End of file magic number.
     */
    private static final int EOF = -1;

    /**
     * Maximum number of bytes to generate.
     */
    private final long maximumBytes;

    /**
     * Current generated byte count.
     */
    private AtomicLong count = new AtomicLong(0L);

    /**
     * Creates a new instance.
     * @param maximumBytes maximum number of random bytes in stream
     */
    public RandomAlphabeticInputStream(final long maximumBytes) {
        this.maximumBytes = maximumBytes;
    }

    @Override
    public int read() throws IOException {
        if (count.getAndIncrement() >= maximumBytes) {
            return EOF;
        }

        return RandomUtils.nextInt(0, Integer.MAX_VALUE);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) {
        if (count.get() >= maximumBytes) {
            return EOF;
        }

        final int bytesToRead;

        if (maximumBytes - count.get() >= len) {
            bytesToRead = len;
        } else {
            bytesToRead = (int)(maximumBytes - count.get());
        }

        count.addAndGet(bytesToRead);

        final byte[] randomBytes = RandomStringUtils.randomAlphabetic(bytesToRead)
                .getBytes(StandardCharsets.US_ASCII);

        System.arraycopy(randomBytes, 0, b, off, bytesToRead);

        return bytesToRead;
    }
}