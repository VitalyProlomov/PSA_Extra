package pokerlibrary.analizer;

import lombok.Getter;
import lombok.Setter;


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

        @Setter
        @Getter
        public double specifiedValue;

        Combination(int value) {
            this.value = value;
        }

}
