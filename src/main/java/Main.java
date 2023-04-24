import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        List<Future> futures = new ArrayList<>();
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            Callable<Integer> callable = new Callable<>() {
                @Override
                public Integer call() {
                    int maxSize = 0;
                    for (int i = 0; i < text.length(); i++) {
                        for (int j = 0; j < text.length(); j++) {
                            if (i >= j) {
                                continue;
                            }
                            boolean bFound = false;
                            for (int k = i; k < j; k++) {
                                if (text.charAt(k) == 'b') {
                                    bFound = true;
                                    break;
                                }
                            }
                            if (!bFound && maxSize < j - i) {
                                maxSize = j - i;
                            }
                        }
                    }
                    System.out.println(text.substring(0, 100) + " -> " + maxSize);
                    return maxSize;
                }
            };
            Future<Integer> future = executorService.submit(callable);
            futures.add(future);
        }

        List<Integer> results = new ArrayList<>();
        for (Future future : futures) {
            results.add((Integer) future.get());
        }
        executorService.shutdown();
        System.out.println(results.stream()
                .max(Comparator.naturalOrder())
                .get());
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
