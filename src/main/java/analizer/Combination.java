package analizer;
    /**
     * All possible combinations in Texas Holdem
     */
    public enum Combination {
        HIGH_CARD(0),
        PAIR(1),
        TWO_PAIRS(2),
        SET(3),
        STRAIGHT(4),
        FLUSH(5),
        FULL_HOUSE(6),
        QUADS(7),
        STRAIGHT_FLUSH(8),
        FLUSH_ROYAL(9);

        public final int value;

        public double specifiedValue;

        Combination(int value) {
            this.value = value;
        }

        public double getSpecifiedValue() {
            return specifiedValue;
        }

        public void setSpecifiedValue(double specifiedValue) {
            this.specifiedValue = specifiedValue;
        }
    }
