import java.util.Random;



public final class Automaton {
    public static final int OFF = 0;
    public static final int ON = 1;


    private final int width;
    private final int steps;
    private final Rule rule;
    private final boolean wrap;
    private final int[] initial;


    private Automaton(int width, int steps, Rule rule, boolean wrap, int[] initial) {
        if (width <= 0 || steps <= 0) throw new IllegalArgumentException("width/steps must be > 0");
        if (initial == null || initial.length != width) throw new IllegalArgumentException("initial length!=width");
        this.width = width; this.steps = steps; this.rule = rule; this.wrap = wrap; this.initial = initial.clone();
    }


    public static Automaton singleCenter(int width, int steps, Rule rule, boolean wrap) {
        int[] row = new int[width];
        row[width/2] = ON;
        return new Automaton(width, steps, rule, wrap, row);
    }


    public static Automaton randomInit(int width, int steps, Rule rule, boolean wrap, long seed, double density) {
        if (density < 0.0 || density > 1.0) throw new IllegalArgumentException("density in [0,1]");
        Random rng = new Random(seed);
        int[] row = new int[width];
        for (int i = 0; i < width; i++) row[i] = (rng.nextDouble() < density) ? ON : OFF;
        return new Automaton(width, steps, rule, wrap, row);
    }


    public int[][] run() {
        int[][] hist = new int[steps][width];
        System.arraycopy(initial, 0, hist[0], 0, width);
        for (int t = 1; t < steps; t++) hist[t] = evolveOnce(hist[t-1]);
        return hist;
    }


    public int[] evolveOnce(int[] row) {
        int[] nxt = new int[width];
        for (int i = 0; i < width; i++) {
            int l, c, r;
            if (wrap) {
                l = row[(i - 1 + width) % width];
                c = row[i];
                r = row[(i + 1) % width];
            } else {
                l = (i - 1 >= 0) ? row[i - 1] : OFF;
                c = row[i];
                r = (i + 1 < width) ? row[i + 1] : OFF;
            }
            nxt[i] = rule.nextState(l, c, r);
        }
        return nxt;
    }


    public static String renderText(int[][] hist, char on, char off) {
        StringBuilder sb = new StringBuilder(hist.length * (hist[0].length + 1));
        for (int[] row : hist) {
            for (int cell : row) sb.append(cell == ON ? on : off);
            sb.append('\n');
        }
        return sb.toString();
    }
}