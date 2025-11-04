package memory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Fachada responsável por orquestrar memória lógica, tabelas de páginas e memória física.
 * Controla quadros livres, cria processos e fornece informações para a interface de linha de comando.
 */
public class MemoryManager {
    private final PhysicalMemory physicalMemory;
    private final int pageSize;
    private final int maxProcessSize;
    private final Map<Integer, Process> processes = new LinkedHashMap<>();
    private final ArrayDeque<Integer> freeFrames = new ArrayDeque<>();
    private final int[] frameOwners;

    public MemoryManager(int physicalMemorySize, int pageSize, int maxProcessSize) {
        if (!isPowerOfTwo(physicalMemorySize) || !isPowerOfTwo(pageSize)) {
            throw new IllegalArgumentException("Memory size and page size must be powers of two");
        }
        if (maxProcessSize <= 0) {
            throw new IllegalArgumentException("Maximum process size must be positive");
        }
        if (maxProcessSize > physicalMemorySize) {
            throw new IllegalArgumentException("Maximum process size cannot exceed physical memory size");
        }

        this.physicalMemory = new PhysicalMemory(physicalMemorySize, pageSize);
        this.pageSize = pageSize;
        this.maxProcessSize = maxProcessSize;
        this.frameOwners = new int[this.physicalMemory.getNumberOfFrames()];
        Arrays.fill(this.frameOwners, -1);
        initializeFreeFrames();
    }

    private void initializeFreeFrames() {
        for (int i = 0; i < physicalMemory.getNumberOfFrames(); i++) {
            freeFrames.addLast(i);
        }
    }

    /**
     * Cria e registra um novo processo, alocando quadros para todas as páginas lógicas.
     *
     * @throws IllegalArgumentException quando o PID já existe ou o tamanho solicitado viola os limites
     * @throws IllegalStateException    quando não há quadros suficientes disponíveis
     */
    public Process createProcess(int pid, int processSize) {
        ensurePidAvailable(pid);
        ensureValidProcessSize(processSize);

        LogicalMemory logicalMemory = new LogicalMemory(processSize, pageSize);
        int pagesNeeded = logicalMemory.getNumberOfPages();
        ensureFramesAvailable(pagesNeeded);

        PagesTable pagesTable = new PagesTable(pagesNeeded);
        List<Integer> allocatedFrames = new ArrayList<>(pagesNeeded);
        try {
            for (int page = 0; page < pagesNeeded; page++) {
                int frame = allocateFrame(pid, logicalMemory, page);
                pagesTable.mapPageToFrame(page, frame);
                allocatedFrames.add(frame);
            }
        } catch (RuntimeException allocationError) {
            allocatedFrames.forEach(this::releaseFrame);
            throw allocationError;
        }

        Process process = new Process(pid, logicalMemory, pagesTable);
        processes.put(pid, process);
        return process;
    }

    private int allocateFrame(int pid, LogicalMemory logicalMemory, int page) {
        int frame = freeFrames.removeFirst();
        frameOwners[frame] = pid;
        byte[] pageData = logicalMemory.readPage(page);
        physicalMemory.writeFrame(frame, pageData);
        return frame;
    }

    private void ensurePidAvailable(int pid) {
        if (processes.containsKey(pid)) {
            throw new IllegalArgumentException("Process with PID " + pid + " already exists");
        }
    }

    private void ensureValidProcessSize(int processSize) {
        if (processSize <= 0) {
            throw new IllegalArgumentException("Process size must be positive");
        }
        if (processSize > maxProcessSize) {
            throw new IllegalArgumentException("Process size exceeds maximum allowed size");
        }
    }

    private void ensureFramesAvailable(int pagesNeeded) {
        if (freeFrames.size() < pagesNeeded) {
            throw new IllegalStateException("Not enough physical memory frames available");
        }
    }

    /**
     * Localiza um processo pelo PID, caso tenha sido criado anteriormente.
     */
    public Optional<Process> findProcess(int pid) {
        return Optional.ofNullable(processes.get(pid));
    }

    public Collection<Process> listProcesses() {
        return Collections.unmodifiableCollection(processes.values());
    }

    public int getFrameOwner(int frameNumber) {
        validateFrameNumber(frameNumber);
        return frameOwners[frameNumber];
    }

    /**
     * Calcula o percentual de bytes livres na memória física.
     */
    public double getFreeMemoryPercentage() {
        if (physicalMemory.getTotalSize() == 0) {
            return 0.0;
        }
        int freeBytes = freeFrames.size() * pageSize;
        return (freeBytes * 100.0) / physicalMemory.getTotalSize();
    }

    public PhysicalMemory getPhysicalMemory() {
        return physicalMemory;
    }

    public int getMaxProcessSize() {
        return maxProcessSize;
    }

    private boolean isPowerOfTwo(int value) {
        return (value > 0) && ((value & (value - 1)) == 0);
    }

    private void validateFrameNumber(int frameNumber) {
        if (frameNumber < 0 || frameNumber >= frameOwners.length) {
            throw new IllegalArgumentException("Invalid frame number: " + frameNumber);
        }
    }

    private void releaseFrame(int frameNumber) {
        frameOwners[frameNumber] = -1;
        physicalMemory.clearFrame(frameNumber);
        freeFrames.addLast(frameNumber);
    }
}
