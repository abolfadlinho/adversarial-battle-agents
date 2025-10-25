//src/test/java/tests/BattleGameChecker.java

package tests;

import java.util.ArrayList;

public class BattleGameChecker {

    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;
        public final int finalScore;

        public ValidationResult(boolean v, String m, int s) {
            this.isValid = v;
            this.errorMessage = m;
            this.finalScore = s;
        }

        public ValidationResult(boolean v, String m) {
            this(v, m, 0);
        }
    }

    public static ValidationResult validateSolution(String s0, String s1, int s2, boolean cs) {
        try {
            if (s1.equals("NOSOLUTION")) {
                return new ValidationResult(true, "No solution exists", 0);
            }

            String[] a0 = s1.split(";");
            if (a0.length != 3) {
                return new ValidationResult(false, "Solution format incorrect - should be plan;score;nodesExpanded");
            }

            String p = a0[0];
            b s = x(s0);

            if (p.isEmpty()) {
                if (s.t()) {
                    int i0 = s.g();
                    return new ValidationResult(i0 == s2,
                            "Score mismatch: expected " + s2 + ", got " + i0, i0);
                } else {
                    return new ValidationResult(false, "Empty plan but state is not terminal");
                }
            }
            p = p.replaceAll(",A","-A");
            p = p.replaceAll(",B","-B");

            String[] a1 = p.split("-");

            for (int i = 0; i < a1.length; i++) {
                String as = a1[i].trim();

                if (!y(as)) {
                    return new ValidationResult(false, "Invalid action format: " + as);
                }

                ArrayList<String> pa = s.p();
                if (!pa.contains(as)) {
                    return new ValidationResult(false, "Invalid action at step " + (i+1) + ": " + as +
                            " not in possible actions: " + pa);
                }

                s = s.a(as);
                if (s == null) {
                    return new ValidationResult(false, "Failed to apply action: " + as);
                }
            }

            if (!s.t()) {
                return new ValidationResult(false, "Final state is not terminal");
            }

            int f0 = s.g();
            boolean m = f0 == s2;

            if (!cs){
                return new ValidationResult(true,
                        "Valid Plan. No score check",
                        f0);
            }

            return new ValidationResult(m,
                    m ? "Valid solution" : "Score mismatch: expected " + s2 + ", got " + f0,
                    f0);

        } catch (Exception e) {
            return new ValidationResult(false, "Exception during validation: " + e.getMessage());
        }
    }

    private static boolean y(String a) {
        return a.matches("[AB]\\(\\d+,\\d+\\)");
    }

    private static b x(String s) {
        String[] p = s.split(";");
        char t = p[2].charAt(0);

        ArrayList<Integer> h0 = new ArrayList<>();
        ArrayList<Integer> d0 = new ArrayList<>();
        String[] t0 = p[0].split(",");
        for (int i = 0; i < t0.length; i += 2) {
            h0.add(Integer.parseInt(t0[i]));
            d0.add(Integer.parseInt(t0[i + 1]));
        }

        ArrayList<Integer> h1 = new ArrayList<>();
        ArrayList<Integer> d1 = new ArrayList<>();
        String[] t1 = p[1].split(",");
        for (int i = 0; i < t1.length; i += 2) {
            h1.add(Integer.parseInt(t1[i]));
            d1.add(Integer.parseInt(t1[i + 1]));
        }

        return new b(h0, d0, h1, d1, t);
    }

    private static class b {
        ArrayList<Integer> h0, d0, h1, d1;
        char t;

        b(ArrayList<Integer> h0, ArrayList<Integer> d0,
          ArrayList<Integer> h1, ArrayList<Integer> d1, char t) {
            this.h0 = new ArrayList<>(h0);
            this.d0 = new ArrayList<>(d0);
            this.h1 = new ArrayList<>(h1);
            this.d1 = new ArrayList<>(d1);
            this.t = t;
        }

        ArrayList<String> p() {
            ArrayList<String> a = new ArrayList<>();

            if (t == 'A') {
                for (int i = 0; i < h0.size(); i++) {
                    if (h0.get(i) > 0) {
                        for (int j = 0; j < h1.size(); j++) {
                            if (h1.get(j) > 0) {
                                a.add("A(" + i + "," + j + ")");
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < h1.size(); i++) {
                    if (h1.get(i) > 0) {
                        for (int j = 0; j < h0.size(); j++) {
                            if (h0.get(j) > 0) {
                                a.add("B(" + i + "," + j + ")");
                            }
                        }
                    }
                }
            }

            return a;
        }

        b a(String ac) {
            b n = new b(h0, d0, h1, d1, t);

            String pr = ac.substring(2, ac.length() - 1);
            String[] pt = pr.split(",");
            int at = Integer.parseInt(pt[0]);
            int tg = Integer.parseInt(pt[1]);

            if (ac.startsWith("A(")) {
                if (t != 'A' || h0.get(at) <= 0 || h1.get(tg) <= 0) {
                    return null;
                }
                int dm = d0.get(at);
                n.h1.set(tg, Math.max(0, h1.get(tg) - dm));
                n.t = 'B';
            } else if (ac.startsWith("B(")) {
                if (t != 'B' || h1.get(at) <= 0 || h0.get(tg) <= 0) {
                    return null;
                }
                int dm = d1.get(at);
                n.h0.set(tg, Math.max(0, h0.get(tg) - dm));
                n.t = 'A';
            } else {
                return null;
            }

            return n;
        }

        boolean t() {
            int s0 = h0.stream().mapToInt(Integer::intValue).sum();
            int s1 = h1.stream().mapToInt(Integer::intValue).sum();
            return s0 == 0 || s1 == 0;
        }

        int g() {
            int s0 = h0.stream().mapToInt(Integer::intValue).sum();
            int s1 = h1.stream().mapToInt(Integer::intValue).sum();

            if (s0 == 0) return -s1;
            if (s1 == 0) return s0;
            return 0;
        }
    }
}