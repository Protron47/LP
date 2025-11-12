import java.io.*;
import java.util.*;

public class LRU {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int frames, hit = 0, fault = 0, ref_len;
        String[] buffer, reference;
        String mem_layout[][];
        char status[];  // F for fault, H for hit

        System.out.println("Please enter the number of Frames: ");
        frames = Integer.parseInt(br.readLine());

        System.out.println("Please enter the length of the Reference string: ");
        ref_len = Integer.parseInt(br.readLine());

        reference = new String[ref_len];
        mem_layout = new String[ref_len][frames];
        buffer = new String[frames];
        status = new char[ref_len];

        for(int j = 0; j < frames; j++)
            buffer[j] = "-"; // empty frame

        System.out.println("Please enter the reference string (separated by spaces): ");
        String[] inputs = br.readLine().split("\\s+");
        for(int i = 0; i < ref_len; i++)
            reference[i] = inputs[i];

        ArrayList<String> stack = new ArrayList<String>(); // to keep track of LRU order

        for(int i = 0; i < ref_len; i++) {

            // Update stack: remove if already present, then add to end
            stack.remove(reference[i]);
            stack.add(reference[i]);

            int search = -1;
            for(int j = 0; j < frames; j++) {
                if(buffer[j].equals(reference[i])) {
                    search = j;
                    hit++;
                    status[i] = 'H';
                    break;
                }
            }

            if(search == -1) { // Page fault
                // Find LRU frame to replace
                int pointer = 0;
                int min_loc = Integer.MAX_VALUE;
                for(int j = 0; j < frames; j++) {
                    if(buffer[j].equals("-")) { // empty frame
                        pointer = j;
                        break;
                    }
                    int idx = stack.indexOf(buffer[j]);
                    if(idx < min_loc) {
                        min_loc = idx;
                        pointer = j;
                    }
                }
                buffer[pointer] = reference[i];
                fault++;
                status[i] = 'F';
            }

            // Update memory layout
            for(int j = 0; j < frames; j++)
                mem_layout[i][j] = buffer[j];
        }

        // Print memory layout
        System.out.println("Memory Layout:");
        for(int i = 0; i < frames; i++) {
            for(int j = 0; j < ref_len; j++)
                System.out.printf("%3s ", mem_layout[j][i]);
            System.out.println();
        }

        // Print F/H status
        System.out.println("F/H Status:");
        for(int i = 0; i < ref_len; i++)
            System.out.printf("%3c ", status[i]);
        System.out.println();

        // Summary
        System.out.println("The number of Hits: " + hit);
        System.out.println("The number of Faults: " + fault);
    }
}
