import java.util.Scanner;

public class MatrixAlgebra {
    
    public static void main(String[] args){
        //Scanner input = new Scanner(System.in);
        Matrix at = new Matrix(3, 3);
        //Matrix a = new Matrix(3, 3);
        Matrix b = new Matrix(3, 1);
        at.gaussJordan(b);
        
    }
    
    //prompts user for information about a quadratic formula and prints the answer
    public static void quadratic(Scanner input){
        System.out.println("Solving Ax^2 + Bx + C = 0" + "\n");
        System.out.print("A: ");
        int a = input.nextInt();
        System.out.print("B: ");
        int b = input.nextInt();
        System.out.print("C: ");
        int c = input.nextInt();
        System.out.println();
        double root = b*b - 4*a*c;
        if(root < 0){
            System.out.println("No real answers");
        } else {
            double sqrt = Math.sqrt(root);
            double res1 = (-1*b + sqrt)/(2*a);
            double res2 = (-1*b - sqrt)/(2*a);
            System.out.println("x1 = " + res1);
            System.out.println("x2 = " + res2);
        }
    }
}
