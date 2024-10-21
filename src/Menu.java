import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final HuffmanEncoderChar huffmanEncoderChar;
    private final HuffmanEncoderWord huffmanEncoderWord;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.huffmanEncoderChar = new HuffmanEncoderChar();
        this.huffmanEncoderWord = new HuffmanEncoderWord();
    }

    /**
     * Descrição: Exibe o menu principal do programa e processa a entrada do usuário para escolher opções de compressão e descompressão de arquivos.
     * Pré-condições: A classe HuffmanEncoderChar e HuffmanEncoderWord devem estar corretamente inicializadas e funcionais.
     * Pós-condições: O programa executa a ação correspondente à opção escolhida pelo usuário.
     */
    public void displayMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\nMENU:");
            System.out.println("1. Comprimir arquivo texto (por caracter)");
            System.out.println("2. Descomprimir arquivo texto (por caracter)");
            System.out.println("3. Comprimir arquivo texto (por palavra)");
            System.out.println("4. Descomprimir arquivo texto (por palavra)");
            System.out.println("5. Sair\n");
            System.out.print("Selecione uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    compressFileByChar();
                    break;
                case 2:
                    decompressFileByChar();
                    break;
                case 3:
                    compressFileByWord();
                    break;
                case 4:
                    decompressFileByWord();
                    break;
                case 5:
                    System.out.println("Saindo...");
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    /**
     * Descrição: Lê o nome de um arquivo de texto e realiza a compressão utilizando codificação Huffman a nível de caracter, salvando o resultado em um arquivo binário.
     * Pré-condições: O arquivo de entrada deve ser um arquivo de texto (.txt) existente.
     * Pós-condições: O arquivo será comprimido e salvo como um arquivo binário com o sufixo '_compactado_caracter.bin'.
     */
    private void compressFileByChar() {
        System.out.print("\nDigite o nome do arquivo de entrada com extensão (.txt) para compressão por caracter: ");
        String inputFilePath = scanner.nextLine();
        String outputFilePath = inputFilePath.replace(".txt", "_compactado_caracter.bin");

        try {
            huffmanEncoderChar.compressFileToBinaryByChar(inputFilePath, outputFilePath);
            System.out.println("Arquivo comprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao comprimir arquivo: " + e.getMessage());
        }
    }

    /**
     * Descrição: Lê o nome de um arquivo binário comprimido e realiza a descompressão utilizando codificação Huffman a nível de caracter, salvando o resultado em um arquivo de texto.
     * Pré-condições: O arquivo de entrada deve ser um arquivo binário (.bin) existente, que tenha sido previamente comprimido.
     * Pós-condições: O arquivo será descomprimido e salvo como um arquivo de texto com o sufixo '_descompactado_caracter.txt'.
     */
    private void decompressFileByChar() {
        System.out.print("\nDigite o nome do arquivo com extensão (.bin) para descompressão por caracter: ");
        String compressedFilePath = scanner.nextLine();
        String outputFilePath = compressedFilePath.replace("_compactado_caracter.bin", "_descompactado_caracter.txt");

        try {
            huffmanEncoderChar.decompressFileFromBinaryByChar(compressedFilePath, outputFilePath);
            System.out.println("Arquivo descomprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir arquivo: " + e.getMessage());
        }
    }

    /**
     * Descrição: Lê o nome de um arquivo de texto e realiza a compressão utilizando codificação Huffman a nível de palavra, salvando o resultado em um arquivo binário.
     * Pré-condições: O arquivo de entrada deve ser um arquivo de texto (.txt) existente.
     * Pós-condições: O arquivo será comprimido e salvo como um arquivo binário com o sufixo '_compactado_palavra.bin'.
     */
    private void compressFileByWord() {
        System.out.print("\nDigite o nome do arquivo de entrada com extensão (.txt) para compressão por palavra: ");
        String inputFilePath = scanner.nextLine();
        String outputFilePath = inputFilePath.replace(".txt", "_compactado_palavra.bin");

        try {
            huffmanEncoderWord.compressFileToBinaryByWord(inputFilePath, outputFilePath);
            System.out.println("Arquivo comprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao comprimir arquivo: " + e.getMessage());
        }
    }

    /**
     * Descrição: Lê o nome de um arquivo binário comprimido e realiza a descompressão utilizando codificação Huffman a nível de palavra, salvando o resultado em um arquivo de texto.
     * Pré-condições: O arquivo de entrada deve ser um arquivo binário (.bin) existente, que tenha sido previamente comprimido.
     * Pós-condições: O arquivo será descomprimido e salvo como um arquivo de texto com o sufixo '_descompactado_palavra.txt'.
     */
    private void decompressFileByWord() {
        System.out.print("\nDigite o nome do arquivo com extensão (.bin) para descompressão por palavra: ");
        String compressedFilePath = scanner.nextLine();
        String outputFilePath = compressedFilePath.replace("_compactado_palavra.bin", "_descompactado_palavra.txt");

        try {
            huffmanEncoderWord.decompressFileFromBinaryByWord(compressedFilePath, outputFilePath);
            System.out.println("Arquivo descomprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir arquivo: " + e.getMessage());
        }
    }

}