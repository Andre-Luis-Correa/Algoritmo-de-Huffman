import java.io.*;
import java.util.*;

public class HuffmanEncoderBinary {

    private Map<Character, String> huffmanCodeMap = new HashMap<>();
    private Map<String, Character> reverseHuffmanCodeMap = new HashMap<>();

    public void compressFileToBinary(String inputFilePath, String outputFilePath) throws IOException {
        // Passo 1: Processar o arquivo em partes para calcular a frequência dos caracteres
        Map<Character, Integer> frequencyMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            int character;
            while ((character = reader.read()) != -1) {
                frequencyMap.put((char) character, frequencyMap.getOrDefault((char) character, 0) + 1);
            }
        }

        // Passo 2: Construir a árvore de Huffman com base na frequência
        HuffmanNode root = buildHuffmanTree(frequencyMap);
        buildHuffmanCode(root, "");

        // Passo 3: Processar o arquivo novamente em blocos grandes para compactar e gravar o resultado
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(outputFilePath))) {

            // Escrever o mapeamento da árvore no arquivo binário
            dataOut.writeInt(huffmanCodeMap.size());
            for (Map.Entry<Character, String> entry : huffmanCodeMap.entrySet()) {
                dataOut.writeChar(entry.getKey());
                dataOut.writeUTF(entry.getValue());
            }

            // Codificar e escrever o conteúdo do arquivo em blocos grandes
            char[] buffer = new char[8192]; // Usar um buffer maior para arquivos grandes
            int charsRead;
            StringBuilder encodedText = new StringBuilder();

            while ((charsRead = reader.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; i++) {
                    encodedText.append(huffmanCodeMap.get(buffer[i]));
                }

                // Escrever blocos de dados binários para evitar sobrecarregar a memória
                if (encodedText.length() > 8192) {
                    writeBits(dataOut, encodedText);
                    encodedText.setLength(0); // Limpa o buffer
                }
            }

            // Grava os bits restantes
            if (encodedText.length() > 0) {
                writeBits(dataOut, encodedText);
            }
        }
    }

    private void writeBits(DataOutputStream dataOut, StringBuilder encodedText) throws IOException {
        BitSet bitSet = new BitSet(encodedText.length());
        for (int i = 0; i < encodedText.length(); i++) {
            if (encodedText.charAt(i) == '1') {
                bitSet.set(i);
            }
        }

        byte[] bytes = bitSet.toByteArray();

        // Grava o número exato de bits e bytes
        dataOut.writeInt(encodedText.length()); // Grava o número de bits
        dataOut.writeInt(bytes.length);         // Grava o número de bytes gerados
        dataOut.write(bytes);                   // Grava os bytes compactados
    }

    // Construir a árvore de Huffman
    private HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(new HuffmanComparator());

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            int sum = left.frequency + right.frequency;
            HuffmanNode parent = new HuffmanNode('\0', sum);
            parent.left = left;
            parent.right = right;

            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    // Construir os códigos de Huffman a partir da árvore
    private void buildHuffmanCode(HuffmanNode root, String code) {
        if (root == null) {
            return;
        }

        // Nó folha
        if (root.left == null && root.right == null) {
            huffmanCodeMap.put(root.character, code);
            reverseHuffmanCodeMap.put(code, root.character);
            return;
        }

        buildHuffmanCode(root.left, code + "0");
        buildHuffmanCode(root.right, code + "1");
    }

    public void decompressFileFromBinary(String compressedFilePath, String outputFilePath) throws IOException {
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(compressedFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            // Lê o tamanho do mapeamento de Huffman
            int mapSize = dataIn.readInt();

            // Reconstrói o mapeamento da árvore de Huffman
            for (int i = 0; i < mapSize; i++) {
                char character = dataIn.readChar();
                String code = dataIn.readUTF();
                reverseHuffmanCodeMap.put(code, character);
            }

            // Lê o número de bits compactados e os processa em blocos
            while (dataIn.available() > 0) {
                int bitLength = dataIn.readInt();
                int byteLength = dataIn.readInt();

                byte[] bytes = new byte[byteLength];
                dataIn.readFully(bytes); // Lê os dados compactados

                // Converte os bytes para uma string de bits (0s e 1s)
                BitSet bitSet = BitSet.valueOf(bytes);
                StringBuilder encodedText = new StringBuilder();
                for (int i = 0; i < bitLength; i++) {
                    encodedText.append(bitSet.get(i) ? '1' : '0');
                }

                // Decodifica o texto em blocos usando o mapeamento de Huffman
                StringBuilder decodedText = new StringBuilder();
                StringBuilder tempCode = new StringBuilder();
                for (char bit : encodedText.toString().toCharArray()) {
                    tempCode.append(bit);
                    if (reverseHuffmanCodeMap.containsKey(tempCode.toString())) {
                        decodedText.append(reverseHuffmanCodeMap.get(tempCode.toString()));
                        tempCode.setLength(0);
                    }
                }

                // Escreve o bloco decodificado no arquivo de saída
                writer.write(decodedText.toString());
            }
        }
    }

    // Método auxiliar para escrever o arquivo de saída
    private void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    public static int[] contarLinhasECaracteresArquivo(String filePath) throws IOException {
        int lineCount = 0;
        int charCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;  // Conta as linhas
                charCount += line.length();  // Conta os caracteres em cada linha
            }
        }
        return new int[]{lineCount, charCount};
    }


}