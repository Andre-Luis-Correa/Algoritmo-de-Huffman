import java.util.Scanner;

public class Main {

    /**
     * Descrição: Método principal que inicializa o programa, criando um objeto Scanner para entrada do usuário
     *            e invocando o menu para exibir as opções disponíveis.
     * Pré-condições: O programa deve ser executado em um ambiente com suporte para entrada via console.
     * Pós-condições: O menu será exibido ao usuário, aguardando interações.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner);
        menu.displayMenu();
    }
}