import java.util.Scanner;

public class exercicio02 {

    static Scanner leitorTeclado;
    
    public static void main(String[] args) throws Exception {
        leitorTeclado = new Scanner(System.in);
        System.out.println("Qual é o seu nome?");

        String nome = leitorTeclado.nextLine();

        System.out.print("Olá, "+nome+", seja muito bem-vindo(a)! É um prazer te conhecer.");

        leitorTeclado.close();

    }
    
}
