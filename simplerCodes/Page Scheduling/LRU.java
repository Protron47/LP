// Import "Scanner" for user input
import java.util.Scanner;
// Import "ArrayList" for our special "tracker" list
import java.util.ArrayList;
// Import "Arrays" to fill our buffer
import java.util.Arrays;

public class LRU {
    public static void main(String[] args) {
        
        // --- 1. SETUP VARIABLES ---
        
        Scanner sc = new Scanner(System.in);
        int frames, hit = 0, fault = 0, ref_len;
        String[] buffer;    // Our "desk slots"
        String[] reference; // Our "homework list"
        char status[];      // Our 'H'/'F' list for output

        // This "stack" (or tracker list) is the key to LRU.
        // It is *NOT* the desk. It just remembers the *order* we use books.
        // The item at the START (index 0) is the LEAST recent.
        // The item at the END is the MOST recent.
        ArrayList<String> stack = new ArrayList<>();

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

        // --- 3. MAIN ALGORITHM LOOP ---
        
        // Loop through each homework item 'i'
        for (int i = 0; i < ref_len; i++) {
            
            // --- THIS IS THE "TRACKER" LOGIC ---
            // No matter if it's a hit or a fault, this page ('reference[i]')
            // is now the "Most Recently Used."
            // 1. Remove it from the tracker (if it's already there)
            stack.remove(reference[i]);
            // 2. Add it to the *very end* of the tracker list
            stack.add(reference[i]);

            // --- 4. HIT CHECK ---
            
            // Reset 'search' to -1 (Not Found)
            int search = -1;
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

            // --- 5. FAULT LOGIC (The "LRU" Part) ---
            
            // After checking, was 'search' still -1?
            if (search == -1) { 
                // YES. This is a PAGE FAULT.
                fault++;
                status[i] = 'F';

                // --- THIS IS THE CORE LRU LOGIC ---
                // We need to find which desk slot to replace.
                
                // 'pointer' will hold the index of the slot we choose
                int pointer = 0; 
                // 'min_loc' will hold the "worst" (smallest) tracker position
                int min_loc = Integer.MAX_VALUE;

                // Loop through our desk slots ('j')
                for (int j = 0; j < frames; j++) {
                    // Is this desk slot empty?
                    if (buffer[j].equals("-")) { 
                        pointer = j; // YES! We'll just use this empty slot.
                        break;       // Stop looking. We found our spot.
                    }
                    
                    // If the slot isn't empty:
                    // Find the "recent-ness" of the book in this slot (buffer[j])
                    // by finding its position in our 'stack' tracker.
                    int idx = stack.indexOf(buffer[j]);
                    
                    // Is this book's position ('idx') "worse" (smaller) than
                    // the worst one we've found so far ('min_loc')?
                    if (idx < min_loc) {
                        min_loc = idx; // This is our new "worst" (least recent) book
                        pointer = j;   // This is the desk slot we'll replace
                    }
                }
                // After the loop, 'pointer' holds the index of either:
                // 1. The first empty slot we found.
                // 2. The slot of the "Least Recently Used" book.
                
                // Put our new book ('reference[i]') in that slot.
                buffer[pointer] = reference[i];
                // --- END OF CORE LOGIC ---
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