package utils;

public class Constants {
    public static class L0 { // Layer 0
        public static class PORT {
            public final static int
                    A1 = 16889,
                    A2 = A1 + 1,
                    A3 = A2 + 1;
        }
        public static class ID {
            public final static String
                    A1 = "A1",
                    A2 = "A2",
                    A3 = "A3";
        }
        public final static int NUM_NODES = 3;
    }

    public static class L1 { // Layer 1
        public static class PORT {
            public final static int
                    B1 = 16989,
                    B2 = B1 + 1;
        }
        public static class ID {
            public final static String
                    B1 = "B1",
                    B2 = "B2";
        }
        public final static int NUM_NODES = 2;
    }

    public static class L2 { // Layer 2
        public static class PORT {
            public final static int
                    C1 = 16999,
                    C2 = C1 + 1;
        }
        public static class ID {
            public final static String
                    C1 = "C1",
                    C2 = "C2";
        }
        public final static int NUM_NODES = 2;
    }

}
