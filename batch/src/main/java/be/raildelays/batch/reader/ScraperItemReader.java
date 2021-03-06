/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.reader;

import be.raildelays.httpclient.Request;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.batch.item.ItemReader} capable of retrieving data from a website.
 * This implementation use a {@link org.springframework.retry.RetryPolicy} to allow to configure upon which
 * {@link java.lang.Exception} you want to try multiple attempt to read. For instance, {@code IOException} would be a
 * good choice as it denote a problem during the HTTP connection (maybe Wi-Fi is off, you network is not fully
 * startup yet). Then aside to the {@link org.springframework.retry.RetryPolicy} you must also configure the
 * {@link org.springframework.retry.backoff.BackOffPolicy} in order to define what to do between two attempts (e.g.:
 * wait 5 seconds).
 * <p>
 * To retrieve data from a website, this reader need to have a {@code request}, a {@code streamer} and a {@code parser}.
 * </p>
 * <p>
 * To be respectful of the website we attempt to read, this reader also wait between 1 and 5 seconds between two
 * reads. Then we avoid any Deny Of Service.
 * </p>
 */
public class ScraperItemReader<T, R extends Request> implements ItemReader<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperItemReader.class);

    private RequestStreamer<R> streamer;

    private StreamParser<T, R> parser;

    private R request;

    private RetryPolicy retryPolicy;

    private BackOffPolicy backOffPolicy;

    private RetryTemplate retryTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Validate all job parameters
        Assert.notNull(parser, "The 'parser' property must have a value");
        Assert.notNull(request, "The 'request' property must have a value");
        Assert.notNull(streamer, "The 'streamer' property must have a value");
        Assert.notNull(retryPolicy, "The 'retryPolicy' property must have a value");
        Assert.notNull(backOffPolicy, "The 'backOffPolicy' property must have a value");
        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public T read() throws Exception {

        return retryTemplate.execute(context -> {
            T result = null;

            if (request != null) {
                LOGGER.debug("Requesting Railtime for {}", request);

                waitRandomly();

                result = parser.parse(streamer.stream(request));

                request = null; // We consume read, then next time we will return null if no new request is provided
            }

            return result;
        });
    }

    /**
     * Wait a certain period of time before processing. It's more respectful for
     * grabber to do so.
     * <p>
     * Wait between 1 and 5 seconds.
     *
     * @throws InterruptedException
     */
    private void waitRandomly() throws InterruptedException {
        long waitTime = 1000 + Math.round(5000L * Math.random());

        LOGGER.debug("Waiting " + (waitTime / 1000) + " seconds...");

        Thread.sleep(waitTime);
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setBackOffPolicy(BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
    }

    public void setStreamer(RequestStreamer<R> streamer) {
        this.streamer = streamer;
    }

    public void setParser(StreamParser<T, R> parser) {
        this.parser = parser;
    }

    public void setRequest(R request) {
        this.request = request;
    }
}
