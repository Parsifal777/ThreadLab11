package task1_concurrent_linked_deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskDispatcher {
    private final ConcurrentLinkedDeque<String> taskQueue;
    private volatile boolean running;

    public TaskDispatcher() {
        this.taskQueue = new ConcurrentLinkedDeque<>();
        this.running = true;
    }

    // Метод для добавления задач в очередь
    public void addTask(String task) {
        taskQueue.add(task);
        System.out.println(Thread.currentThread().getName() + " добавил задачу: " + task);
    }

    // Метод для обработки задач
    public void processTasks() {
        while (running || !taskQueue.isEmpty()) {
            String task = taskQueue.poll();
            if (task != null) {
                System.out.println(Thread.currentThread().getName() + " обрабатывает задачу: " + task);
                try {
                    Thread.sleep(500); // Имитация обработки
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // Метод для запуска демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Поток-обработчик
        executor.submit(this::processTasks);

        // Потоки-производители
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    addTask("Задача-" + producerId + "-" + j);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        running = false;
    }

    public static void main(String[] args) {
        TaskDispatcher dispatcher = new TaskDispatcher();
        dispatcher.demonstrate();
    }
}
