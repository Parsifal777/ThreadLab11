package task4_copy_on_write_array_list;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskRegistry {
    private final CopyOnWriteArrayList<String> taskList;

    public TaskRegistry() {
        this.taskList = new CopyOnWriteArrayList<>();
    }

    // Метод для добавления задачи
    public void addTask(String task) {
        taskList.add(task);
        System.out.println(Thread.currentThread().getName() + " добавил: " + task);
    }

    // Метод для удаления задачи
    public void removeTask(String task) {
        boolean removed = taskList.remove(task);
        if (removed) {
            System.out.println(Thread.currentThread().getName() + " удалил: " + task);
        }
    }

    // Метод для отображения всех задач
    public void displayTasks() {
        System.out.println("Текущие задачи: " + taskList);
    }

    // Метод для безопасной итерации
    public void iterateTasks() {
        System.out.println(Thread.currentThread().getName() + " читает список...");
        for (String task : taskList) {
            System.out.println("  Задача: " + task);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Потоки для добавления задач
        executor.submit(() -> {
            for (int i = 0; i < 5; i++) {
                addTask("Задача-A" + i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.submit(() -> {
            for (int i = 0; i < 5; i++) {
                addTask("Задача-B" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Поток для чтения списка
        executor.submit(() -> {
            for (int i = 0; i < 3; i++) {
                iterateTasks();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Поток для удаления задач
        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            removeTask("Задача-A0");
            removeTask("Задача-B1");
        });

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        displayTasks();
    }

    public static void main(String[] args) {
        TaskRegistry registry = new TaskRegistry();
        registry.demonstrate();
    }
}
