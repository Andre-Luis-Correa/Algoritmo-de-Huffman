import java.io.*;
import java.util.*;

public class HuffmanEncoderWord {

    private Map<String, String> huffmanCodeMapWord = new HashMap<>();
    private Map<String, String> reverseHuffmanCodeMapWord = new HashMap<>();

    // Compressão por palavra
    public void compressFileToBinaryByWord(String inputFilePath, String outputFilePath) throws IOException {
        Map<String, Integer> frequencyMapWord = new HashMap<>();

        // Etapa 1: Contar frequência de cada palavra, preservando espaços e quebras de linha
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8"))) {
            int character;
            StringBuilder wordBuilder = new StringBuilder();
            while ((character = reader.read()) != -1) {
                char ch = (char) character;

                // Se for um espaço ou caractere de separação, tratamos como uma "palavra"
                if (Character.isWhitespace(ch)) {
                    if (wordBuilder.length() > 0) {
                        String word = wordBuilder.toString();
                        frequencyMapWord.put(word, frequencyMapWord.getOrDefault(word, 0) + 1);
                        wordBuilder.setLength(0); // Reset the word builder
                    }
                    String separator = String.valueOf(ch);  // Tratar o espaço ou separador
                    frequencyMapWord.put(separator, frequencyMapWord.getOrDefault(separator, 0) + 1);
                } else {
                    wordBuilder.append(ch);
                }
            }
            // Adicionar a última palavra, se houver
            if (wordBuilder.length() > 0) {
                String word = wordBuilder.toString();
                frequencyMapWord.put(word, frequencyMapWord.getOrDefault(word, 0) + 1);
            }
        }

        // Etapa 2: Construir a árvore de Huffman com base nas frequências
        HuffmanNode rootWord = buildHuffmanTreeWord(frequencyMapWord);
        buildHuffmanCodeWord(rootWord, "");

        // Etapa 3: Reescrever o arquivo utilizando os códigos de Huffman para cada palavra e separador
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8"));
             DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(outputFilePath))) {

            // Escrever o mapeamento da árvore no arquivo binário
            dataOut.writeInt(huffmanCodeMapWord.size());
            for (Map.Entry<String, String> entry : huffmanCodeMapWord.entrySet()) {
                dataOut.writeUTF(entry.getKey());
                dataOut.writeUTF(entry.getValue());
            }

            // Substituir as palavras e separadores pelos códigos de Huffman
            StringBuilder encodedText = new StringBuilder();
            int character;
            StringBuilder wordBuilder = new StringBuilder();
            while ((character = reader.read()) != -1) {
                char ch = (char) character;

                if (Character.isWhitespace(ch)) {
                    if (wordBuilder.length() > 0) {
                        String word = wordBuilder.toString();
                        encodedText.append(huffmanCodeMapWord.get(word));
                        wordBuilder.setLength(0); // Reset the word builder
                    }
                    encodedText.append(huffmanCodeMapWord.get(String.valueOf(ch))); // Codificar o separador
                } else {
                    wordBuilder.append(ch);
                }
            }

            if (wordBuilder.length() > 0) {
                String word = wordBuilder.toString();
                encodedText.append(huffmanCodeMapWord.get(word)); // Codificar a última palavra
            }

            writeBits(dataOut, encodedText);
        }
    }

    public void decompressFileFromBinaryByWord(String compressedFilePath, String outputFilePath) throws IOException {
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(compressedFilePath));
             OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8")) {

            // Etapa 1: Reconstruir o mapeamento de Huffman do arquivo compactado
            int mapSize = dataIn.readInt();
            for (int i = 0; i < mapSize; i++) {
                String word = dataIn.readUTF();
                String code = dataIn.readUTF();
                reverseHuffmanCodeMapWord.put(code, word);
            }

            // Etapa 2: Decodificar o arquivo compactado, incluindo palavras e separadores
            StringBuilder tempCode = new StringBuilder();
            byte[] buffer = new byte[8192];

            while (dataIn.available() > 0) {
                int bitLength = dataIn.readInt();
                int byteLength = dataIn.readInt();
                byte[] bytes = new byte[byteLength];
                dataIn.readFully(bytes);

                BitSet bitSet = BitSet.valueOf(bytes);

                for (int i = 0; i < bitLength; i++) {
                    tempCode.append(bitSet.get(i) ? '1' : '0');
                    if (reverseHuffmanCodeMapWord.containsKey(tempCode.toString())) {
                        String decodedWord = reverseHuffmanCodeMapWord.get(tempCode.toString());
                        writer.write(decodedWord); // Escrever palavra ou separador no arquivo de saída
                        tempCode.setLength(0);
                    }
                }
            }
        }
    }

    // Construir a árvore de Huffman por palavra
    private HuffmanNode buildHuffmanTreeWord(Map<String, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(new HuffmanComparator());

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            int sum = left.frequency + right.frequency;
            HuffmanNode parent = new HuffmanNode(null, sum);
            parent.left = left;
            parent.right = right;

            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    // Construir os códigos de Huffman por palavra
    private void buildHuffmanCodeWord(HuffmanNode root, String code) {
        if (root == null) {
            return;
        }

        if (root.word != null) {
            huffmanCodeMapWord.put(root.word, code);
            reverseHuffmanCodeMapWord.put(code, root.word);
            return;
        }

        buildHuffmanCodeWord(root.left, code + "0");
        buildHuffmanCodeWord(root.right, code + "1");
    }

    // Método auxiliar para escrever os bits no arquivo binário
    private void writeBits(DataOutputStream dataOut, StringBuilder encodedText) throws IOException {
        BitSet bitSet = new BitSet(encodedText.length());
        for (int i = 0; i < encodedText.length(); i++) {
            if (encodedText.charAt(i) == '1') {
                bitSet.set(i);
            }
        }

        byte[] bytes = bitSet.toByteArray();
        dataOut.writeInt(encodedText.length()); // Grava o número de bits
        dataOut.writeInt(bytes.length);         // Grava o número de bytes gerados
        dataOut.write(bytes);                   // Grava os bytes compactados
    }
}