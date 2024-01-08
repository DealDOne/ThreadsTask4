import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final int length = 100_000;
        final int amountOfStrings = 30_000;
        final int capacityForQueue = 100;

        long startThreads = System.currentTimeMillis();

        BlockingQueue<String> textsForA = new ArrayBlockingQueue<>(capacityForQueue);
        BlockingQueue<String> textsForB = new ArrayBlockingQueue<>(capacityForQueue);
        BlockingQueue<String> textsForC = new ArrayBlockingQueue<>(capacityForQueue);

        BlockingQueue<FutureTask<Integer>> futureTaskA = new ArrayBlockingQueue<>(capacityForQueue);
        BlockingQueue<FutureTask<Integer>> futureTaskB = new ArrayBlockingQueue<>(capacityForQueue);
        BlockingQueue<FutureTask<Integer>> futureTaskC = new ArrayBlockingQueue<>(capacityForQueue);


        Thread thread1 = new Thread(() -> {
            for (int tx = 0; tx < amountOfStrings; tx++) {
//                System.out.println(tx);
                String letters = "abc";
                Random random = new Random();
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    text.append(letters.charAt(random.nextInt(letters.length())));
                }
                try {
                    textsForA.put(text.toString());
                    textsForB.put(text.toString());
                    textsForC.put(text.toString());

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        thread1.start();

        Runnable myRunnableA = () -> {
            for (int tr = 0; tr < amountOfStrings; tr++) {
                String someText;
                try {
                    someText = textsForA.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int someTextLength = someText.length();
                Callable<Integer> myCallableForMaxSizeA = () -> {
                    int maxSizeA = 0;
                    for (int i = 0; i < someTextLength; i++) {
                        for (int j = 0; j < someTextLength; j++) {
                            if (i >= j) {
                                continue;
                            }
                            boolean aFound = false;
                            for (int k = i; k < j; k++) {
                                if (someText.charAt(k) == 'a') {
                                    aFound = true;
                                    break;
                                }
                            }
                            if (!aFound && maxSizeA < j - i) {
                                maxSizeA = j - i;
                            }
                        }
                    }
                    return maxSizeA;
                };
                FutureTask<Integer> someFutureTaskA = new FutureTask<>(myCallableForMaxSizeA);
                try {
                    futureTaskA.put(someFutureTaskA);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread thread = new Thread(someFutureTaskA);
                thread.start();
            }
        };

        Runnable myRunnableB = () -> {
            for (int tr = 0; tr < amountOfStrings; tr++) {
                String someText;
                try {
                    someText = textsForB.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int someTextLength = someText.length();
                Callable<Integer> myCallableForMaxSizeB = () -> {
                    int maxSizeB = 0;
                    for (int i = 0; i < someTextLength; i++) {
                        for (int j = 0; j < someTextLength; j++) {
                            if (i >= j) {
                                continue;
                            }
                            boolean bFound = false;
                            for (int k = i; k < j; k++) {
                                if (someText.charAt(k) == 'b') {
                                    bFound = true;
                                    break;
                                }
                            }
                            if (!bFound && maxSizeB < j - i) {
                                maxSizeB = j - i;
                            }
                        }
                    }
                    return maxSizeB;
                };
                FutureTask<Integer> someFutureTaskB = new FutureTask<>(myCallableForMaxSizeB);
                try {
                    futureTaskB.put(someFutureTaskB);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread thread = new Thread(someFutureTaskB);
                thread.start();
            }
        };

        Runnable myRunnableC = () -> {
            for (int tr = 0; tr < amountOfStrings; tr++) {
                String someText;
                try {
                    someText = textsForC.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int someTextLength = someText.length();
                Callable<Integer> myCallableForMaxSizeC = () -> {
                    int maxSizeC = 0;
                    for (int i = 0; i < someTextLength; i++) {
                        for (int j = 0; j < someTextLength; j++) {
                            if (i >= j) {
                                continue;
                            }
                            boolean cFound = false;
                            for (int k = i; k < j; k++) {
                                if (someText.charAt(k) == 'c') {
                                    cFound = true;
                                    break;
                                }
                            }
                            if (!cFound && maxSizeC < j - i) {
                                maxSizeC = j - i;
                            }
                        }
                    }
                    return maxSizeC;
                };
                FutureTask<Integer> someFutureTaskC = new FutureTask<>(myCallableForMaxSizeC);
                try {
                    futureTaskC.put(someFutureTaskC);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread thread = new Thread(someFutureTaskC);
                thread.start();
            }
        };
        Thread threadA = new Thread(myRunnableA);
        Thread threadB = new Thread(myRunnableB);
        Thread threadC = new Thread(myRunnableC);

        threadA.start();
        threadB.start();
        threadC.start();

        AtomicInteger MaxA = new AtomicInteger(0);
        AtomicInteger MaxB = new AtomicInteger(0);
        AtomicInteger MaxC = new AtomicInteger(0);

        new Thread(() -> {
            for (int tr = 0; tr < amountOfStrings; tr++) {
                try {
                    int currentValueAFromQueue = futureTaskA.take().get();
                    if (currentValueAFromQueue > MaxA.get()) {
                        MaxA.getAndSet(currentValueAFromQueue);
                    };
                    if (tr == amountOfStrings -1) {
                        long endThreads = System.currentTimeMillis();
                        System.out.println("maxSize case 'a' -> " + MaxA.get());
                        System.out.println("Time: " + (endThreads - startThreads) + "ms");
                    }
                }catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    int currentValueBFromQueue = futureTaskB.take().get();
                    if (currentValueBFromQueue > MaxB.get()) {
                        MaxB.getAndSet(currentValueBFromQueue);
                    };
                    if (tr == amountOfStrings -1) {
                        long endThreads = System.currentTimeMillis();
                        System.out.println("maxSize case 'b' -> " + MaxB.get());
                        System.out.println("Time: " + (endThreads - startThreads) + "ms");
                    }
                }catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    int currentValueCFromQueue = futureTaskC.take().get();
                    if (currentValueCFromQueue > MaxC.get()) {
                        MaxC.getAndSet(currentValueCFromQueue);
                    };
                    if (tr == amountOfStrings -1) {
                        long endThreads = System.currentTimeMillis();
                        System.out.println("maxSize case 'c' -> " + MaxC.get());
                        System.out.println("Time: " + (endThreads - startThreads) + "ms");
                    }
                }

                catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
}