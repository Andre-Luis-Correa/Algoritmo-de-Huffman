import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HuffmanEncoderBinary huffmanEncoder = new HuffmanEncoderBinary();
        boolean running = true;  // Condição de controle do loop

        while (running) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Compactar arquivo (por caractere)");
            System.out.println("2. Descompactar arquivo (por caractere)");
            System.out.println("3. Contar o número de linhas de um arquivo");
            System.out.println("4. Sair");

            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            switch (escolha) {
                case 1:
                    // O usuário informa o arquivo de entrada
                    System.out.println("Digite o nome do arquivo de entrada (.txt) ou 'exit' para sair:");
                    String inputFilePath = scanner.nextLine();

                    // Verifica se o usuário digitou 'exit' para encerrar
                    if (inputFilePath.equalsIgnoreCase("exit")) {
                        running = false;
                        System.out.println("Saindo...");
                        break;
                    }

                    String outputFilePath = "compressed.bin"; // Nome padrão para o arquivo compactado
                    try {
                        huffmanEncoder.compressFileToBinary(inputFilePath, outputFilePath);
                        System.out.println("Arquivo compactado com sucesso. Salvo como 'compressed.bin' na raiz do projeto.");
                    } catch (IOException e) {
                        System.err.println("Erro ao compactar o arquivo: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Usando nome padrão para o arquivo compactado e arquivo descompactado
                    String compressedFilePath = "compressed.bin";
                    String decompressedFilePath = "decompressed.txt";
                    try {
                        huffmanEncoder.decompressFileFromBinary(compressedFilePath, decompressedFilePath);
                        System.out.println("Arquivo descompactado com sucesso. Salvo como 'decompressed.txt' na raiz do projeto.");
                    } catch (IOException e) {
                        System.err.println("Erro ao descompactar o arquivo: " + e.getMessage());
                    }
                    break;
                case 3:
                    // Contar o número de linhas e caracteres de um arquivo
                    System.out.println("Digite o caminho do arquivo de texto para contar as linhas e caracteres:");
                    String filePath = scanner.nextLine();
                    try {
                        int[] result = HuffmanEncoderBinary.contarLinhasECaracteresArquivo(filePath);
                        int lineCount = result[0];
                        int charCount = result[1];
                        System.out.println("O arquivo contém " + lineCount + " linhas e " + charCount + " caracteres.");
                    } catch (IOException e) {
                        System.out.println("Erro ao contar as linhas e caracteres do arquivo: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Saindo...");
                    running = false;  // Define a condição para encerrar o loop
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }
}