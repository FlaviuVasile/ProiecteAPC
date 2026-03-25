import java.util.Arrays;

public class FirBroadcast {

    private static final double[] weights = {1, 2, 2, 1};
    private final double[] history = new double[weights.length];
    private final double[] partial = new double[weights.length];

    public double run(double sample) {
        push(sample);

        double result = 0;

        for (int i = 0; i < weights.length; i++) {
            result += history[i] * weights[i];
            partial[i] = result;
        }

        System.out.println("history = " + Arrays.toString(history));
        System.out.println("partial = " + Arrays.toString(partial));

        return result;
    }

    private void push(double value) {
        for (int i = history.length - 1; i > 0; i--) {
            history[i] = history[i - 1];
        }
        history[0] = value;
    }

    public static void main(String[] args) {
        FirBroadcast fp = new FirBroadcast();
        double[] seq = {1, 2, 3, 4, 5};

        for (double s : seq) {
            double y = fp.run(s);
            System.out.println("x = " + s + "  y = " + y);
            System.out.println(" ");
        }
    }
}
