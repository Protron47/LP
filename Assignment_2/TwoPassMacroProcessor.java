import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Two-pass macro processor (Pass-1 writer).
 * - Pass1: build MNT, MDT, KPDTAB, ALA templates and produce IC (intermediate code)
 *   and write the outputs to external files so Pass-2 can read them.
 *
 * Usage: put input.asm in same folder and run.
 */
public class TwoPassMacroProcessor {

    // Data structures
    static class MNTEntry {
        String name;
        int mdtIndex; // 1-based index into MDT
        int pp; // positional param count
        int kp; // keyword param count
        Integer kpdtIndex; // start index in KPDTAB (1-based) or null
        MNTEntry(String name, int mdtIndex, int pp, int kp, Integer kpdtIndex) {
            this.name = name; this.mdtIndex = mdtIndex; this.pp = pp; this.kp = kp; this.kpdtIndex = kpdtIndex;
        }
    }

    static Map<String, MNTEntry> MNT = new LinkedHashMap<>();
    static List<String> MDT = new ArrayList<>();
    static List<String> KPDTAB = new ArrayList<>(); // store as "&TEMP=R0"
    static Map<String, List<String>> ALA_templates = new LinkedHashMap<>(); // preserve order
    static List<String> IC = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String inputFile = "input.asm";
        if (args.length > 0) inputFile = args[0];

        List<String> lines = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        pass1(lines);
        printPass1Outputs();
        writePass1Files(); // write MNT.txt, MDT.txt, KPDTAB.txt, ALA.txt, IC.txt
        System.out.println("\nPass-1 files written: MNT.txt, MDT.txt, KPDTAB.txt, ALA.txt, IC.txt");
    }

    // PASS-1: Build MNT, MDT, KPDTAB, ALA templates, and IC (remove macro definitions)
    static void pass1(List<String> lines) {
        int i = 0;
        int mdtIndex = 1; // 1-based for reporting
        while (i < lines.size()) {
            String ln = lines.get(i);
            String[] tokens = ln.split("\\s+");
            if (tokens.length > 0 && tokens[0].equals("MACRO")) {
                // Expect format: "MACRO NAME &p1,&p2=def"
                String header = ln;
                String[] parts = header.split("\\s+", 3);
                if (parts.length < 2) {
                    throw new RuntimeException("Invalid MACRO header: " + header);
                }
                String name = parts[1].trim();
                String paramsPart = "";
                if (parts.length >= 3) paramsPart = parts[2].trim();

                // split params by comma (could be empty)
                List<String> params = new ArrayList<>();
                if (!paramsPart.isEmpty()) {
                    String[] ps = paramsPart.split(",");
                    for (String p : ps) {
                        if (!p.trim().isEmpty()) params.add(p.trim());
                    }
                }

                int pp = 0, kp = 0;
                Integer kpdtStartIndex = null; // 1-based
                List<String> formals = new ArrayList<>();
                for (String p : params) {
                    if (p.contains("=")) {
                        // keyword param (store entire "&NAME=DEF" in KPDTAB)
                        kp++;
                        if (kpdtStartIndex == null) kpdtStartIndex = KPDTAB.size() + 1; // 1-based start
                        KPDTAB.add(p); // store like "&TEMP=R0"
                        formals.add(p.split("=")[0]); // store just &TEMP as formal
                    } else {
                        pp++;
                        formals.add(p);
                    }
                }

                // record in MNT
                MNT.put(name, new MNTEntry(name, mdtIndex, pp, kp, kpdtStartIndex));
                ALA_templates.put(name, formals);

                // read macro body until MEND and store into MDT replacing &formal with #pos
                i++; // move to first line of macro body
                while (i < lines.size() && !lines.get(i).equals("MEND")) {
                    String bodyLine = lines.get(i);
                    // replace occurrences of formal names (like &ARG1) with positional markers #1, #2...
                    for (int f = 0; f < formals.size(); f++) {
                        String formal = formals.get(f); // e.g., "&ARG1" or "&TEMP"
                        String posToken = "#" + (f + 1);
                        bodyLine = bodyLine.replace(formal, posToken);
                    }
                    MDT.add(bodyLine);
                    mdtIndex++;
                    i++;
                }
                // add MEND marker in MDT
                MDT.add("MEND");
                mdtIndex++;
                i++; // skip MEND line
            } else {
                // normal line or macro call -> copy to IC
                IC.add(ln);
                i++;
            }
        }
    }

    static void printPass1Outputs() {
        System.out.println("\n--- PASS-1 OUTPUTS ---\n");

        // MNT
        System.out.println("MNT (Macro Name Table):");
        System.out.printf("%-10s %-8s %-4s %-4s %-8s%n", "Macro", "MDTIdx", "PP", "KP", "KPDTIdx");
        for (MNTEntry e : MNT.values()) {
            System.out.printf("%-10s %-8d %-4d %-4d %-8s%n", e.name, e.mdtIndex, e.pp, e.kp,
                    (e.kpdtIndex == null ? "-" : e.kpdtIndex.toString()));
        }

        // MDT
        System.out.println("\nMDT (Macro Definition Table):");
        int idx = 1;
        for (String s : MDT) {
            System.out.printf("%3d : %s%n", idx++, s);
        }

        // KPDTAB
        System.out.println("\nKPDTAB (Keyword Parameter Default Table):");
        if (KPDTAB.isEmpty()) {
            System.out.println(" (empty)");
        } else {
            int kindex = 1;
            for (String kp : KPDTAB) {
                System.out.printf("%3d : %s%n", kindex++, kp);
            }
        }

        // ALA templates (formal->position)
        System.out.println("\nALA Templates (formal parameters -> positional #):");
        for (Map.Entry<String, List<String>> entry : ALA_templates.entrySet()) {
            System.out.println("Macro: " + entry.getKey());
            List<String> formals = entry.getValue();
            for (int i = 0; i < formals.size(); i++) {
                System.out.printf("  #%d -> %s%n", i + 1, formals.get(i));
            }
        }

        // IC
        System.out.println("\nIC (Intermediate Code):");
        int lno = 1;
        for (String s : IC) {
            System.out.printf("%3d : %s%n", lno++, s);
        }
    }

    // Write Pass-1 outputs to files for Pass-2
    static void writePass1Files() throws IOException {
        Path base = Paths.get("."); // current directory

        // MNT.txt: MacroName MDTIndex PP KP KPDTIdx (use '-' when null)
        List<String> mntLines = new ArrayList<>();
        for (MNTEntry e : MNT.values()) {
            String kpIdxStr = (e.kpdtIndex == null) ? "-" : e.kpdtIndex.toString();
            mntLines.add(String.join(" ", e.name, Integer.toString(e.mdtIndex),
                    Integer.toString(e.pp), Integer.toString(e.kp), kpIdxStr));
        }
        Files.write(base.resolve("MNT.txt"), mntLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // MDT.txt: one MDT entry per line (MDT index = line number)
        Files.write(base.resolve("MDT.txt"), MDT, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // KPDTAB.txt: one entry per line (like &TEMP=R0)
        Files.write(base.resolve("KPDTAB.txt"), KPDTAB, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // ALA.txt: MacroName:formal1,formal2,...
        List<String> alaLines = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : ALA_templates.entrySet()) {
            String name = e.getKey();
            List<String> formals = e.getValue();
            String joined = String.join(",", formals);
            alaLines.add(name + ":" + joined);
        }
        Files.write(base.resolve("ALA.txt"), alaLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // IC.txt: intermediate code lines
        Files.write(base.resolve("IC.txt"), IC, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
