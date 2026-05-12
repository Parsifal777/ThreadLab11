package task8_atomic_integer_array;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

@SuppressWarnings("ALL")
public class MeasurementGrid {
    private final AtomicIntegerArray grid;
    private final int rows;
    private final int cols;

    public MeasurementGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new AtomicIntegerArray(rows * cols);
    }

    // Метод для обновления ячейки
    public void updateCell(int row, int col, int delta) {
        int index = getIndex(row, col);
        int oldValue = grid.getAndAdd(index, delta);
        int newValue = oldValue + delta;
        System.out.println(Thread.currentThread().getName() +
                " обновил ячейку [" + row + "][" + col + "]: " +
                oldValue + " + " + delta + " = " + newValue);
    }

    // Метод для установки значения ячейки
    public void setCell(int row, int col, int value) {
        int index = getIndex(row, col);
        grid.set(index, value);
    }

    // Метод для получения значения ячейки
    public int getCell(int row, int col) {
        int index = getIndex(row, col);
        return grid.get(index);
    }

    // Метод для условного обновления
    public void compareAndSetCell(int row, int col, int expected, int newValue) {
        int index = getIndex(row, col);
        boolean result = grid.compareAndSet(index, expected, newValue);
        if (result) {
            System.out.println(Thread.currentThread().getName() +
                    " CAS успешно для [" + row + "][" + col + "]: " +
                    expected + " -> " + newValue);
        }
    }

    // Вспомогательный метод для получения индекса
    private int getIndex(int row, int col) {
        return row * cols + col;
    }

    // Метод для отображения сетки
    public void displayGrid() {
        System.out.println("\nСетка измерений:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%4d ", getCell(i, j));
            }
            System.out.println();
        }
    }

    // Метод для вычисления статистики
    public void calculateStatistics() {
        long sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < rows * cols; i++) {
            int value = grid.get(i);
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        double average = (double) sum / (rows * cols);

        System.out.println("\nСтатистика сетки:");
        System.out.println("Сумма: " + sum);
        System.out.println("Среднее: " + average);
        System.out.println("Минимум: " + min);
        System.out.println("Максимум: " + max);
    }

    // Метод для демонстрации
    public void demonstrate() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Инициализация сетки
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                setCell(i, j, 10);
            }
        }

        System.out.println("Начальная сетка:");
        displayGrid();

        // Потоки для обновления разных областей сетки
        executor.submit(() -> {
            for (int i = 0; i < 50; i++) {
                int row = (int) (Math.random() * 2);
                int col = (int) (Math.random() * 3);
                updateCell(row, col, 5);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.submit(() -> {
            for (int i = 0; i < 50; i++) {
                int row = (int) (Math.random() * 2);
                int col = (int) (Math.random() * 3);
                updateCell(row, col, -3);
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.submit(() -> {
            for (int i = 0; i < 50; i++) {
                int row = 2 + (int) (Math.random() * 2);
                int col = (int) (Math.random() * 3);
                updateCell(row, col, 7);
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Демонстрация CAS
            System.out.println("\n--- Демонстрация CAS в сетке ---");
            int row = 0, col = 0;
            int currentValue = getCell(row, col);
            compareAndSetCell(row, col, currentValue, currentValue * 2);
        });

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        displayGrid();
        calculateStatistics();
    }

    public static void main(String[] args) {
        MeasurementGrid grid = new MeasurementGrid(4, 3);
        grid.demonstrate();
    }
}
