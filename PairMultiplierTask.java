import java.util.*;
import java.util.concurrent.*;

class PairMultiplierTask implements Callable<List<Integer>> {
    private final int[] numberChunk;
    private final int taskId;

    public PairMultiplierTask(int[] chunk, int id) {
        this.numberChunk = chunk;
        this.taskId = id;
    }

    @Override
    public List<Integer> call() throws Exception {
        List<Integer> partialResult = new ArrayList<>();
        String threadName = Thread.currentThread().getName();
        
        System.out.println("Thread " + threadName + " (Task " + taskId + ") started processing array of length " + numberChunk.length);

        Thread.sleep(500);

        // обчислення попарного добутку 
        for (int i = 0; i < numberChunk.length - 1; i += 2) {
            int product = numberChunk[i] * numberChunk[i+1];
            partialResult.add(product);
        }

        return partialResult;
    }
}
