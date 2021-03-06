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

package be.raildelays.batch.listener;

import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;

/**
 * Base abstract class to log items before and after process.
 *
 * @author Almex
 * @since 1.1
 * @see org.springframework.batch.core.ItemProcessListener
 */
public abstract class AbstractLogItemProcessorListener<I, O> {

    private static final String HEADER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";
    private static final String FOOTER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";
    protected Logger logger;

    public AbstractLogItemProcessorListener() {
        logger = LoggerFactory.getLogger("xXx", this.getClass(), '|');
    }

    @BeforeProcess
    public void beforeProcess(I item) {
        logger.info(HEADER);
        infoInput("beforeProcess", item);
        logger.info(FOOTER);
    }

    @AfterProcess
    public void afterProcess(I item, O result) {
        logger.info(HEADER);
        infoOutput("afterProcess", result);
        logger.info(FOOTER);
    }

    public abstract void infoInput(String message, I input);

    public abstract void infoOutput(String message, O output);
}
