import java.io.*;
import java.util.*;

public class HuffmanEncoderWord {

    private Map<String, String> huffmanCodeMapWord = new HashMap<>();
    private Map<String, String> reverseHuffmanCodeMapWord = new HashMap<>();

    /**
     * Descrição: Comprime um arquivo de texto, substituindo as palavras e separadores por seus códigos de Huffman, e salva o resultado em um arquivo binário.
     * Pré-condições: O arquivo de entrada deve existir e ser um arquivo de texto legível.
     * Pós-condições: Um arquivo binário compactado será gerado com a codificação de Huffman por palavra.
     */
    public void compressFileToBinaryByWord(String inputFilePath, String outputFilePath) throws IOException {
        long startTime = System.currentTimeMillis();  // Iniciar medição de tempo

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

        long endTime = System.currentTimeMillis();  // Finalizar medição de tempo
        System.out.println("Tempo gasto na compressão por palavra: " + (endTime - startTime) + " ms");
    }

    /**
     * Descrição: Descomprime um arquivo binário previamente compactado por palavras usando a codificação de Huffman e restaura o texto original.
     * Pré-condições: O arquivo compactado deve existir e ter sido gerado utilizando a compressão por palavra.
     * Pós-condições: Um arquivo de texto descompactado será gerado.
     */
    public void decompressFileFromBinaryByWord(String compressedFilePath, String outputFilePath) throws IOException {
        long startTime = System.currentTimeMillis();  // Iniciar medição de tempo

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

        long endTime = System.currentTimeMillis();  // Finalizar medição de tempo
        System.out.println("Tempo gasto na descompressão por palavra: " + (endTime - startTime) + " ms");
    }

    /**
     * Descrição: Constrói a árvore de Huffman com base nas frequências das palavras de um texto.
     * Pré-condições: O mapa de frequências deve conter as palavras e suas frequências.
     * Pós-condições: Retorna a raiz da árvore de Huffman construída.
     */
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

    /**
     * Descrição: Constrói os códigos de Huffman para cada palavra da árvore de Huffman.
     * Pré-condições: A árvore de Huffman deve estar construída corretamente.
     * Pós-condições: Os códigos de Huffman são armazenados nos mapas `huffmanCodeMapWord` e `reverseHuffmanCodeMapWord`.
     */
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

    /**
     * Descrição: Escreve a representação binária do texto codificado no arquivo binário.
     * Pré-condições: O texto já deve estar codificado em uma sequência de bits.
     * Pós-condições: A sequência de bits é convertida em bytes e gravada no arquivo binário.
     */
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