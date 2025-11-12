// Import "Scanner" for user input
import java.util.Scanner;
// Import "Arrays" to fill our buffer and our 'nextUse' array
import java.util.Arrays;

// This is our "Optimal" (psychic) program
public class OPT {
    public static void main(String[] args) {
        
        // --- 1. SETUP VARIABLES ---
        
        Scanner sc = new Scanner(System.in);
        int frames, hit = 0, fault = 0, ref_len;
        String[] buffer;    // Our "desk slots"
        String[] reference; // Our "homework list"
        char status[];      // Our 'H'/'F' list for output

        // --- 2. GET USER INPUT ---
        
        System.out.println("Please enter the number of Frames: ");
        frames = sc.nextInt();

        System.out.println("Please enter the length of the Reference string: ");
        ref_len = sc.nextInt();
        sc.nextLine(); // Consume the leftover newline

        // Create the arrays
        reference = new String[ref_len];
        buffer = new String[frames];
        status = new char[ref_len];

        // Mark all desk slots as empty
        Arrays.fill(buffer, "-");

        System.out.println("Please enter the reference string (separated by spaces): ");
        String[] inputs = sc.nextLine().split("\\s+");
        // Copy inputs to our reference list
        for (int i = 0; i < ref_len; i++) {
            reference[i] = inputs[i];
        }
        System.out.println();

        // 'isFull' tracks if we've filled all the empty slots at the start
        boolean isFull = false;
        // 'pointer' tracks the next empty slot to fill *before* the desk is full
        int pointer = 0;

        // --- 3. MAIN ALGORITHM LOOP ---
        
        // Loop through each homework item 'i'
        for (int i = 0; i < ref_len; i++) {
            
            // --- 4. HIT CHECK ---
            
            int search = -1; // -1 means "Not Found"
            // Loop through the desk slots
            for (int j = 0; j < frames; j++) {
                // Is the book in this slot the one we need?
                if (buffer[j].equals(reference[i])) {
                    // YES! This is a HIT.
                    search = j;
                    hit++;
                    status[i] = 'H';
                    break; // Stop searching the desk
                }
            }

            // --- 5. FAULT LOGIC (The "OPT" Part) ---
            
            // After checking, was 'search' still -1?
            if (search == -1) { 
                // YES. This is a PAGE FAULT.
                fault++;
                status[i] = 'F';

                // First, check if our desk is full (have we filled all the '-' spots?)
                if (isFull) {
                    // --- THIS IS THE CORE "PSYCHIC" OPT LOGIC ---
                    // The desk is full. We must replace a book.
                    // We need to find the book on our desk that is used
                    // FARTHEST in the *future* homework list.
                    
                    // Create a list to "score" the books on our desk
                    int[] nextUse = new int[frames];
                    
                    // Assume all books on the desk are NEVER used again (infinity)
                    Arrays.fill(nextUse, Integer.MAX_VALUE);

                    // Loop through each book 'j' *on our desk*
                    for (int j = 0; j < frames; j++) {
                        // Scan the *future* homework list (from 'k = i + 1')
                        for (int k = i + 1; k < ref_len; k++) {
                            // If the book on our desk (buffer[j]) matches this future book...
                            if (buffer[j].equals(reference[k])) {
                                // ...we found its next use! Save the time 'k'.
                                nextUse[j] = k;
                                break; // Stop scanning the future for *this* book.
                            }
                        }
                    } // After this, 'nextUse' holds the future time for each desk book

                    // Now, find the book with the *biggest* (farthest) 'nextUse' time
                    int maxIndex = 0; // Assume the book in slot 0 is the "farthest"
                    for (int j = 1; j < frames; j++) {
                        // Is the book in slot 'j' used *later* than our current 'farthest' book?
                        if (nextUse[j] > nextUse[maxIndex]) {
                            maxIndex = j; // Yes. This is our new "fartst" book.
                        }
                    }
                    // 'maxIndex' now holds the slot of the book we should kick out.
                    pointer = maxIndex;
                    // --- END OF CORE LOGIC ---
                }

                // Put the new book ('reference[i]') in the chosen slot.
                // If the desk wasn't full, 'pointer' just points to the next empty slot.
                // If the desk *was* full, 'pointer' now points to the "optimal" slot.
                buffer[pointer] = reference[i];

                // This logic just fills the buffer at the start
                if (!isFull) {
                    pointer++; // Move to the next empty slot
                    // Did we just fill the LAST empty slot?
                    if (pointer == frames) {
                        isFull = true; // Yes. Mark the desk as "full" for next time.
                    }
                }
            }
        } // End of main loop. Go to the next homework item.

        // --- 6. PRINT RESULTS ---
        
        System.out.println("F/H Status:");
        for (int i = 0; i < ref_len; i++) {
            System.out.printf("%3c ", status[i]);
        }
        System.out.println();

        System.out.println("The number of Hits: " + hit);
        System.out.println("The number of Faults: " + fault);
        
        sc.close();
    }
}