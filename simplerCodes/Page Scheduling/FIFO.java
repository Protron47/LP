// Import the "Scanner" tool so we can read user input
import java.util.Scanner;
// Import the "Arrays" tool so we can easily fill our buffer with "-"
import java.util.Arrays;

// This is our program's name
public class FIFO {
    // This is the "main" function where all the code runs
    public static void main(String[] args) {
        
        // --- 1. SETUP VARIABLES ---
        
        // Make a new Scanner tool named "sc" to read what the user types
        Scanner sc = new Scanner(System.in);
        
        // 'frames' = How many slots are on our "desk" (e.g., 3)
        // 'pointer' = The "finger" that points to the *oldest* slot to replace. Starts at 0.
        // 'hit' = Counter for how many times we found the book on the desk.
        // 'fault' = Counter for how many times we had to get the book from the "locker".
        // 'ref_len' = The total length of our "homework list" (reference string).
        int frames, pointer = 0, hit = 0, fault = 0, ref_len;
        
        // 'buffer' = The array representing our "desk slots".
        String[] buffer;
        // 'reference' = The array for our "homework list" of pages.
        String[] reference;
        // 'status' = The array to store 'H' for Hit or 'F' for Fault for the final output.
        char status[];

        // --- 2. GET USER INPUT ---
        
        System.out.println("Please enter the number of Frames: ");
        // Read the number the user types and store it in the 'frames' variable
        frames = sc.nextInt();

        System.out.println("Please enter the length of the Reference string: ");
        // Read the number for the length and store it in 'ref_len'
        ref_len = sc.nextInt();
        // This 'nextLine()' is important. It "eats" the leftover 'Enter' key press
        // from when the user entered the number.
        sc.nextLine(); 

        // Now that we know the sizes, create the arrays
        reference = new String[ref_len]; // e.g., Make a list with 'ref_len' empty spots
        buffer = new String[frames];     // e.g., Make a list with 'frames' empty spots
        status = new char[ref_len];      // e.g., Make a list with 'ref_len' empty spots

        // Use the 'Arrays' tool to fill our "desk slots" with "-" to show they are empty
        Arrays.fill(buffer, "-");

        System.out.println("Please enter the reference string (separated by spaces): ");
        // Read the whole line of text (e.g., "7 0 1 2 0")
        // and split it by spaces into a temporary array 'inputs'
        String[] inputs = sc.nextLine().split("\\s+");
        
        // Copy the pages from the temporary 'inputs' array into our real 'reference' array
        for (int i = 0; i < ref_len; i++) {
            reference[i] = inputs[i];
        }
        System.out.println(); // Print a blank line for neatness

        // --- 3. MAIN ALGORITHM LOOP ---
        
        // Loop from i = 0 up to the end of the homework list ('ref_len')
        // 'i' is the current step (e.g., we are on homework item 'i')
        for (int i = 0; i < ref_len; i++) {
            
            // --- 4. HIT CHECK ---
            
            // Reset 'search' to -1 for every new page. -1 means "Not Found".
            int search = -1;
            
            // Loop through our desk slots ('j' is the slot number)
            for (int j = 0; j < frames; j++) {
                // Check if the book in desk slot 'j' is the one we need ('reference[i]')
                if (buffer[j].equals(reference[i])) { 
                    // YES! We found it. This is a HIT.
                    search = j;      // Remember the slot 'j' where we found it
                    hit++;           // Add 1 to the 'hit' counter
                    status[i] = 'H'; // Mark this step 'i' as a 'Hit'
                    break;           // Stop searching the desk. Go to the next homework item.
                }
            }

            // --- 5. FAULT LOGIC (The "FIFO" Part) ---
            
            // After checking the desk, was 'search' still -1?
            if (search == -1) { 
                // YES. We didn't find it. This is a PAGE FAULT.
                
                // --- THIS IS THE CORE FIFO LOGIC ---
                // Put the new book ('reference[i]') onto the desk,
                // in the slot our 'pointer' finger is pointing at.
                buffer[pointer] = reference[i];
                
                fault++;           // Add 1 to the 'fault' counter
                status[i] = 'F'; // Mark this step 'i' as a 'Fault'
                
                // Move the 'pointer' finger to the *next* slot
                pointer++;
                
                // If the pointer just moved past the last slot...
                if (pointer == frames) {
                    pointer = 0; // ...wrap it around back to the first slot (0).
                }
                // --- END OF CORE LOGIC ---
            }
        } // End of main loop. Go to the next homework item (next 'i').

        // --- 6. PRINT RESULTS ---
        
        System.out.println("F/H Status:");
        // Loop through our status list and print each 'H' or 'F'
        for (int i = 0; i < ref_len; i++) {
            System.out.printf("%3c ", status[i]);
        }
        System.out.println(); // New line

        // Print the final totals
        System.out.println("The number of Hits: " + hit);
        System.out.println("The number of Faults: " + fault);
        
        // Close the scanner tool to prevent resource leaks
        sc.close();
    }
}