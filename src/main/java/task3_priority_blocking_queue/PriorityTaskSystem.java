package task3_priority_blocking_queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PriorityTaskSystem {

    static class PriorityTask implements Comparable<PriorityTask> {
        private final String name;
        private final int priority; // 1 - наивысший приоритет

        public PriorityTask(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public int compareTo(PriorityTask other) {
            return Integer.compare(this.priority, other.priority);
        }

        @Override
        public String toString() {
            return name + " (приоритет: " + priority + ")";
        }
    }

    private final PriorityBlockingQueue<PriorityTask> taskQueue;
    private volatile boolean running;

    public PriorityTaskSystem() {
        this.taskQueue = new PriorityBlockingQueue<>();
        this.running = true;
    }

    // Метод для добавления задачи с приоритетом
    public void addPriorityTask(String name, int priority) {
        PriorityTask task = new PriorityTask(name, priority);
        taskQueue.put(task);
        System.out.println(Thread.currentThread().getName() + " добавил: " + task);
    }

    // Метод для обработки задач по приоритету
    public void processPriorityTasks() {
        while (running || !taskQueue.isEmpty()) {
            try {
                PriorityTask task = taskQueue.poll(1, TimeUnit.SECONDS);
                if (task != null) {
                    System.out.println(Thread.currentThread().getName() + " обрабатывает: " + task);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Поток-обработчик
        executor.submit(this::processPriorityTasks);

        // Потоки-производители с разными приоритетами
        executor.submit(() -> {
            addPriorityTask("Критическая задача 1", 1);
            addPriorityTask("Обычная задача 1", 3);
            addPriorityTask("Срочная задача 1", 2);
        });

        executor.submit(() -> {
            addPriorityTask("Низкоприоритетная задача", 5);
            addPriorityTask("Критическая задача 2", 1);
            addPriorityTask("Обычная задача 2", 3);
        });

        executor.submit(() -> {
            addPriorityTask("Срочная задача 2", 2);
            addPriorityTask("Обычная задача 3", 3);
            addPriorityTask("Критическая задача 3", 1);
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        running = false;
        executor.shutdown();
    }

    public static void main(String[] args) {
        PriorityTaskSystem system = new PriorityTaskSystem();
        system.demonstrate();
    }
}
