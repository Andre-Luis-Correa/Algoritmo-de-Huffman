import java.io.IOException;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final HuffmanEncoderBinary huffmanEncoder;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.huffmanEncoder = new HuffmanEncoderBinary();
    }

    public void displayMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\nMENU");
            System.out.println("1. Comprimir arquivo texto (por caracter)");
            System.out.println("2. Descomprimir arquivo texto (por caracter)");
            System.out.println("3. Contar o número de linhas e caracteres de um arquivo");
            System.out.println("4. Sair");
            System.out.print("Selecione uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            switch (choice) {
                case 1:
                    compressFile();
                    break;
                case 2:
                    decompressFile();
                    break;
                case 3:
                    countLinesAndCharacters();
                    break;
                case 4:
                    System.out.println("Saindo...");
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private void compressFile() {
        System.out.print("\nDigite o nome do arquivo de entrada com extensão (.txt): ");
        String inputFilePath = scanner.nextLine();

        if (!inputFilePath.endsWith(".txt")) {
            System.out.println("Formato de arquivo inválido. Por favor, insira um arquivo com extensão .txt.");
            return;
        }

        String outputFilePath = inputFilePath.replace(".txt", "_compactado.bin");

        try {
            huffmanEncoder.compressFileToBinary(inputFilePath, outputFilePath);
            System.out.println("Arquivo comprimido com sucesso. Salvo como '" + outputFilePath + "' na raiz do projeto.");
        } catch (IOException e) {
            System.err.println("Erro ao comprimir arquivo: " + e.getMessage());
        }
    }

    private void decompressFile() {
        System.out.print("Digite o nome do arquivo binário (.bin) para descomprimir: ");
        String compressedFilePath = scanner.nextLine();

        if (!compressedFilePath.endsWith("_compactado.bin")) {
            System.out.println("Formato de arquivo inválido. Por favor, insira um arquivo com '_compactado.bin'.");
            return;
        }

        String decompressedFilePath = compressedFilePath.replace("_compactado.bin", "_descompactado.txt");

        try {
            huffmanEncoder.decompressFileFromBinary(compressedFilePath, decompressedFilePath);
            System.out.println("Arquivo descomprimido com sucesso. Salvo como '" + decompressedFilePath + "' na raiz do projeto.");
        } catch (IOException e) {
            System.err.println("Erro ao descomprimir arquivo: " + e.getMessage());
        }
    }

    private void countLinesAndCharacters() {
        System.out.println("Digite o caminho do arquivo de texto para contar as linhas e caracteres:");
        String filePath = scanner.nextLine();
        try {
            int[] result = HuffmanEncoderBinary.countLinesAndCharactersInFile(filePath);
            int lineCount = result[0];
            int charCount = result[1];
            System.out.println("O arquivo contém " + lineCount + " linhas e " + charCount + " caracteres.");
        } catch (IOException e) {
            System.out.println("Erro ao contar as linhas e caracteres do arquivo: " + e.getMessage());
        }
    }
}