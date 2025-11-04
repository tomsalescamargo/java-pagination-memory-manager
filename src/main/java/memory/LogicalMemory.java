package memory;

import java.util.Arrays;
import java.util.Random;

/**
 * Representa a memória lógica de um processo. O conteúdo é armazenado em bytes
 * e dividido em páginas de tamanho fixo, que posteriormente podem ser mapeadas
 * para quadros da memória física.
 */
public class LogicalMemory {
    private final byte[] logicalMemory;
    private final int pageSize;

    /**
     * Cria a memória lógica de um processo e a preenche com valores aleatórios.
     *
     * @param processLength tamanho do processo em bytes
     * @param pageSize      tamanho configurado da página (e do quadro)
     */
    public LogicalMemory(int processLength, int pageSize) {
        if (processLength <= 0) {
            throw new IllegalArgumentException("Process length must be positive");
        }
        this.logicalMemory = new byte[processLength];
        this.pageSize = pageSize;

        fillWithRandomData();
    }

    private void fillWithRandomData() {
        Random random = new Random();
        random.nextBytes(this.logicalMemory);
    }

    public int getSize() {
        return this.logicalMemory.length;
    }

    /**
     * Retorna o número de páginas lógicas necessárias para armazenar os bytes do processo.
     */
    public int getNumberOfPages() {
        return (this.logicalMemory.length + pageSize - 1) / this.pageSize;
    }

    /**
     * Retorna uma cópia dos bytes que pertencem à página lógica informada.
     */
    public byte[] readPage(int pageNumber) {
        if (pageNumber < 0 || pageNumber >= getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid logical page: " + pageNumber);
        }

        int start = pageNumber * pageSize;
        int endExclusive = Math.min(start + pageSize, logicalMemory.length);
        return Arrays.copyOfRange(logicalMemory, start, endExclusive);
    }

}
