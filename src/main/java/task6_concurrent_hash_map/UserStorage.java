package task6_concurrent_hash_map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStorage {

    static class User {
        private final int id;
        private final String name;
        private String email;
        private int loginCount;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.loginCount = 0;
        }

        public void incrementLoginCount() {
            this.loginCount++;
        }

        public void updateEmail(String newEmail) {
            this.email = newEmail;
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "', logins=" + loginCount + "}";
        }
    }

    private final ConcurrentHashMap<Integer, User> userStorage;
    private final AtomicInteger userIdCounter;

    public UserStorage() {
        this.userStorage = new ConcurrentHashMap<>();
        this.userIdCounter = new AtomicInteger(1);
    }

    // Метод для добавления пользователя
    public void addUser(String name, String email) {
        int id = userIdCounter.getAndIncrement();
        User user = new User(id, name, email);
        User existingUser = userStorage.putIfAbsent(id, user);
        if (existingUser == null) {
            System.out.println(Thread.currentThread().getName() + " добавил: " + user);
        }
    }

    // Метод для обновления email
    public void updateEmail(int id, String newEmail) {
        User user = userStorage.get(id);
        if (user != null) {
            user.updateEmail(newEmail);
            System.out.println(Thread.currentThread().getName() + " обновил email для пользователя " + id);
        }
    }

    // Метод для инкремента счетчика входов
    public void incrementLoginCount(int id) {
        User user = userStorage.get(id);
        if (user != null) {
            user.incrementLoginCount();
            System.out.println(Thread.currentThread().getName() + " увеличил счетчик входов для пользователя " + id);
        }
    }

    // Метод для отображения всех пользователей
    public void displayAllUsers() {
        System.out.println("Все пользователи:");
        userStorage.forEach((id, user) -> System.out.println("  " + user));
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Потоки для добавления пользователей
        executor.submit(() -> {
            addUser("Анна", "anna@example.com");
            addUser("Борис", "boris@example.com");
            addUser("Виктор", "victor@example.com");
        });

        executor.submit(() -> {
            addUser("Галина", "galina@example.com");
            addUser("Дмитрий", "dmitry@example.com");
        });

        // Потоки для обновления данных
        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            updateEmail(1, "anna.new@example.com");
            updateEmail(3, "victor.new@example.com");
        });

        // Потоки для имитации входов
        executor.submit(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (int i = 0; i < 10; i++) {
                incrementLoginCount(1);
                incrementLoginCount(2);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (int i = 0; i < 8; i++) {
                incrementLoginCount(3);
                incrementLoginCount(4);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        displayAllUsers();
    }

    public static void main(String[] args) {
        UserStorage storage = new UserStorage();
        storage.demonstrate();
    }
}
