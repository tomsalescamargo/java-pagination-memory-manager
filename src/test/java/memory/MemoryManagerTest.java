package memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemoryManagerTest {

    @Test
    void deveCriarProcessoEMontarTabelaDePaginas() {
        MemoryManager manager = new MemoryManager(64, 16, 64);
        Process process = manager.createProcess(1, 30);

        assertEquals(1, process.getPid());
        assertEquals(30, process.getSizeInBytes());
        assertEquals(2, process.getPageCount());

        PagesTable pagesTable = process.getPagesTable();
        for (int page = 0; page < process.getPageCount(); page++) {
            int frame = pagesTable.getPageFrame(page);
            byte[] expectedPage = process.getLogicalMemory().readPage(page);
            byte[] storedFrame = manager.getPhysicalMemory().readFrame(frame);
            assertArrayEquals(expectedPage, slice(storedFrame, expectedPage.length));
            assertArrayEquals(new byte[storedFrame.length - expectedPage.length],
                    slice(storedFrame, expectedPage.length, storedFrame.length));
            assertEquals(1, manager.getFrameOwner(frame));
        }
    }

    @Test
    void deveFalharQuandoNaoHouverQuadrosSuficientes() {
        MemoryManager manager = new MemoryManager(32, 16, 32);

        manager.createProcess(1, 32); // consome os dois quadros disponíveis
        assertThrows(IllegalStateException.class, () -> manager.createProcess(2, 16));
    }

    @Test
    void naoDevePermitirPidDuplicado() {
        MemoryManager manager = new MemoryManager(64, 16, 64);
        manager.createProcess(10, 16);

        assertThrows(IllegalArgumentException.class, () -> manager.createProcess(10, 16));
    }

    @Test
    void liberaQuadrosSeFalharDuranteAlocacao() {
        MemoryManager manager = new MemoryManager(32, 16, 32);

        assertThrows(IllegalArgumentException.class, () -> manager.createProcess(1, -10));
        assertEquals(100.0, manager.getFreeMemoryPercentage());
        int totalFrames = manager.getPhysicalMemory().getNumberOfFrames();
        for (int frame = 0; frame < totalFrames; frame++) {
            assertEquals(-1, manager.getFrameOwner(frame));
        }
    }

    @Test
    void percentualDeMemoriaLivreAtualizaAposCriacao() {
        MemoryManager manager = new MemoryManager(64, 16, 64);
        assertEquals(100.0, manager.getFreeMemoryPercentage());

        manager.createProcess(1, 16); // consome 1 quadro de 4

        assertEquals(75.0, manager.getFreeMemoryPercentage());
    }
    private byte[] slice(byte[] source, int length) {
        return slice(source, 0, length);
    }

    private byte[] slice(byte[] source, int start, int end) {
        byte[] result = new byte[end - start];
        System.arraycopy(source, start, result, 0, result.length);
        return result;
    }
}
