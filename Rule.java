public final class Rule {
    private final int value;

    public Rule(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Rule must be in [0,255], got " + value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int nextState(int left, int center, int right) {
        int idx = (left << 2) | (center << 1) | right;
        return (value >> idx) & 1;
    }
}
