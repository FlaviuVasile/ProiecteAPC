public final class ECAConsole {
    private static void usage() {
        System.out.println("Usage:\n" +
                " -r, --rule <0..255> (required)\n" +
                " -s, --steps <int> (default 100)\n" +
                " -w, --width <int> (default 121)\n" +
                " --wrap                 periodic boundaries\n" +
                " --random-seed <long>   random initial row\n" +
                " --density <0..1>       density for random init (default 0.5)\n");
    }

    public static void main(String[] args) {
        Integer ruleVal = null; int steps = 100; int width = 121; boolean wrap = false;
        Long seed = null; double density = 0.5;

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            try {
                switch (a) {
                    case "-r": case "--rule": ruleVal = Integer.parseInt(args[++i]); break;
                    case "-s": case "--steps": steps = Integer.parseInt(args[++i]); break;
                    case "-w": case "--width": width = Integer.parseInt(args[++i]); break;
                    case "--wrap": wrap = true; break;
                    case "--random-seed": seed = Long.parseLong(args[++i]); break;
                    case "--density": density = Double.parseDouble(args[++i]); break;
                    case "-h": case "--help": usage(); return;
                    default: System.err.println("Unknown option: " + a); usage(); return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Missing value for option: " + a);
                usage(); return;
            } catch (NumberFormatException e) {
                System.err.println("Invalid number for option " + a + ": " + e.getMessage());
                usage(); return;
            }
        }

        if (ruleVal == null) { System.err.println("--rule is required"); usage(); return; }
        if (steps <= 0) { System.err.println("--steps must be > 0"); return; }
        if (width <= 0) { System.err.println("--width must be > 0"); return; }
        if (density < 0.0 || density > 1.0) { System.err.println("--density must be in [0,1]"); return; }

        System.out.printf("Config: rule=%d, width=%d, steps=%d, wrap=%b, init=%s, density=%.3f, seed=%s%n",
                ruleVal, width, steps, wrap, (seed==null?"center":"random"), density, (seed==null?"—":seed.toString()));

        Rule rule = new Rule(ruleVal);
        Automaton automaton = (seed != null)
                ? Automaton.randomInit(width, steps, rule, wrap, seed, density)
                : Automaton.singleCenter(width, steps, rule, wrap);

        int[][] hist = automaton.run();
        String art = Automaton.renderText(hist, '█', ' ');
        System.out.print(art);
    }
}
