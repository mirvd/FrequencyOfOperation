package ru.netology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final String INSTRUCTIONS = "RLRFR";
    public static final int ROUTE_LENGTH = 100;
    public static final int THREAD_NUM = 1000;

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        Thread[] threads = new Thread[THREAD_NUM];

        Thread printerMax = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printerFrequencyMax();
                }
            }
        });
        printerMax.start();

        for (int i = 0; i < THREAD_NUM; i++) {
            Thread thread = new Thread(() -> {
                String route = generateRoute(INSTRUCTIONS, ROUTE_LENGTH);
                int countR = countChar(route, 'R');
                synchronized (sizeToFreq) {
                    sizeToFreq.putIfAbsent(countR, 0);
                    sizeToFreq.compute(countR, (k, v) -> v + 1);
                    sizeToFreq.notify();
                }
            });
            threads[i] = thread;
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        printerMax.interrupt();
    }

    public static void printerFrequencyMax() {
        int maxFreq = 0;
        int mostCommonSize = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            int size = entry.getKey();
            int freq = entry.getValue();
            if (freq > maxFreq) {
                maxFreq = freq;
                mostCommonSize = size;
            }
        }
        System.out.println("Самое частое количество повторений: " + mostCommonSize +
                " (встретилось " + maxFreq + " раз)");
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
}