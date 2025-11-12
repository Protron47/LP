// Import "Scanner" for user input
import java.util.Scanner;
// Import "Arrays" to easily fill our 'allocation' array
import java.util.Arrays;

// Our program's name
public class BestFit {
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
        
        // --- 4. CORE "BEST FIT" ALGORITHM ---
        
        // Loop through each process ('i')
        for (int i = 0; i < n; i++) {
            
            // --- THIS IS THE CORE LOGIC ---
            // We need to find the "best" block (tightest fit)
            
            // 'bestIdx' will store the *index* of the best block we've found
            int bestIdx = -1; 
            // 'minSize' will store the *size* of that best block
            int minSize = Integer.MAX_VALUE; // Start at "infinity"

            // Loop through ALL memory blocks ('j') to find the best one
            // (Notice: no 'break' inside this loop)
            for (int j = 0; j < m; j++) {
                
                // Ask two questions:
                // 1. Is this block "empty"?
                // 2. Is this block "big enough"?
                if (!blockAllocated[j] && blockSize[j] >= processSize[i]) {
                    
                    // YES. Now, is it the "best" one *so far*?
                    // Ask: Is this block's size *smaller* than the 'minSize' we're tracking?
                    if (blockSize[j] < minSize) {
                        // YES. This is a tighter fit. This is our new "best" choice.
                        minSize = blockSize[j]; // Remember its size
                        bestIdx = j;          // Remember its index
                    }
                }
            }
            // --- END OF CORE LOGIC ---

            // After checking ALL blocks, did we find one? (Is 'bestIdx' still -1?)
            if (bestIdx != -1) {
                // We found a best-fit block!
                // Assign this process 'i' to the 'bestIdx' block
                allocation[i] = bestIdx;
                
                // Mark that 'bestIdx' block as "full" (true)
                blockAllocated[bestIdx] = true;
            }
            // (If 'bestIdx' is still -1, the process remains "Not Allocated")
        }

        // --- 5. PRINT RESULTS & UTILIZATION (Same as First Fit) ---
        
        System.out.println("\n--- Best Fit Allocation ---");
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