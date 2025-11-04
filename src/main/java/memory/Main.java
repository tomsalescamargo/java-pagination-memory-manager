package memory;

import java.util.Optional;
import java.util.Scanner;

/**
 * Ponto de entrada em linha de comando para o simulador de paginação.
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            MemoryManager memoryManager = configureMemoryManager(scanner);
            runMenu(scanner, memoryManager);
        }
    }

    private static MemoryManager configureMemoryManager(Scanner scanner) {
        System.out.println("=== Configuração do simulador ===");
        while (true) {
            int physicalSize = readPositiveInt(scanner, "Tamanho da memória física (bytes): ");
            int pageSize = readPositiveInt(scanner, "Tamanho da página/quadro (bytes): ");
            int maxProcessSize = readPositiveInt(scanner, "Tamanho máximo de processo (bytes): ");

            try {
                return new MemoryManager(physicalSize, pageSize, maxProcessSize);
            } catch (IllegalArgumentException ex) {
                System.out.println("Configuração inválida: " + ex.getMessage());
                System.out.println("Tente novamente.\n");
            }
        }
    }

    private static void runMenu(Scanner scanner, MemoryManager memoryManager) {
        boolean running = true;
        while (running) {
            printMenu();
            int option = readPositiveInt(scanner, "Selecione uma opção: ");

            switch (option) {
                case 1 -> handleVisualizarMemoria(memoryManager);
                case 2 -> handleCriarProcesso(scanner, memoryManager);
                case 3 -> handleVisualizarTabela(scanner, memoryManager);
                case 4 -> running = false;
                default -> System.out.println("Opção inválida. Escolha entre 1 e 4.");
            }
        }
        System.out.println("Simulador encerrado.");
    }

    private static void printMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1 - Visualizar memória física");
        System.out.println("2 - Criar processo");
        System.out.println("3 - Visualizar tabela de páginas de um processo");
        System.out.println("4 - Sair");
    }

    private static void handleVisualizarMemoria(MemoryManager memoryManager) {
        System.out.printf("Memória livre: %.2f%%%n", memoryManager.getFreeMemoryPercentage());
        PhysicalMemory physicalMemory = memoryManager.getPhysicalMemory();
        int totalFrames = physicalMemory.getNumberOfFrames();

        for (int frame = 0; frame < totalFrames; frame++) {
            int ownerPid = memoryManager.getFrameOwner(frame);
            String ownerLabel = ownerPid < 0 ? "livre" : "PID " + ownerPid;
            byte[] frameData = physicalMemory.readFrame(frame);
            System.out.printf("Quadro %d [%s]: %s%n", frame, ownerLabel, formatFrameData(frameData));
        }
    }

    private static void handleCriarProcesso(Scanner scanner, MemoryManager memoryManager) {
        int pid = readPositiveInt(scanner, "Informe o PID do processo: ");
        if (memoryManager.findProcess(pid).isPresent()) {
            System.out.println("Já existe um processo com esse PID.");
            return;
        }

        while (true) {
            int processSize = readPositiveInt(scanner, "Informe o tamanho do processo (bytes): ");
            if (processSize > memoryManager.getMaxProcessSize()) {
                System.out.printf("O tamanho excede o limite de %d bytes. Informe outro valor.%n",
                        memoryManager.getMaxProcessSize());
                continue;
            }

            try {
                Process process = memoryManager.createProcess(pid, processSize);
                System.out.printf("Processo %d criado com %d páginas.%n", pid, process.getPageCount());
            } catch (IllegalArgumentException ex) {
                System.out.println("Falha ao criar processo: " + ex.getMessage());
            } catch (IllegalStateException ex) {
                System.out.println("Memória insuficiente para alocar o processo.");
            }
            break;
        }
    }

    private static void handleVisualizarTabela(Scanner scanner, MemoryManager memoryManager) {
        int pid = readPositiveInt(scanner, "Informe o PID do processo: ");
        Optional<Process> optionalProcess = memoryManager.findProcess(pid);

        if (optionalProcess.isEmpty()) {
            System.out.println("Processo não encontrado.");
            return;
        }

        Process process = optionalProcess.get();
        System.out.printf("Processo %d - tamanho: %d bytes (%d páginas)%n",
                process.getPid(), process.getSizeInBytes(), process.getPageCount());

        PagesTable table = process.getPagesTable();
        for (int page = 0; page < table.size(); page++) {
            int frame = table.getPageFrame(page);
            System.out.printf("Página %d -> Quadro %d%n", page, frame);
        }
    }

    private static String formatFrameData(byte[] frameData) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < frameData.length; i++) {
            builder.append(String.format("%02X", frameData[i] & 0xFF));
            if (i < frameData.length - 1) {
                builder.append(' ');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("O valor deve ser positivo.");
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return value;
            }
            System.out.println("Entrada inválida. Informe um número inteiro.");
            scanner.nextLine(); // discard invalid input
        }
    }
}
