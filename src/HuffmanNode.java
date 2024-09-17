class HuffmanNode {
    String word;     // Para compressão por palavra
    char character;  // Para compressão por caractere
    int frequency;   // Frequência do caractere ou palavra
    HuffmanNode left;
    HuffmanNode right;

    // Construtor para caracteres
    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.word = null;
        left = null;
        right = null;
    }

    // Construtor para palavras
    HuffmanNode(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
        this.character = '\0'; // Valor nulo para char quando usando palavra
        left = null;
        right = null;
    }
}
