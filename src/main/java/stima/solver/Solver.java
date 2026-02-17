package stima.solver;

public class Solver {
    public record SolverStatus(long iterations, long timeMs, boolean finished, String message) {}

    public enum AlgorithmType {
        BRUTE_FORCE("Brute Force Kombinasi Petak"),
        OPTIMIZED_BRUTE_FORCE("Brute Force Per Wilayah");
        private final String label;
        AlgorithmType(String label) {
            this.label = label;
        }
        @Override 
        public String toString() {
            return label;
        }
    }
}