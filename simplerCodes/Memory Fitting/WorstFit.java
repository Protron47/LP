// Import "Scanner" for user input
import java.util.Scanner;
// Import "Arrays" to easily fill our 'allocation' array
import java.util.Arrays;

// Our program's name
public class WorstFit {
    // The "main" function where all code runs
    public static void main(String[] args) {

        // --- 1. SETUP & 2. GET USER INPUT (Same as First Fit) ---
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter memory block sizes (separated by spaces): ");
        String[] blockStrings = sc.nextLine().split("\\s+");
        System.out.println("Enter process sizes (separated by spaces): ");
        String[] processStrings = sc.nextLine().split("\\s+");

        // --- 3. CREATE & FILL ARRAYS (Same as First Fit) ---
        
        int m = blockStrings.length;
        int n = processStrings.length;
        int[] blockSize = new int[m];
        int[] processSize = new int[n];
        boolean[] blockAllocated = new boolean[m]; // All 'false' by default
        int[] allocation = new int[n];
        Arrays.fill(allocation, -1); // -1 means "Not Allocated"
        for (int i = 0; i < m; i++) blockSize[i] = Integer.parseInt(blockStrings[i]);
        for (int i = 0; i < n; i++) processSize[i] = Integer.parseInt(processStrings[i]);
        
        // --- 4. CORE "WORST FIT" ALGORITHM ---
        
        // Loop through each process ('i')
        for (int i = 0; i < n; i++) {
            
            // --- THIS IS THE CORE LOGIC ---
            // We need to find the "worst" block (biggest fit)
            
            // 'worstIdx' will store the *index* of the worst block
            int worstIdx = -1; 
            // 'maxSize' will store the *size* of that worst block
            int maxSize = -1; // Start at -1 (or 0)

            // Loop through ALL memory blocks ('j') to find the worst one
            for (int j = 0; j < m; j++) {
                
                // Ask two questions:
                // 1. Is this block "empty"?
                // 2. Is this block "big enough"?
                if (!blockAllocated[j] && blockSize[j] >= processSize[i]) {
                    
                    // YES. Now, is it the "worst" one *so far*?
                    // Ask: Is this block's size *BIGGER* than the 'maxSize' we're tracking?
                    if (blockSize[j] > maxSize) {
                        // YES. This is a looser fit. This is our new "worst" choice.
                        maxSize = blockSize[j]; // Remember its size
                        worstIdx = j;         // Remember its index
                    }
                }
            }
            // --- END OF CORE LOGIC ---

            // After checking ALL blocks, did we find one?
            if (worstIdx != -1) {
                // We found a worst-fit block!
                // Assign this process 'i' to the 'worstIdx' block
                allocation[i] = worstIdx;
                
                // Mark that 'worstIdx' block as "full" (true)
                blockAllocated[worstIdx] = true;
            }
        }

        // --- 5. PRINT RESULTS & UTILIZATION (Same as First Fit) ---
        
        System.out.println("\n--- Worst Fit Allocation ---");
        System.out.println("Process No.\tProcess Size\tBlock No.");
        int totalMemory = 0;
        int totalUsed = 0;
        for (int size : blockSize) totalMemory += size;

        for (int i = 0; i < n; i++) {
            System.out.print("   " + (i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocation[i] != -1) {
                System.out.println(allocation[i] + 1);
                totalUsed += processSize[i];
            } else {
                System.out.println("Not Allocated");
            }
        }
        
        double utilization = (double) totalUsed / totalMemory * 100;
        System.out.println("\nTotal Memory Available: " + totalMemory);
        System.out.println("Total Memory Used (by processes): " + totalUsed);
        System.out.printf("Memory Utilization: %.2f%%\n", utilization);

        sc.close();
    }
}