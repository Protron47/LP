/* Pass-II of Two Pass Assembler in Java */
import java.io.*;
import java.util.HashMap;

public class Pass2 {
    public static void main(String[] args) throws Exception {
        System.out.println("Nipun Savkare 31056 T2 Batch");

        BufferedReader br  = new BufferedReader(new FileReader("IC.txt")); // IC
        BufferedReader b2  = new BufferedReader(new FileReader("symtab.txt"));       // SYMTAB
        BufferedReader b3  = new BufferedReader(new FileReader("littab.txt"));       // LITTAB
        BufferedWriter f1  = new BufferedWriter(new FileWriter("machinecode2.txt"));

        HashMap<Integer, String> symAddr = new HashMap<>();
        HashMap<Integer, String> litAddr = new HashMap<>();

        String s;
        int symtabPointer = 1, littabPointer = 1;

        // Load SYMTAB
        while ((s = b2.readLine()) != null) {
            String[] word = s.trim().split("\\s+");
            if (word.length >= 3) {
                symAddr.put(symtabPointer++, word[2]); // FIX: use address, not symbol name
            }
        }

        // Load LITTAB
        while ((s = b3.readLine()) != null) {
            String[] word = s.trim().split("\\s+");
            if (word.length >= 3) {
                litAddr.put(littabPointer++, word[2]); // FIX: use address
            }
        }

        // Process IC
        while ((s = br.readLine()) != null) {
            s = s.trim();
            if (s.isEmpty()) continue;

            if (s.contains("(IS,00)")) {
                f1.write("00 00 000\n"); // STOP
            }
            else if (s.contains("(IS,")) {
                // Example: (IS,04) (RG,1) (S,03)
                String[] parts = s.split("\\s+");
                String opcode = parts[0].substring(4, 6); // from (IS,04)

                String reg = "00";
                String addr = "000";

                for (String p : parts) {
                    if (p.startsWith("(RG,")) {
                        reg = String.format("%02d", Integer.parseInt(p.substring(4, p.length()-1)));
                    }
                    else if (p.startsWith("(S,")) {
                        int idx = Integer.parseInt(p.substring(3, p.length()-1));
                        addr = String.format("%03d", Integer.parseInt(symAddr.get(idx)));
                    }
                    else if (p.startsWith("(L,")) {
                        int idx = Integer.parseInt(p.substring(3, p.length()-1));
                        addr = String.format("%03d", Integer.parseInt(litAddr.get(idx)));
                    }
                    else if (p.startsWith("(C,")) {
                        addr = String.format("%03d", Integer.parseInt(p.substring(3, p.length()-1)));
                    }
                }
                f1.write(opcode + " " + reg + " " + addr + "\n");
            }
            else if (s.contains("(DL,01)")) {
                // DC constant
                String num = s.substring(s.indexOf("(C,")+3, s.length()-1);
                String addr = String.format("%03d", Integer.parseInt(num));
                f1.write("00 00 " + addr + "\n");
            }
            // Skip AD (assembler directives)
        }

        f1.close();
        br.close();
        b2.close();
        b3.close();

        System.out.println("Pass-2 complete. Machine code in machinecode2.txt");
    }
}
