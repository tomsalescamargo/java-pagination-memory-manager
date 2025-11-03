public class LogicalMemory {
    private final int[] logicalMemory;
    private final int pageSize;

    public LogicalMemory(int processLength, int pageSize) {
        this.logicalMemory = new int[processLength];
        this.pageSize = pageSize;

        for (int i = 0; i < processLength; i++) {
            this.logicalMemory[i] = i;
        }
    }

    public int getNumberOfPages() {
        return (this.logicalMemory.length + pageSize - 1) / this.pageSize;
    }

    public int getAddressPage(int logicalAddress) {
        if (logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Invalid logical address");
        }

        return logicalAddress / this.pageSize;
    }

    public int getAddressOffset(int logicalAddress) {
        if (logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Invalid logical address");
        }

        return logicalAddress % this.pageSize;
    }

    public int readByte(int logicalAddress) {
        if (logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Invalid logical address");
        }

        return this.logicalMemory[logicalAddress];
    }
 }
