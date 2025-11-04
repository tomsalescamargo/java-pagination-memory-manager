package memory;

import java.util.Arrays;

/**
 * Modela a memória física dividida em quadros. Expõe operações de leitura/escrita
 * utilizadas pelo gerenciador para copiar páginas de entrada e saída.
 */
public class PhysicalMemory {
    private final byte[] physicalMemory;
    private final int frameSize;

    /**
     * @param size      tamanho total da memória física em bytes
     * @param frameSize tamanho de cada quadro em bytes (precisa dividir o total)
     */
    public PhysicalMemory(int size, int frameSize) {
        if (size <= 0 || frameSize <= 0) {
            throw new IllegalArgumentException("Memory size and frame size must be positive");
        }
        if (size % frameSize != 0) {
            throw new IllegalArgumentException("Physical memory size must be a multiple of frame size");
        }

        this.physicalMemory = new byte[size];
        this.frameSize = frameSize;
    }

    public int getTotalSize() {
        return physicalMemory.length;
    }

    /**
     * @return quantidade de quadros disponíveis nesta memória física
     */
    public int getNumberOfFrames() {
        return physicalMemory.length / frameSize;
    }

    public int getFrameStartAddress(int frameNumber) {
        validateFrameNumber(frameNumber);
        return frameNumber * frameSize;
    }

    public void clearFrame(int frameNumber) {
        int start = getFrameStartAddress(frameNumber);
        Arrays.fill(physicalMemory, start, start + frameSize, (byte) 0);
    }

    /**
     * Copia os bytes de uma página lógica para o quadro informado. Os bytes restantes são preenchidos com zero.
     */
    public void writeFrame(int frameNumber, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Frame data cannot be null");
        }
        if (data.length > frameSize) {
            throw new IllegalArgumentException("Frame data larger than frame size");
        }

        int start = getFrameStartAddress(frameNumber);
        System.arraycopy(data, 0, physicalMemory, start, data.length);
        if (data.length < frameSize) {
            Arrays.fill(physicalMemory, start + data.length, start + frameSize, (byte) 0);
        }
    }

    public byte[] readFrame(int frameNumber) {
        int start = getFrameStartAddress(frameNumber);
        return Arrays.copyOfRange(physicalMemory, start, start + frameSize);
    }

    private void validateFrameNumber(int frameNumber) {
        if (frameNumber < 0 || frameNumber >= getNumberOfFrames()) {
            throw new IllegalArgumentException("Invalid frame number: " + frameNumber);
        }
    }
}
