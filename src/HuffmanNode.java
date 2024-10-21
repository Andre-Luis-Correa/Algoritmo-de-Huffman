class HuffmanNode {
    String word;
    char character;
    int frequency;
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
