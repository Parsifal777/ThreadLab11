package task2_linked_blocking_deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockingTaskDispatcher {
    private final LinkedBlockingDeque<String> taskQueue;
    private volatile boolean running;

    public BlockingTaskDispatcher(int capacity) {
        this.taskQueue = new LinkedBlockingDeque<>(capacity);
        this.running = true;
    }

    // Метод для добавления задач с блокировкой
    public void putTask(String task) throws InterruptedException {
        taskQueue.put(task); // Блокирует, если очередь полна
        System.out.println(Thread.currentThread().getName() + " добавил задачу: " + task);
    }

    // Метод для извлечения задач с блокировкой
    public void takeTask() throws InterruptedException {
        while (running || !taskQueue.isEmpty()) {
            String task = taskQueue.take(); // Блокирует, если очередь пуста
            System.out.println(Thread.currentThread().getName() + " обрабатывает задачу: " + task);
            Thread.sleep(1000); // Имитация обработки
        }
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Поток-потребитель
        executor.submit(() -> {
            try {
                takeTask();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Потоки-производители
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        putTask("Задача-" + producerId + "-" + j);
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        running = false;
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        BlockingTaskDispatcher dispatcher = new BlockingTaskDispatcher(5);
        dispatcher.demonstrate();
    }
}
