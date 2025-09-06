import java.util.Scanner;

public class exercicio05 {

static Scanner leitorTeclado;

public static void main(String[] args) throws Exception{
leitorTeclado = new Scanner(System.in);
Double nota1;
Double nota2;
Double media;

System.out.println("Digite a primeira nota.");
nota1 = leitorTeclado.nextDouble();

System.out.println("Digite a segunda nota.");
nota2 = leitorTeclado.nextDouble();

media = (nota1 + nota2)/2;

System.out.println("A média entre "+nota1+" e "+nota2+" é de "+media+".");

leitorTeclado.close();
}
    
}
