
import java.util.Arrays;

public class SystolicMatrix {

    private static final int[][] A = {
            {2, 4, 1, 3},
            {-1, -2, 3, 1},
            {-3, 3, -2, 1}
    };

    private static final int[][] B = {
            {2, 2, -1},
            {3, 1, 1},
            {3, -1, 2},
            {3, 4, 5}
    };

    private static class Cell {
        int c = 0;
        Integer ain;
        Integer aout;
        Integer bin;
        Integer bout;

        @Override
        public String toString() {
            return String.format("c=%3d (ain=%3s, bin=%3s)",
                    c,
                    ain == null ? "." : ain.toString(),
                    bin == null ? "." : bin.toString());
        }
    }

    private static class SystolicArray {
        final int rows;
        final int cols;
        final Cell[][] cells;

        final Integer[][] aStream;
        final Integer[][] bStream;

        int stepIndex = 0;

        SystolicArray(int[][] a, int[][] b) {
            this.rows = a.length;
            this.cols = b[0].length;
            this.aStream = addDelays(a);

            int[][] bT = transpose(b);
            Integer[][] bTDelayed = addDelays(bT);
            this.bStream = transpose(bTDelayed);

            cells = new Cell[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    cells[i][j] = new Cell();
                }
            }
        }

        void step() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = cells[i][j];


                    if (j < cols - 1) {
                        cell.ain = cells[i][j + 1].aout;
                    } else {
                        cell.ain = stepIndex < aStream[i].length
                                ? aStream[i][stepIndex]
                                : null;
                    }


                    if (i < rows - 1) {
                        cell.bin = cells[i + 1][j].bout;
                    } else {
                        cell.bin = stepIndex < bStream.length
                                ? bStream[stepIndex][j]
                                : null;
                    }
                }
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = cells[i][j];

                    if (cell.ain != null && cell.bin != null) {
                        cell.c += cell.ain * cell.bin;
                    }

                    cell.aout = cell.ain;
                    cell.bout = cell.bin;
                }
            }

            stepIndex++;
        }

        void runAll() {
            int maxSteps = aStream[0].length + bStream.length + rows + cols;
            for (int t = 0; t < maxSteps; t++) {
                System.out.println("===== STEP " + t + " =====");
                step();
                printState();
            }
        }

        void printState() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    System.out.print(String.format("P%d,%d: %s   ",
                            i + 1, j + 1, cells[i][j]));
                }
                System.out.println();
            }
            System.out.println();
        }

        int[][] getResult() {
            int[][] c = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    c[i][j] = cells[i][j].c;
                }
            }
            return c;
        }
    }

    private static int[][] transpose(int[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        int[][] t = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }

    private static Integer[][] transpose(Integer[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        Integer[][] t = new Integer[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }

    private static Integer[][] addDelays(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int len = cols + rows - 1;

        Integer[][] result = new Integer[rows][len];

        for (int r = 0; r < rows; r++) {
            int prefixNulls = rows - r - 1;
            int suffixNulls = r;

            int index = 0;

            for (int i = 0; i < prefixNulls; i++) {
                result[r][index++] = null;
            }

            for (int c = 0; c < cols; c++) {
                result[r][index++] = matrix[r][c];
            }

            for (int i = 0; i < suffixNulls; i++) {
                result[r][index++] = null;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SystolicArray array = new SystolicArray(A, B);

        array.runAll();

        int[][] C = array.getResult();
        System.out.println("Matricea rezultat C = A * B:");
        for (int[] row : C) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("\nVerificare prin inmultire standard:");
        int[][] classic = multiplyClassic(A, B);
        for (int[] row : classic) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static int[][] multiplyClassic(int[][] a, int[][] b) {
        int m = a.length;
        int k = a[0].length;
        int n = b[0].length;

        int[][] c = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int sum = 0;
                for (int t = 0; t < k; t++) {
                    sum += a[i][t] * b[t][j];
                }
                c[i][j] = sum;
            }
        }
        return c;
    }
}
