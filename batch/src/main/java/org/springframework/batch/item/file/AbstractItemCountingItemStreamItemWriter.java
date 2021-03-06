package org.springframework.batch.item.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.IndexedItem;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Almex
 * @see #setCurrentItemIndex(int)
 * @see #setMaxItemCount(int)
 * @implSpec This implementation is not thread-safe
 * @since 1.1
 */
public abstract class AbstractItemCountingItemStreamItemWriter<T> extends AbstractItemStreamItemWriter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemCountingItemStreamItemWriter.class);
    private static final String WRITE_COUNT = "write.count";
    private static final String WRITE_COUNT_MAX = "write.count.max";
    private boolean saveState = true;
    private int currentItemIndex = 0;
    private int currentItemCount = 0;
    private int maxItemCount = Integer.MAX_VALUE;
    private boolean useItemIndex = true;

    /**
     * Write item to a certain index.
     *
     * @return <code>true</code> if it's a new item, <code>false</code> if it has replaced something
     * @throws Exception
     */
    protected abstract boolean doWrite(T item) throws Exception;

    /**
     * Open resources necessary to start writing output.
     */
    protected abstract void doOpen() throws ItemStreamException;

    /**
     * Close the resources opened in {@link #doOpen()}.
     */
    protected abstract void doClose() throws ItemStreamException;

    /**
     * Move to the given item index.
     */
    protected void jumpToItem(int itemIndex) throws ItemStreamException {
        this.currentItemIndex = itemIndex;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            if (item instanceof IndexedItem && useItemIndex) {
                Long index = ((IndexedItem) item).getIndex();
                if (index != null) {
                    jumpToItem(index.intValue());
                }
            }

            if (currentItemCount < maxItemCount) {
                if (doWrite(item)) {
                    currentItemCount++;
                }
                currentItemIndex++;
            }

            LOGGER.trace("[currentItemCount={}, currentItemIndex={}]", currentItemCount, currentItemIndex);
        }

        LOGGER.debug("Written {} items", items);
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();
        currentItemCount = 0;
        currentItemIndex = 0;
        doClose();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);

        doOpen();

        if (!isSaveState()) {
            return;
        }

        if (executionContext.containsKey(getExecutionContextKey(WRITE_COUNT_MAX))) {
            maxItemCount = executionContext.getInt(getExecutionContextKey(WRITE_COUNT_MAX));
        }

        if (executionContext.containsKey(getExecutionContextKey(WRITE_COUNT))) {
            int itemCount = executionContext.getInt(getExecutionContextKey(WRITE_COUNT));

            currentItemCount = itemCount;

            if (itemCount < maxItemCount) {
                try {
                    jumpToItem(itemCount);
                } catch (Exception e) {
                    throw new ItemStreamException("Could not move to stored position on restart", e);
                }
            }

        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        if (saveState) {
            Assert.notNull(executionContext, "ExecutionContext must not be null");
            executionContext.putInt(getExecutionContextKey(WRITE_COUNT), currentItemCount);
            if (maxItemCount < Integer.MAX_VALUE) {
                executionContext.putInt(getExecutionContextKey(WRITE_COUNT_MAX), maxItemCount);
            }
        }

    }

    /**
     * The flag that determines whether to save internal state for restarts.
     *
     * @return true if the flag was set
     */
    public boolean isSaveState() {
        return saveState;
    }

    /**
     * Set the flag that determines whether to save internal data for
     * {@link ExecutionContext}. Only switch this to false if you don't want to
     * save any state from this stream, and you don't need it to be restartable.
     * Always set it to false if the reader is being used in a concurrent
     * environment.
     *
     * @param saveState flag value (default true).
     */
    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }

    protected int getCurrentItemIndex() {
        return currentItemIndex;
    }

    /**
     * The index of the item to start writing to. If the
     * {@link org.springframework.batch.item.ExecutionContext} contains a key <code>[name].write.count</code>
     * (where <code>[name]</code> is the name of this component) the value from
     * the {@link org.springframework.batch.item.ExecutionContext} will be used in preference.
     *
     * @param itemIndex the value of the current item index
     * @see #setName(String)
     */
    public void setCurrentItemIndex(int itemIndex) {
        this.currentItemIndex = itemIndex;
    }

    /**
     * The maximum number of the items to be write. If the
     * {@link org.springframework.batch.item.ExecutionContext} contains a key
     * <code>[name].write.count.max</code> (where <code>[name]</code> is the name
     * of this component) the value from the {@link org.springframework.batch.item.ExecutionContext} will be
     * used in preference.
     *
     * @param count the value of the maximum item count
     * @see #setName(String)
     */
    public void setMaxItemCount(int count) {
        this.maxItemCount = count;
    }

    /**
     * In case we have items implementing {@link IndexedItem} you can decide if its
     * index should be used or not during the writing process.
     * <p>
     * It's a common usage to set it to <code>false</code> when you have an
     * {@link org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader} as input and filter items
     * in between then you don't want to skip a row in the output file (filling the gap left by the filtered item).
     * </p>
     *
     * @param useItemIndex <code>true</code> if you want to use it, <code>false</code> otherwise.
     */
    public void setUseItemIndex(boolean useItemIndex) {
        this.useItemIndex = useItemIndex;
    }
}
