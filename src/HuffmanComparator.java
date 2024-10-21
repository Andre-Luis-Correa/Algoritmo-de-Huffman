import java.util.Comparator;

class HuffmanComparator implements Comparator<HuffmanNode> {

    /**
     * Descrição: Compara dois nós `HuffmanNode` com base na frequência.
     * Pré-condições: `x` e `y` são instâncias de `HuffmanNode` com a variável `frequency` inicializada.
     * Pós-condições: Retorna a diferença entre as frequências dos dois nós.
     *                Um valor negativo indica que `x` tem menor frequência que `y`,
     *                zero indica que as frequências são iguais,
     *                e um valor positivo indica que `x` tem maior frequência que `y`.
     */
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.frequency - y.frequency;
    }
}