import java.util.Scanner;

public class exercicio03 {

static Scanner leitorTeclado;

public static void main (String[] args) throws Exception {

leitorTeclado = new Scanner(System.in);

System.out.println("Olá! Qual é o seu nome?");

String nome = leitorTeclado.nextLine();

System.out.println("Qual é o seu salário, "+nome+"?");

double salario = leitorTeclado.nextDouble();

System.out.println("O(a) funcionário(a) "+nome+" atualmente possui um salário de R$ "+salario+".");

leitorTeclado.close();
}

}
