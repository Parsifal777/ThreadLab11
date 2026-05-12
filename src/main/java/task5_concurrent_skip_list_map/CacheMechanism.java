package task5_concurrent_skip_list_map;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheMechanism {
    private final ConcurrentSkipListMap<Integer, String> cache;
    private volatile boolean running;

    public CacheMechanism() {
        this.cache = new ConcurrentSkipListMap<>();
        this.running = true;
    }

    // Метод для добавления элемента в кэш
    public void putCache(Integer key, String value) {
        cache.put(key, value);
        System.out.println(Thread.currentThread().getName() + " добавил: ключ=" + key + ", значение=" + value);
    }

    // Метод для поиска ближайшего ключа
    public void findNearest(Integer searchKey) {
        Map.Entry<Integer, String> lower = cache.lowerEntry(searchKey);
        Map.Entry<Integer, String> higher = cache.higherEntry(searchKey);
        Map.Entry<Integer, String> exact = cache.ceilingEntry(searchKey);

        System.out.println(Thread.currentThread().getName() + " ищет ближайший к " + searchKey + ":");

        if (exact != null && exact.getKey().equals(searchKey)) {
            System.out.println("  Точное совпадение: " + exact);
            return;
        }

        if (lower == null && higher == null) {
            System.out.println("  Кэш пуст");
            return;
        }

        if (lower == null) {
            System.out.println("  Ближайший (выше): " + higher);
            return;
        }

        if (higher == null) {
            System.out.println("  Ближайший (ниже): " + lower);
            return;
        }

        int lowerDiff = searchKey - lower.getKey();
        int higherDiff = higher.getKey() - searchKey;

        if (lowerDiff <= higherDiff) {
            System.out.println("  Ближайший (ниже): " + lower);
        } else {
            System.out.println("  Ближайший (выше): " + higher);
        }
    }

    // Метод для отображения содержимого кэша
    public void displayCache() {
        System.out.println("Содержимое кэша: " + cache);
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Поток для добавления данных
        executor.submit(() -> {
            putCache(10, "Данные-10");
            putCache(20, "Данные-20");
            putCache(30, "Данные-30");
            putCache(40, "Данные-40");
            putCache(50, "Данные-50");
        });

        // Поток для добавления дополнительных данных
        executor.submit(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            putCache(25, "Данные-25");
            putCache(35, "Данные-35");
            putCache(45, "Данные-45");
        });

        // Поток для поиска ближайших ключей
        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int[] searchKeys = {15, 22, 33, 48, 5, 60};
            for (int key : searchKeys) {
                findNearest(key);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Поток для отображения содержимого
        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            displayCache();
        });

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        running = false;
    }

    public static void main(String[] args) {
        CacheMechanism cache = new CacheMechanism();
        cache.demonstrate();
    }
}
