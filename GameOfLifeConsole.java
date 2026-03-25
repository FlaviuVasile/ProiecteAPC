public final class GameOfLifeConsole {
    private static void usage() {
        System.out.println("Usage:\n" +
                " -W, --width <int>      (default 80)\n" +
                " -H, --height <int>     (default 30)\n" +
                " -s, --steps <int>      (default 200)\n" +
                " --wrap                 torus boundary (default false)\n" +
                " --random-seed <long>   random initialization\n" +
                " --density <0..1>       random density (default 0.15)\n" +
                " --pattern <name>       one of: glider, blinker, block\n" +
                " --on <char>            live cell char (default █)\n" +
                " --off <char>           dead cell char (default space)\n" +
                " --animate <ms>         delay between steps (default 0)\n");
    }

    private static void die(String msg) { System.err.println(msg); System.exit(1); }

    private static final int[][] GLIDER = {
            {0,1,0},
            {0,0,1},
            {1,1,1}
    };

    private static final int[][] BLINKER = {
            {1,1,1}
    };

    private static final int[][] BLOCK_2x2 = {
            {1,1},
            {1,1}
    };

    public static void main(String[] args) {
        int width = 80, height = 30, steps = 200; boolean wrap = false;
        Long seed = null; double density = 0.15; String pattern = null;
        char onChar = '█', offChar = ' ';
        long animateMs = 0;

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            try {
                switch (a) {
                    case "-W": case "--width":  width = Integer.parseInt(args[++i]); break;
                    case "-H": case "--height": height = Integer.parseInt(args[++i]); break;
                    case "-s": case "--steps":  steps = Integer.parseInt(args[++i]); break;
                    case "--wrap": wrap = true; break;
                    case "--random-seed": seed = Long.parseLong(args[++i]); break;
                    case "--density": density = Double.parseDouble(args[++i]); break;
                    case "--pattern": pattern = args[++i].toLowerCase(); break;
                    case "--on": onChar = args[++i].charAt(0); break;
                    case "--off": offChar = args[++i].charAt(0); break;
                    case "--animate": animateMs = Long.parseLong(args[++i]); break;
                    case "-h": case "--help": usage(); return;
                    default: die("Unknown option: " + a);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                die("Missing value for option: " + a);
            } catch (NumberFormatException e) {
                die("Invalid number for option " + a + ": " + e.getMessage());
            }
        }

        if (width <= 0 || height <= 0 || steps <= 0) die("width/height/steps must be > 0");
        if (density < 0.0 || density > 1.0) die("--density must be in [0,1]");

        System.out.printf("Config: %dx%d, steps=%d, wrap=%b, init=%s, density=%.3f, seed=%s, pattern=%s%n",
                width, height, steps, wrap,
                (pattern != null ? "pattern" : (seed == null ? "empty" : "random")),
                density, (seed == null ? "—" : seed.toString()), (pattern == null ? "—" : pattern));

        GameOfLife life;
        if (pattern != null) {
            int[][] p;
            switch (pattern) {
                case "glider":  p = GLIDER; break;
                case "blinker": p = BLINKER; break;
                case "block":   p = BLOCK_2x2; break;
                default: die("Unknown pattern: " + pattern); return;
            }
            life = GameOfLife.withPatternCentered(width, height, steps, wrap, p);
        } else if (seed != null) {
            life = GameOfLife.randomInit(width, height, steps, wrap, seed, density);
        } else {
            life = GameOfLife.empty(width, height, steps, wrap);
        }

        life.runStreaming(System.out, onChar, offChar, animateMs);
    }
}
