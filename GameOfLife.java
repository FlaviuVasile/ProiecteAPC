public final class GameOfLife {
    public static final int DEAD = 0;
    public static final int ALIVE = 1;

    private final int width;
    private final int height;
    private final int steps;
    private final boolean wrap;
    private final int[][] initial;

    private GameOfLife(int width, int height, int steps, boolean wrap, int[][] initial) {
        if (width <= 0 || height <= 0 || steps <= 0)
            throw new IllegalArgumentException("width/height/steps must be > 0");
        if (initial == null || initial.length != height || initial[0].length != width)
            throw new IllegalArgumentException("initial grid size mismatch");
        this.width = width;
        this.height = height;
        this.steps = steps;
        this.wrap = wrap;
        this.initial = copyGrid(initial);
    }

    public static GameOfLife randomInit(int width, int height, int steps, boolean wrap, long seed, double density) {
        if (density < 0.0 || density > 1.0) throw new IllegalArgumentException("density in [0,1]");
        java.util.Random rng = new java.util.Random(seed);
        int[][] g = new int[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                g[y][x] = (rng.nextDouble() < density) ? ALIVE : DEAD;
        return new GameOfLife(width, height, steps, wrap, g);
    }

    public static GameOfLife empty(int width, int height, int steps, boolean wrap) {
        return new GameOfLife(width, height, steps, wrap, new int[height][width]);
    }

    public static GameOfLife withPatternCentered(int width, int height, int steps, boolean wrap, int[][] pattern) {
        int[][] g = new int[height][width];
        stampCentered(g, pattern);
        return new GameOfLife(width, height, steps, wrap, g);
    }

    // --- Evoluție (B3/S23) ----
    public int[][] evolveOnce(int[][] grid) {
        int[][] nxt = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int n = neighbors(grid, x, y);
                int c = grid[y][x];
                nxt[y][x] = (c == ALIVE)
                        ? ((n == 2 || n == 3) ? ALIVE : DEAD)
                        : ((n == 3) ? ALIVE : DEAD);
            }
        }
        return nxt;
    }

    private int neighbors(int[][] g, int x, int y) {
        int sum = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (wrap) {
                    nx = (nx + width) % width;
                    ny = (ny + height) % height;
                    sum += g[ny][nx];
                } else {
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height)
                        sum += g[ny][nx];
                }
            }
        }
        return sum;
    }

    public void runStreaming(java.io.PrintStream out, char on, char off, long animateMs) {
        int[][] grid = copyGrid(initial);
        renderGrid(out, grid, on, off);
        for (int t = 1; t < steps; t++) {
            grid = evolveOnce(grid);
            if (animateMs > 0) {
                try { Thread.sleep(animateMs); } catch (InterruptedException ignored) {}
            }
            renderGrid(out, grid, on, off);
        }
    }

    public static String renderText(int[][] grid, char on, char off) {
        StringBuilder sb = new StringBuilder(grid.length * (grid[0].length + 1));
        for (int[] row : grid) {
            for (int cell : row) sb.append(cell == ALIVE ? on : off);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void renderGrid(java.io.PrintStream out, int[][] grid, char on, char off) {
        out.print(renderText(grid, on, off));
    }

    private static int[][] copyGrid(int[][] src) {
        int h = src.length, w = src[0].length;
        int[][] dst = new int[h][w];
        for (int y = 0; y < h; y++) System.arraycopy(src[y], 0, dst[y], 0, w);
        return dst;
    }

    private static void stampCentered(int[][] g, int[][] pattern) {
        int ph = pattern.length, pw = pattern[0].length;
        int startY = Math.max(0, (g.length - ph) / 2);
        int startX = Math.max(0, (g[0].length - pw) / 2);
        for (int y = 0; y < ph; y++) {
            for (int x = 0; x < pw; x++) {
                int yy = startY + y, xx = startX + x;
                if (yy >= 0 && yy < g.length && xx >= 0 && xx < g[0].length) {
                    g[yy][xx] = (pattern[y][x] != 0) ? ALIVE : DEAD;
                }
            }
        }
    }

    public int[][] runToLast() {
        int[][] grid = copyGrid(initial);
        for (int t = 1; t < steps; t++) grid = evolveOnce(grid);
        return grid;
    }
}
