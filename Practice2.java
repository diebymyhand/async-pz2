import java.util.*;
import java.util.concurrent.*;

public class Practice2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // введення параметрів користувачем
        int size = 0;
        while (size < 40 || size > 60) {
            System.out.print("Enter array size (40-60): ");
            if (scanner.hasNextInt()) {
                size = scanner.nextInt();
            } else {
                scanner.next();     // очистка буфера
            }
        }

        // запуск таймера
        long startTime = System.currentTimeMillis();

        int[] mainArray = new int[size];
        Random random = new Random();
        System.out.println("\nGenerated array:");
        for (int i = 0; i < size; i++) {
            mainArray[i] = random.nextInt(101);     // діапазон цілих чисел [0; 100]
            System.out.print(mainArray[i] + " ");
        }
        System.out.println("\n");

        // використання CopyOnWriteArraySet 
        CopyOnWriteArraySet<Integer> finalResultSet = new CopyOnWriteArraySet<>();
        
        // кількість потоків
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();

        // розбиваємо масив на частини
        int chunkSize = size / numberOfThreads;
        if (chunkSize % 2 != 0) {
            chunkSize++;     // робимо розмір частини парним
        }

        int currentIndex = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            int endIndex = Math.min(currentIndex + chunkSize, size);
            
            if (currentIndex >= size - 1) {
                break;
            }

            int[] chunk = Arrays.copyOfRange(mainArray, currentIndex, endIndex);
            
            // створення та запуск задачі Callable
            Callable<List<Integer>> task = new PairMultiplierTask(chunk, i + 1);
            Future<List<Integer>> future = executorService.submit(task);
            futures.add(future);

            currentIndex = endIndex;
        }

        // збір результатів (Future) з перевірками 
        System.out.println("Waiting for results...");
        
        for (Future<List<Integer>> future : futures) {
            try {
                // перевірки isDone() та isCancelled() 
                while (!future.isDone()) {
                    if (future.isCancelled()) {
                        System.out.println("Task was cancelled!");
                        break;
                    }
                    Thread.sleep(10);
                }

                // отримання результату
                if (!future.isCancelled()) {
                    List<Integer> partResult = future.get();
                    finalResultSet.addAll(partResult);
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // завершення роботи
        executorService.shutdown();

        System.out.println("\n=== Results (CopyOnWriteArraySet - unique values) ===");
        System.out.println(finalResultSet);

        long endTime = System.currentTimeMillis();
        System.out.println("\nProgram execution time: " + (endTime - startTime) + " ms");
        
        scanner.close();
    }
}
