import java.io.*;
import java.util.*;

public class HuffmanEncoderChar {

    private Map<Character, String> huffmanCodeMapChar = new HashMap<>();
    private Map<String, Character> reverseHuffmanCodeMapChar = new HashMap<>();

    /**
     * Descrição: Comprime um arquivo de texto codificando seus caracteres usando a codificação de Huffman e salva o resultado em um arquivo binário.
     * Pré-condições: O arquivo de entrada deve ser um arquivo de texto existente e legível.
     * Pós-condições: Um arquivo binário compactado será gerado com a codificação de Huffman por caractere.
     */
    public void compressFileToBinaryByChar(String inputFilePath, String outputFilePath) throws IOException {
        long startTime = System.currentTimeMillis();  // Iniciar medição de tempo
        Map<Character, Integer> frequencyMapChar = new HashMap<>();

        // Ler o arquivo com codificação UTF-8 e calcular a frequência de cada caractere
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8"))) {
            int character;
            while ((character = reader.read()) != -1) {
                frequencyMapChar.put((char) character, frequencyMapChar.getOrDefault((char) character, 0) + 1);
            }
        }

        // Construir a árvore de Huffman com base nas frequências
        HuffmanNode rootChar = buildHuffmanTreeChar(frequencyMapChar);
        buildHuffmanCodeChar(rootChar, "");

        // Escrever o arquivo compactado binário em partes menores
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF-8"));
             DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(outputFilePath))) {

            // Escrever o mapeamento da árvore no arquivo binário
            dataOut.writeInt(huffmanCodeMapChar.size());
            for (Map.Entry<Character, String> entry : huffmanCodeMapChar.entrySet()) {
                dataOut.writeChar(entry.getKey());
                dataOut.writeUTF(entry.getValue());
            }

            // Processar e gravar o arquivo em blocos menores
            char[] buffer = new char[8192];  // Buffer de 8 KB
            int charsRead;
            StringBuilder encodedText = new StringBuilder();

            while ((charsRead = reader.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; i++) {
                    encodedText.append(huffmanCodeMapChar.get(buffer[i]));
                }

                // Gravar os bits codificados no arquivo
                writeBits(dataOut, encodedText);
                encodedText.setLength(0);  // Limpar o buffer
            }
        }
        long endTime = System.currentTimeMillis();  // Finalizar medição de tempo
        System.out.println("Tempo gasto na compressão por caractere: " + (endTime - startTime) + " ms");
    }

    /**
     * Descrição: Descomprime um arquivo binário previamente compactado por caracteres usando a codificação de Huffman e restaura o texto original.
     * Pré-condições: O arquivo compactado deve existir e ter sido gerado utilizando a compressão por caractere.
     * Pós-condições: Um arquivo de texto descompactado será gerado.
     */
    public void decompressFileFromBinaryByChar(String compressedFilePath, String outputFilePath) throws IOException {
        long startTime = System.currentTimeMillis();  // Iniciar medição de tempo
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(compressedFilePath));
             OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8")) {

            // Reconstruir o mapeamento da árvore de Huffman a partir do arquivo compactado
            int mapSize = dataIn.readInt();
            for (int i = 0; i < mapSize; i++) {
                char character = dataIn.readChar();
                String code = dataIn.readUTF();
                reverseHuffmanCodeMapChar.put(code, character);
            }

            // Ler o arquivo compactado e decodificar os bits
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
                    if (reverseHuffmanCodeMapChar.containsKey(tempCode.toString())) {
                        writer.write(reverseHuffmanCodeMapChar.get(tempCode.toString()));
                        tempCode.setLength(0);
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();  // Finalizar medição de tempo
        System.out.println("Tempo gasto na descompressão por caractere: " + (endTime - startTime) + " ms");
    }

    /**
     * Descrição: Constrói a árvore de Huffman com base nas frequências dos caracteres de um texto.
     * Pré-condições: O mapa de frequências deve conter os caracteres e suas frequências.
     * Pós-condições: Retorna a raiz da árvore de Huffman construída.
     */
    private HuffmanNode buildHuffmanTreeChar(Map<Character, Integer> frequencyMap) {
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

    /**
     * Descrição: Constrói os códigos de Huffman para cada caractere da árvore de Huffman.
     * Pré-condições: A árvore de Huffman deve estar construída corretamente.
     * Pós-condições: Os códigos de Huffman são armazenados nos mapas `huffmanCodeMapChar` e `reverseHuffmanCodeMapChar`.
     */
    private void buildHuffmanCodeChar(HuffmanNode root, String code) {
        if (root == null) {
            return;
        }

        if (root.left == null && root.right == null) {
            huffmanCodeMapChar.put(root.character, code);
            reverseHuffmanCodeMapChar.put(code, root.character);
            return;
        }

        buildHuffmanCodeChar(root.left, code + "0");
        buildHuffmanCodeChar(root.right, code + "1");
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