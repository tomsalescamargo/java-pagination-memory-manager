package memory;

/**
 * Descritor simples de um processo simulado, ligando sua memória lógica e tabela de páginas.
 */
public class Process {
    private final int pid;
    private final LogicalMemory logicalMemory;
    private final PagesTable pagesTable;

    public Process(int pid, LogicalMemory logicalMemory, PagesTable pagesTable) {
        this.pid = pid;
        this.logicalMemory = logicalMemory;
        this.pagesTable = pagesTable;
    }

    public int getPid() {
        return pid;
    }

    public int getSizeInBytes() {
        return logicalMemory.getSize();
    }

    public int getPageCount() {
        return logicalMemory.getNumberOfPages();
    }

    public LogicalMemory getLogicalMemory() {
        return logicalMemory;
    }

    public PagesTable getPagesTable() {
        return pagesTable;
    }
}
