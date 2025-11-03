public class PhysicalMemory {
    private final int[] physicalMemory;
    private final int pageSize;

    public PhysicalMemory(int size, int pageSize) {
        this.physicalMemory = new int[size];
        this.pageSize = pageSize;

        for (int i = 0; i < size; i++) {
            this.physicalMemory[i] = i + 10;
        }
    }

    public int getAddressFrame(int physicalAddress) {
        if (physicalAddress >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Invalid physical address");
        }

        return physicalAddress / this.pageSize;
    }

    public int readByte(int physicalAddress) {
        if (physicalAddress >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Invalid physical address");
        }

        return this.physicalMemory[physicalAddress];
    }

    public void alocateMemory(int addressStart, int addressLimit) {
        if (addressStart >= this.physicalMemory.length || addressLimit >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Invalid physical address");
        }
        if (addressStart > addressLimit) {
            throw new IllegalArgumentException("Address Start must be bigger than AddressLimit");
        }

    }
}
