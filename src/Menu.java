import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public void displayMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\nMENU");
            System.out.println("1. Comprimir arquivo texto (por caracter)");
            System.out.println("2. Descomprimir arquivo texto (por caracter)");
            System.out.println("3. Comprimir arquivo texto (por palavra)");
            System.out.println("4. Descomprimir arquivo texto (por palavra)");
            System.out.println("5. Contar número de linhas e caracteres");
            System.out.println("6. Sair");
            System.out.print("Selecione uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

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
                    countLinesAndCharactersOption();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    // Compactação e descompactação por caractere
    private void compressFileByChar() {
        System.out.print("\nDigite o nome do arquivo de entrada (.txt) para compressão por caracter: ");
        String inputFilePath = scanner.nextLine();
        String outputFilePath = inputFilePath.replace(".txt", "_compactado_caracter.bin");

        try {
            huffmanEncoderChar.compressFileToBinaryByChar(inputFilePath, outputFilePath);
            System.out.println("Arquivo comprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao comprimir arquivo: " + e.getMessage());
        }
    }

    private void decompressFileByChar() {
        System.out.print("\nDigite o nome do arquivo (.bin) para descompressão por caracter: ");
        String compressedFilePath = scanner.nextLine();
        String outputFilePath = compressedFilePath.replace("_compactado_caracter.bin", "_descompactado_caracter.txt");

        try {
            huffmanEncoderChar.decompressFileFromBinaryByChar(compressedFilePath, outputFilePath);
            System.out.println("Arquivo descomprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir arquivo: " + e.getMessage());
        }
    }

    // Compactação e descompactação por palavra
    private void compressFileByWord() {
        System.out.print("\nDigite o nome do arquivo de entrada (.txt) para compressão por palavra: ");
        String inputFilePath = scanner.nextLine();
        String outputFilePath = inputFilePath.replace(".txt", "_compactado_palavra.bin");

        try {
            huffmanEncoderWord.compressFileToBinaryByWord(inputFilePath, outputFilePath);
            System.out.println("Arquivo comprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao comprimir arquivo: " + e.getMessage());
        }
    }

    private void decompressFileByWord() {
        System.out.print("\nDigite o nome do arquivo (.bin) para descompressão por palavra: ");
        String compressedFilePath = scanner.nextLine();
        String outputFilePath = compressedFilePath.replace("_compactado_palavra.bin", "_descompactado_palavra.txt");

        try {
            huffmanEncoderWord.decompressFileFromBinaryByWord(compressedFilePath, outputFilePath);
            System.out.println("Arquivo descomprimido com sucesso como '" + outputFilePath + "'");
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir arquivo: " + e.getMessage());
        }
    }

    // Opção para contar linhas e caracteres
    private void countLinesAndCharactersOption() {
        System.out.println("\nDigite o nome do arquivo para contar o número de linhas e caracteres:");
        String inputFilePath = scanner.nextLine();

        try {
            int[] result = countLinesAndCharacters(inputFilePath);
            int lineCount = result[0];
            int charCount = result[1];
            System.out.println("O arquivo contém " + lineCount + " linhas e " + charCount + " caracteres.");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static int[] countLinesAndCharacters(String inputFilePath) throws IOException {
        int lineCount = 0;
        int charCount = 0;

        // Ler o arquivo e contar as linhas e os caracteres
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;  // Contar linhas
                charCount += line.length();  // Contar caracteres
            }
        }

        return new int[]{lineCount, charCount};  // Retornar o número de linhas e caracteres
    }

}