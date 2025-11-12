// Import "Scanner" for user input
import java.util.Scanner;
// Import "Arrays" to easily fill our 'allocation' array
import java.util.Arrays;

// Our program's name
public class FirstFit {
    // The "main" function where all code runs
    public static void main(String[] args) {

        // --- 1. SETUP VARIABLES ---
        
        // Create a Scanner tool to read what the user types
        Scanner sc = new Scanner(System.in);
        
        // --- 2. GET USER INPUT (Simplified!) ---
        
        System.out.println("Enter memory block sizes (separated by spaces): ");
        // Read the whole line of block sizes (e.g., "100 50 200 80")
        String blockLine = sc.nextLine();
        // Split that line by spaces into an array of strings
        String[] blockStrings = blockLine.split("\\s+");

        System.out.println("Enter process sizes (separated by spaces): ");
        // Read the whole line of process sizes (e.g., "70 60 150")
        String processLine = sc.nextLine();
        // Split that line by spaces
        String[] processStrings = processLine.split("\\s+");

        // --- 3. CREATE & FILL ARRAYS ---
        
        // The number of blocks (m) is just the length of our blockStrings array
        int m = blockStrings.length;
        // The number of processes (n) is the length of our processStrings array
        int n = processStrings.length;

        // Create integer arrays to hold the actual numbers
        int[] blockSize = new int[m];
        int[] processSize = new int[n];
        
        // This 'boolean' array tracks if a block is full (true) or empty (false)
        boolean[] blockAllocated = new boolean[m]; // All 'false' by default
        
        // This array will store our answer (which block process 'i' goes into)
        int[] allocation = new int[n];
        
        // Fill the 'allocation' array with -1 (meaning "Not Allocated" yet)
        Arrays.fill(allocation, -1);

        // Loop to convert the string block sizes into integer numbers
        for (int i = 0; i < m; i++) {
            blockSize[i] = Integer.parseInt(blockStrings[i]);
        }
        // Loop to convert the string process sizes into integer numbers
        for (int i = 0; i < n; i++) {
            processSize[i] = Integer.parseInt(processStrings[i]);
        }
        
        // --- 4. CORE "FIRST FIT" ALGORITHM ---
        
        // Loop through each process ('i')
        for (int i = 0; i < n; i++) {
            // For each process, loop through the memory blocks ('j') *from the beginning (j=0)*
            for (int j = 0; j < m; j++) {
                
                // --- THIS IS THE CORE LOGIC ---
                // Ask two questions:
                // 1. Is this block "empty"? (!blockAllocated[j])
                // 2. Is this block "big enough"? (blockSize[j] >= processSize[i])
                if (!blockAllocated[j] && blockSize[j] >= processSize[i]) {
                    
                    // YES. It's the first one that fits.
                    // Assign this process 'i' to this block 'j'
                    allocation[i] = j;
                    
                    // Mark this block 'j' as "full" (true)
                    blockAllocated[j] = true;
                    
                    // --- This 'break' is the "FIRST" in First Fit ---
                    // STOP looking for blocks for this process
                    break; 
                }
                // --- END OF CORE LOGIC ---
            }
            // (If 'break' was hit, we go to the next process 'i')
            // (If loop finished with no 'break', process 'i' remains "Not Allocated")
        }

        // --- 5. PRINT RESULTS & UTILIZATION ---
        
        System.out.println("\n--- First Fit Allocation ---");
        System.out.println("Process No.\tProcess Size\tBlock No.");
        
        int totalMemory = 0;
        int totalUsed = 0;

        // Calculate total memory available
        for (int size : blockSize) {
            totalMemory += size;
        }

        // Loop through all processes to print their allocation
        for (int i = 0; i < n; i++) {
            // Print process number (i+1) and its size
            System.out.print("   " + (i + 1) + "\t\t" + processSize[i] + "\t\t");
            
            // Did this process get a block? (Is it NOT -1?)
            if (allocation[i] != -1) {
                // Yes. Print the block number (allocation[i] + 1)
                System.out.println(allocation[i] + 1);
                // Add this process's size to our 'totalUsed' counter
                totalUsed += processSize[i];
            } else {
                // No. Print "Not Allocated"
                System.out.println("Not Allocated");
            }
        }
        
        // Calculate utilization %
        double utilization = (double) totalUsed / totalMemory * 100;

        System.out.println("\nTotal Memory Available: " + totalMemory);
        System.out.println("Total Memory Used (by processes): " + totalUsed);
        System.out.printf("Memory Utilization: %.2f%%\n", utilization);

        // Close the scanner
        sc.close();
    }
}