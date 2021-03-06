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

package org.springframework.batch.item.resource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;

import java.util.List;

/**
 * A resource locator is way to define a resource on 3 of {@code ItemStream} operations.
 * Then you can dynamically create a path based on something in the {@link ResourceContext} or
 * based on items in case of the {@link #onWrite(Object, ResourceContext)} method.
 *
 * @param <T> type of the data to write
 * @author Almex
 * @see org.springframework.batch.item.ItemStream
 * @see ResourceLocatorItemStreamWriter
 * @see ResourceLocatorItemStreamReader
 * @see AbstractResourceLocatorItemStream
 * @since 2.0
 */
public interface ResourceLocator<T> {

    /**
     * Event triggered on {@link AbstractResourceLocatorItemStream#open(ExecutionContext)} method.
     *
     * @param context to communicate changes on the resource you attempt to build
     * @throws ItemStreamException in case of any exception
     */
    void onOpen(ResourceContext context) throws ItemStreamException;

    /**
     * Event triggered on {@link ResourceLocatorItemStreamWriter#write(List)} method.
     *
     * @param item   data to write
     * @param context to communicate changes on the resource you attempt to build
     * @throws Exception in case of any exception
     */
    void onWrite(T item, ResourceContext context) throws Exception;

    /**
     * Event triggered on {@link ResourceLocatorItemStreamReader#read()} method.
     *
     * @implNote an implementation of this method should if the item is null to trigger the move to the next resource
     * @param item    read data
     * @param context to communicate changes on the resource you attempt to build
     * @throws Exception in case of any exception
     */
    void onRead(T item, ResourceContext context) throws Exception;

}
