import java.util.Scanner;

public class Matrix {
    private int[][] matrix;
    private static final Scanner input = new Scanner(System.in);

    // constructs a rows x cols matrix
    // throws IllegalArgumentException if either rows or cols is <= 0
    // asks user for input about the elements
    public Matrix(int rows, int cols) {
        this(rows, cols, false);
    }

    // constructs a rows x cols matrix
    // throws IllegalArgumentException if either rows or cols is <= 0
    // if !leaveEmpty, asks user for input about the elements
    private Matrix(int rows, int cols, boolean leaveEmpty) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Invalid dimensions");
        }
        matrix = new int[rows][cols];
        if (!leaveEmpty) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    System.out.print("number[" + (i + 1) + "][" + (j + 1) + "]: ");
                    matrix[i][j] = input.nextInt();
                }
            }
        }
        System.out.println();
    }
    
    //returns a copy of the Matrix
    public Matrix copy(){
        Matrix result = new Matrix(matrix.length, matrix[0].length, true);
        for(int r = 0; r < matrix.length; r++){
            for(int c = 0; c < matrix[0].length; c++){
                result.matrix[r][c] = matrix[r][c];
            }
        }
        return result;
    }
    
    // returns a Matrix result from multiplying this one with the other one
    // returns null if the multiplication is invalid
    public Matrix multiply(Matrix other) {
        if (matrix[0].length != other.matrix.length) {
            return null;
        }
        Matrix result = new Matrix(matrix.length, other.matrix[0].length, true);
        for (int r = 0; r < result.matrix.length; r++) {
            for (int c = 0; c < result.matrix[0].length; c++) {
                int sum = 0;
                for (int i = 0; i < matrix[0].length; i++) {
                    sum += matrix[r][i] * other.matrix[i][c];
                }
                result.matrix[r][c] = sum;
            }
        }
        return result;
    }
    
    // returns the determinant of the Matrix
    // throws IllegalArgumentException if the Matrix is not square
    public int det(){
        if(matrix.length != matrix[0].length){
            throw new IllegalArgumentException("Must be a square matrix");
        }
        return det(matrix);
    }
    
    // returns the determinant of the given square matrix
    private int det(int[][] curMatrix){
        if(curMatrix.length == 2){
            return curMatrix[0][0]*curMatrix[1][1] - curMatrix[0][1]*curMatrix[1][0];
        }
        int negOrPos = 1;
        int sum = 0;
        for(int i = 0; i < curMatrix.length; i++){
            int[][] smaller = new int[curMatrix.length-1][curMatrix.length-1];
            int r = 0;
            int c = 0;
            for(int row = 1; row < curMatrix.length; row++){
                for(int col = 0; col < curMatrix.length; col++){
                    if(col != i){
                        smaller[r][c] = curMatrix[row][col];
                        c++;
                    }
                }
                r++;
                c = 0;
            }
            int detSmaller = det(smaller);
            sum += negOrPos*curMatrix[0][i]*detSmaller;
            negOrPos = -1*negOrPos;
        }
        return sum;
    }
    
    //performs Gauss-Jordan elimination on the augmented Matrix of the form
    //[this | other]
    //throws IllegalArgumentException if the Matrix objects have different row numbers
    public void gaussJordan(Matrix other){
        if(other.matrix.length != matrix.length){
            throw new IllegalArgumentException("incompatible");
        }
        Matrix result = new Matrix(matrix.length, matrix[0].length + 
                other.matrix[0].length, true);
        for(int r = 0; r < matrix.length; r++){
            for(int c = 0; c < matrix[0].length; c++){
                result.matrix[r][c] = matrix[r][c];
            }
            for(int c = 0; c < other.matrix[0].length; c++){
                result.matrix[r][c + matrix[0].length] = other.matrix[r][c]; 
            }
        }
        eliminate(result, matrix[0].length);
    }
    
    //prints the inverse of the current Matrix
    //asks user for input on what actions they want to do to achieve the inverse
    //does not alter the current Matrix
    //throws IllegalArgumentException if Matrix is not square
    //prints "Singular matrix" if not invertible and returns null
    public void inverse(){
        if(matrix.length != matrix[0].length){
            throw new IllegalArgumentException("must be square");
        }
        double det = det(matrix);
        if(det == 0){
            System.out.println("Singular matrix");
            return;
        }
        Matrix result = new Matrix(matrix.length, 2*matrix[0].length, true);
        for(int r = 0; r < matrix.length; r++){
            for(int c = 0; c < matrix[0].length; c++){
                result.matrix[r][c] = matrix[r][c];
            }
        }
        for(int i = 0; i < matrix.length; i++){
            result.matrix[i][i+matrix.length] = 1;
        }
        eliminate(result, matrix.length);
        double[][] inverse = new double[matrix.length][matrix.length];
        for(int r = 0; r < inverse.length; r++){
            double scalar = result.matrix[r][r];
            for(int c = 0; c < inverse[0].length; c++){
                inverse[r][c] = result.matrix[r][c + matrix[0].length]/scalar;
            }
        }
        //prints inverse
        System.out.println("\nInverse:");
        for (int r = 0; r < matrix.length; r++) {
            System.out.print("[");
            for (int c = 0; c < matrix[0].length - 1; c++) {
                System.out.print(inverse[r][c] + ", ");
            }
            System.out.println(inverse[r][inverse[0].length - 1] + "]");
        }
        
    }
    
    //performs Gauss-Jordan elimination on the given Matrix
    //which has "colsBeforeAugment" cols before the augmented part of the Matrix
    //asks user for input on what actions they want to do to proceed
    private void eliminate(Matrix result, int colsBeforeAugment){
        System.out.println(result.augmentedMatrix(matrix.length));
        System.out.print("What do you want to do? ");
        System.out.print("(M)ultily/(D)ivide a row by a scalar, (S)wap two rows"
                + ", (C)lear a column, (Q)uit: ");
        String userInput = input.next();
        char response = Character.toLowerCase(userInput.charAt(0));
        int pivot = 0;
        while(response != 'q'){
            if(response == 'm' || response == 'd'){
                int row = result.getRow();
                System.out.print("Which scalar: ");
                double scalar = input.nextDouble();
                if(response == 'd'){
                    scalar = 1/scalar;
                }
                for(int c = 0; c < result.matrix[row].length; c++){
                    result.matrix[row][c] *= scalar;
                }
            } else if(response == 's'){
                int row1 = result.getRow();
                int row2 = result.getRow();
                int[] temp = result.matrix[row1];
                result.matrix[row1] = result.matrix[row2];
                result.matrix[row2] = temp;
            } else if(response == 'c'){
                if(pivot >= result.matrix.length){
                    System.out.println("Cannot clear anymore");
                    continue;
                }
                int pivotPoint = result.matrix[pivot][pivot];
                if(pivotPoint == 0){
                    System.out.println("Cannot pivot on a 0");
                } else {
                    for(int row = 0; row < result.matrix.length; row++){
                        if(row != pivot){
                            int cur = result.matrix[row][pivot];
                            if(cur != 0){
                                for(int col = 0; col < result.matrix[0].length; col++){
                                    result.matrix[row][col] = cur*result.matrix[pivot][col]
                                            - pivotPoint*result.matrix[row][col];
                                }
                            }
                        }
                    }
                    pivot++;
                }
            }
            
            System.out.println(result.augmentedMatrix(colsBeforeAugment));
            System.out.print("What do you want to do? ");
            System.out.print("(M)ultily/(D)ivide a row by a scalar, (S)wap two rows"
                    + ", (C)lear a column, (Q)uit: ");
            userInput = input.next();
            response = Character.toLowerCase(userInput.charAt(0));
        }
    }
    
    //prompts the user for a row and keeps prompting while the input is invalid
    //returns (row - 1) when 1 <= user-chosen row <= # of rows
    //so the user doesn't need to know about 0-based indexing
    private int getRow(){
        System.out.print("Which row: ");
        int row = input.nextInt();
        while(row < 1 || row > matrix.length){
            System.out.print("Which row: ");
            row = input.nextInt();
        }
        return row - 1;
    }
    
    //returns a String representation of an augmented Matrix where there are
    //leftCols cols before the augmentation starts 
    //throws IllegalArgumentException if leftCols > cols in Matrix
    private String augmentedMatrix(int leftCols){
        if(leftCols >= matrix[0].length){
            throw new IllegalArgumentException();
        }
        String result = "";
        for (int r = 0; r < matrix.length; r++) {
            result += "[";
            for (int c = 0; c < leftCols - 1; c++) {
                result += matrix[r][c] + ", ";
            }
            result += matrix[r][leftCols - 1] + "|";
            for(int c = leftCols; c < matrix[0].length - 1; c++){
                result += matrix[r][c] + ", ";
            }
            result += matrix[r][matrix[0].length - 1] + "]\n";
        }
        return result;
    }
    
    // returns a String representation of the Matrix
    @Override
    public String toString() {
        String result = "";
        for (int r = 0; r < matrix.length; r++) {
            result += "[";
            for (int c = 0; c < matrix[0].length - 1; c++) {
                result += matrix[r][c] + ", ";
            }
            result += matrix[r][matrix[0].length - 1] + "]\n";
        }
        return result;
    }
}
