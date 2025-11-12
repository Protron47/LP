import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Pass2MacroProcessor {

    static class MNTEntry {
        String name;
        int mdtIndex; // 1-based
        int pp; // positional count
        int kp; // keyword count
        Integer kpdtIndex; // 1-based or null
        MNTEntry(String n,int m,int p,int k,Integer kidx){ name=n;mdtIndex=m;pp=p;kp=k;kpdtIndex=kidx; }
    }

    public static void main(String[] args) throws Exception {
        String dir = "."; // current folder
        if (args.length > 0) dir = args[0];

        Map<String, MNTEntry> MNT = readMNT(Paths.get(dir,"MNT.txt"));
        List<String> MDT = readLinesSafe(Paths.get(dir,"MDT.txt"));
        List<String> KPDTAB = readLinesSafe(Paths.get(dir,"KPDTAB.txt"));
        Map<String,List<String>> ALA_templates = readALA(Paths.get(dir,"ALA.txt"));
        List<String> IC = readLinesSafe(Paths.get(dir,"IC.txt"));

        List<String> expanded = new ArrayList<>();

        for (String line : IC) {
            if (line.trim().isEmpty()) { expanded.add(line); continue; }
            String[] parts = line.trim().split("\\s+",2);
            String first = parts[0];
            if (MNT.containsKey(first)) {
                // macro call
                MNTEntry me = MNT.get(first);
                String argsPart = (parts.length>1)?parts[1].trim():"";
                List<String> actualArgs = splitArgs(argsPart);

                // build ALA for this call: #1->value
                Map<String,String> ALA_call = new HashMap<>();

                // load defaults from KPDTAB if any
                if (me.kp>0 && me.kpdtIndex!=null) {
                    int kpStart = me.kpdtIndex -1; // 0-based
                    for (int i=0;i<me.kp && (kpStart+i) < KPDTAB.size(); i++) {
                        String entry = KPDTAB.get(kpStart+i).trim(); // e.g. &TEMP=R0
                        if (entry.isEmpty()) continue;
                        String[] kv = entry.split("=",2);
                        String pname = kv[0].trim();
                        String pdef = (kv.length>1)?kv[1].trim():"";
                        // find position
                        List<String> formals = ALA_templates.get(first);
                        int pos = formals.indexOf(pname);
                        if (pos>=0) ALA_call.put("#"+(pos+1), pdef);
                    }
                }

                // assign actuals: positional then keyword
                List<String> formals = ALA_templates.get(first);
                int posIndex=1;
                for (String a : actualArgs) {
                    if (a.contains("=")) {
                        String[] kv = a.split("=",2);
                        String k = kv[0].trim();
                        String v = (kv.length>1)?kv[1].trim():"";
                        // try with & prefix and without
                        int pos = formals.indexOf(k);
                        if (pos<0) pos = formals.indexOf("&"+k);
                        if (pos>=0) ALA_call.put("#"+(pos+1), v);
                    } else {
                        ALA_call.put("#"+posIndex, a);
                        posIndex++;
                    }
                }

                // Expand MDT from mdtIndex until MEND
                int idx = me.mdtIndex - 1; // 0-based
                while (idx < MDT.size()) {
                    String mdline = MDT.get(idx);
                    if (mdline.trim().equals("MEND")) break;
                    String out = mdline;
                    // replace tokens #1..#n with actuals
                    if (formals!=null) {
                        for (int f=0; f<formals.size(); f++) {
                            String key = "#" + (f+1);
                            String val = ALA_call.getOrDefault(key, key);
                            out = out.replace(key, val);
                        }
                    }
                    expanded.add(out);
                    idx++;
                }

            } else {
                // not a macro call
                expanded.add(line);
            }
        }

        // Print expanded program and write to file
        System.out.println("--- Expanded Program (Pass-2) ---");
        int ln=1;
        for (String s : expanded) System.out.printf("%3d : %s\n", ln++, s);

        // also save to file Expanded.out
        Path outp = Paths.get(dir,"Expanded.out");
        Files.write(outp, expanded, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("\nSaved expanded output to: " + outp.toAbsolutePath());
    }

    static Map<String,MNTEntry> readMNT(Path p) throws IOException {
        Map<String,MNTEntry> map = new LinkedHashMap<>();
        List<String> lines = readLinesSafe(p);
        for (String l : lines) {
            if (l.trim().isEmpty()) continue;
            // accept both space/tab separated and also if line has commas
            String[] tok = l.trim().split("\\s+");
            if (tok.length < 5) {
                // try comma or other
                tok = l.trim().split("[,\\s]+");
            }
            if (tok.length < 5) continue; // skip invalid line
            String name = tok[0].trim();
            int mdtIdx = Integer.parseInt(tok[1].trim());
            int pp = Integer.parseInt(tok[2].trim());
            int kp = Integer.parseInt(tok[3].trim());
            Integer kpIdx = null;
            if (!tok[4].trim().equals("-")) kpIdx = Integer.parseInt(tok[4].trim());
            map.put(name, new MNTEntry(name,mdtIdx,pp,kp,kpIdx));
        }
        return map;
    }

    static List<String> readLinesSafe(Path p) throws IOException {
        if (!Files.exists(p)) return new ArrayList<>();
        return Files.readAllLines(p).stream().map(String::trim).collect(Collectors.toList());
    }

    static Map<String,List<String>> readALA(Path p) throws IOException {
        Map<String,List<String>> map = new HashMap<>();
        List<String> lines = readLinesSafe(p);
        for (String l : lines) {
            if (l.isEmpty()) continue;
            // format: MACRONAME: &A,&B,&TEMP
            int colon = l.indexOf(':');
            if (colon<0) continue;
            String name = l.substring(0,colon).trim();
            String rest = l.substring(colon+1).trim();
            if (rest.isEmpty()) {
                map.put(name, new ArrayList<>());
            } else {
                String[] toks = rest.split(",");
                List<String> formals = new ArrayList<>();
                for (String t: toks) formals.add(t.trim());
                map.put(name, formals);
            }
        }
        return map;
    }

    static List<String> splitArgs(String s) {
        List<String> out = new ArrayList<>();
        if (s==null || s.trim().isEmpty()) return out;
        // simple split by comma (no support for commas inside args)
        String[] parts = s.split(",");
        for (String p: parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}
