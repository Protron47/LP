// Import "Scanner" for user input
import java.util.Scanner;
// Import "Arrays" to easily fill our 'allocation' array
import java.util.Arrays;

// Our program's name
public class NextFit {
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
        
        // --- 4. CORE "NEXT FIT" ALGORITHM ---
        
        // --- THIS IS THE KEY "NEXT FIT" VARIABLE ---
        // 'lastAllocated' tracks the *index* of the last block we used.
        // We start searching *from* this spot.
        int lastAllocated = 0; 
        
        // Loop through each process ('i')
        for (int i = 0; i < n; i++) {
            
            // --- THIS IS THE CORE LOGIC ---
            // We search in a "circle" starting from 'lastAllocated'
            
            // Loop 'm' times (e.g., if there are 5 blocks, we check 5 times)
            for (int j = 0; j < m; j++) {
                
                // This 'idx' calculation is the "wrap-around"
                // e.g., if m=5, lastAllocated=3:
                // j=0 -> idx = (3 + 0) % 5 = 3
                // j=1 -> idx = (3 + 1) % 5 = 4
                // j=2 -> idx = (3 + 2) % 5 = 0  (It wrapped around!)
                // j=3 -> idx = (3 + 3) % 5 = 1
                // j=4 -> idx = (3 + 4) % 5 = 2
                int idx = (lastAllocated + j) % m; 
                
                // Ask the same two questions:
                // 1. Is this 'idx' block "empty"?
                // 2. Is this 'idx' block "big enough"?
                if (!blockAllocated[idx] && blockSize[idx] >= processSize[i]) {
                    
                    // YES. This is the "next" spot that fits.
                    // Assign this process 'i' to this block 'idx'
                    allocation[i] = idx;
                    
                    // Mark this block 'idx' as "full" (true)
                    blockAllocated[idx] = true;
                    
                    // --- This is the "NEXT" in Next Fit ---
                    // REMEMBER this spot for the *next* process
                    lastAllocated = idx;
                    
                    // STOP looking for blocks for *this* process
                    break; 
                }
            }
            // (If loop finished with no 'break', process 'i' remains "Not Allocated")
        }

        // --- 5. PRINT RESULTS & UTILIZATION (Same as First Fit) ---
        
        System.out.println("\n--- Next Fit Allocation ---");
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