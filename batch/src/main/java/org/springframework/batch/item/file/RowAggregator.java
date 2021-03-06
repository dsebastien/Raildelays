package org.springframework.batch.item.file;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author Almex
 */
public interface RowAggregator<T> {
    /**
     * Create a {@link Row} from the value provided.
     *
     * @param item values to be converted
     * @return previous row content mapped by an {@link org.springframework.batch.item.file.RowMapper}
     */
    T aggregate(T item, Workbook workbook, int sheetIndex, int rowIndex) throws Exception;
}
