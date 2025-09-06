import java.util.Scanner;

public class exercicio04 {

static Scanner leitorTeclado;

public static void main(String[] args) throws Exception{

leitorTeclado = new Scanner(System.in);
int primeironumero;
int segundonumero;
int soma;

System.out.println("Digite o primeiro número inteiro.");
primeironumero = leitorTeclado.nextInt();

System.out.println("Digite o segundo número inteiro.");
segundonumero = leitorTeclado.nextInt();

soma = primeironumero + segundonumero;

System.out.println("A soma entre "+primeironumero+" e "+segundonumero+" é igual a "+soma+".");
leitorTeclado.close();
}
    
}
