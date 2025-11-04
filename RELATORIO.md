# Relatório do Simulador de Paginação
Estéfano Tuyama, Gean Pereira e Tom Sales

## Principais Componentes
- `memory.Main`  
  Interface de linha de comando que coleta a configuração inicial, apresenta o menu (visualizar memória, criar processo, consultar tabela) e coordena as ações junto ao `MemoryManager`.

- `memory.MemoryManager`  
  Fachada do sistema. Valida a configuração, controla quadros livres por meio de uma fila (`ArrayDeque`), cria processos e mantém o mapeamento de qual PID utiliza cada quadro (`frameOwners`). Expõe estatísticas e acessos para a UI e para os testes.

- `memory.PhysicalMemory`  
  Vetor de bytes que representa a RAM física. Implementa operações de leitura e escrita por quadro, sempre garantindo alinhamento com o tamanho do quadro. Zera quadros liberados.

- `memory.LogicalMemory`  
  Memória lógica de um processo, também em bytes. Divide o conteúdo em páginas, gera dados aleatórios e fornece utilitários para leitura por página.

- `memory.PagesTable`  
  Tabela de páginas simples (página → quadro). Usada tanto pelo `MemoryManager` quanto para exibição ao usuário.

- `memory.Process`  
  Estrutura imutável que liga PID, memória lógica e tabela de páginas. Serve como descritor para consultas posteriores.

## Compilação e Execução
### Requisitos
- Java >= 21
- Maven >= 3.9

1. **Compilar / gerar JAR**  
   ```bash
   mvn clean package
   ```
   O artefato é gerado em `target/java-pagination-memory-manager-1.0.0-SNAPSHOT.jar`.

2. **Executar o simulador**  
   ```bash
   java -jar target/java-pagination-memory-manager-1.0.0-SNAPSHOT.jar
   ```
   O programa inicia solicitando as configurações e abre o menu interativo.

## Casos de Teste
Os testes automatizados ficam em `src/test/java/memory/MemoryManagerTest.java` e foram executados com:

```bash
mvn clean test
```

### Cenários Cobertos
1. **Criação de processo e montagem da tabela de páginas**  
   Confere PID, tamanho, número de páginas, integridade dos dados gravados na memória física e registro do proprietário do quadro.

2. **Falta de quadros suficientes**  
   Garante que uma nova alocação dispara `IllegalStateException` quando não há quadros livres.

3. **PID duplicado**  
   Verifica que tentar reutilizar o mesmo PID lança `IllegalArgumentException`.

4. **Rollback após falha na alocação**  
   Certifica que, ao ocorrer uma exceção durante a criação (ex.: tamanho inválido), todos os quadros continuam livres e o percentual de memória retorna a 100%.

5. **Percentual de memória livre**  
   Valida o cálculo de memória livre antes e depois de alocar um processo.