package task7_atomic_variables;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickCounter {
    private final AtomicInteger clickCount;
    private final AtomicBoolean isTracking;

    public ClickCounter() {
        this.clickCount = new AtomicInteger(0);
        this.isTracking = new AtomicBoolean(true);
    }

    // Метод для инкремента счетчика
    public void incrementClick() {
        int currentCount = clickCount.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + " клик! Всего: " + currentCount);
    }

    // Метод для получения текущего значения
    public int getClickCount() {
        return clickCount.get();
    }

    // Метод для сравнения и установки
    public void compareAndSetExpected(int expected, int newValue) {
        boolean result = clickCount.compareAndSet(expected, newValue);
        if (result) {
            System.out.println("CAS успешно: " + expected + " -> " + newValue);
        } else {
            System.out.println("CAS не удалось: ожидалось " + expected + ", текущее " + clickCount.get());
        }
    }

    public boolean isTracking() {
        return isTracking.get();
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Потоки для имитации кликов
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    if (isTracking()) {
                        incrementClick();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Поток для демонстрации CAS операции
        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("\n--- Демонстрация CAS ---");
            int currentValue = getClickCount();
            compareAndSetExpected(currentValue, currentValue + 100);
            compareAndSetExpected(999, 1000); // Вероятно не совпадет
        });

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nИтоговое количество кликов: " + getClickCount());
    }

    public static void main(String[] args) {
        ClickCounter counter = new ClickCounter();
        counter.demonstrate();
    }
}
