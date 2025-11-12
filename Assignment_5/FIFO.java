import java.io.*;

public class FIFO {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int frames, pointer = 0, hit = 0, fault = 0, ref_len;
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

        System.out.println();

        for(int i = 0; i < ref_len; i++) {
            int search = -1;
            for(int j = 0; j < frames; j++) {
                if(buffer[j].equals(reference[i])) { // Hit condition
                    search = j;
                    hit++;
                    status[i] = 'H';
                    break;
                }
            }

            if(search == -1) { // Fault condition
                buffer[pointer] = reference[i];
                fault++;
                status[i] = 'F';
                pointer++;
                if(pointer == frames)
                    pointer = 0;
            }

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

        // Print F/H line
        System.out.println("F/H Status:");
        for(int i = 0; i < ref_len; i++)
            System.out.printf("%3c ", status[i]);
        System.out.println();

        // Print summary (without hit ratio)
        System.out.println("The number of Hits: " + hit);
        System.out.println("The number of Faults: " + fault);
    }
}
