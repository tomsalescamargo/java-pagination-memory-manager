package memory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Armazena o mapeamento entre índices de páginas lógicas e números de quadros físicos de um processo.
 */
public class PagesTable {
    private final int pageCount;
    private final Map<Integer, Integer> pagesTable;

    public PagesTable(int pageCount) {
        if (pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be positive");
        }
        this.pageCount = pageCount;
        this.pagesTable = new LinkedHashMap<>(pageCount);
    }

    public void mapPageToFrame(int pageNumber, int frameNumber) {
        validatePageNumber(pageNumber);
        pagesTable.put(pageNumber, frameNumber);
    }

    public int getPageFrame(int pageNumber) {
        validatePageNumber(pageNumber);
        Integer frameNumber = pagesTable.get(pageNumber);
        if (frameNumber == null) {
            throw new IllegalStateException("Page " + pageNumber + " is not mapped to any frame");
        }
        return frameNumber;
    }

    public int size() {
        return pageCount;
    }

    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 0 || pageNumber >= pageCount) {
            throw new IllegalArgumentException("Invalid page number: " + pageNumber);
        }
    }
}
