import java.io.*;
import java.util.*;

public class OPT{

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

        boolean isFull = false;
        int pointer = 0;

        for(int i = 0; i < ref_len; i++) {
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
                if(isFull) {
                    int[] nextUse = new int[frames];
                    Arrays.fill(nextUse, Integer.MAX_VALUE);

                    // Calculate next use of each page in buffer
                    for(int j = 0; j < frames; j++) {
                        for(int k = i + 1; k < ref_len; k++) {
                            if(buffer[j].equals(reference[k])) {
                                nextUse[j] = k;
                                break;
                            }
                        }
                    }

                    // Find page with farthest next use
                    int maxIndex = 0;
                    for(int j = 1; j < frames; j++) {
                        if(nextUse[j] > nextUse[maxIndex]) {
                            maxIndex = j;
                        }
                    }
                    pointer = maxIndex;
                }

                buffer[pointer] = reference[i];
                fault++;
                status[i] = 'F';

                if(!isFull) {
                    pointer++;
                    if(pointer == frames) isFull = true;
                }
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
