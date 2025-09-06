import java.util.Scanner;

public class exercicio06 {

static Scanner leitorTeclado;

public static void main(String[] args) throws Exception{

leitorTeclado = new Scanner(System.in);
int numero;
int antecessor;
int sucessor;

System.out.println("Digite um número inteiro.");
numero = leitorTeclado.nextInt();

antecessor = numero - 1;
sucessor = numero + 1;

System.out.println("O antecessor de "+numero+" é "+antecessor+" e o sucessor é "+sucessor+".");

leitorTeclado.close();
}
}
