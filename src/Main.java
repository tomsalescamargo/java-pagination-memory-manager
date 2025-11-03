public class Main {
    public static void main(String[] args) {
        // Process creation
        int pageSize = 4;
        int pid = 1;
        int processLength = 100;
        LogicalMemory logicalMemory = new LogicalMemory(processLength, pageSize);

        int numberOfPages = logicalMemory.getNumberOfPages();
        PagesTable pagesTable = new PagesTable(numberOfPages);

        // Entry point - logical address
        int logicalAddress = 11;
        int logicalAddressPage = logicalMemory.getAddressPage(logicalAddress);
        int logicalAddressOffset = logicalMemory.getAddressOffset(logicalAddress);
        int physicalMemoryFrame = pagesTable.getPageFrame(logicalAddressPage);


        int physicalMemorySize = 1000;
        PhysicalMemory physicalMemory = new PhysicalMemory(physicalMemorySize, pageSize);

        // TODO: create a method that finds physical memory addressStart by the frame given

        // IMPLEMENT method alocateMemory, considering addressStart and offset
        physicalMemory.alocateMemory(1, 10);

    }
}