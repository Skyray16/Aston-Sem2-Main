package com.skyray16.userservice;

import com.skyray16.userservice.dao.UserDao;
import com.skyray16.userservice.dao.UserDaoImpl;
import com.skyray16.userservice.entity.User;
import com.skyray16.userservice.exception.UserServiceException;
import com.skyray16.userservice.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDaoImpl();
private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("=== Добро пожаловать в User Service (Hibernate + PostgreSQL) ===");
        
        try {
            while (true) {
                printMenu();
                int choice = readInt("Выберите действие: ");

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> readUser();
                    case 3 -> readAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        System.out.println("Завершение работы...");
                        HibernateUtil.shutdown();
                        return;
                    }
                    default -> System.out.println("Неверный пункт меню.");
                }
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
        } finally {
            scanner.close();
            HibernateUtil.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
    }

    private static void createUser() {
        try {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine().trim();
            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();
            int age = readInt("Введите возраст: ");

            User user = new User(name, email, age);
            User saved = userDao.save(user);
            System.out.println("Пользователь создан: " + saved);
        } catch (UserServiceException e) {
            System.out.println("Ошибка: " + e.getMessage() + " (Причина: " + e.getCause().getMessage() + ")");
        }
    }

    private static void readUser() {
        long id = readInt("Введите ID пользователя: ");
        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            System.out.println("Найден: " + user.get());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private static void readAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Всего пользователей: " + users.size());
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        long id = readInt("Введите ID пользователя для обновления: ");
        Optional<User> existingOpt = userDao.findById(id);
        
        if (existingOpt.isEmpty()) {
            System.out.println("Пользователь не найден.");
            return;
        }

        User user = existingOpt.get();
        System.out.print("Новое имя (Enter - оставить '" + user.getName() + "'): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        System.out.print("Новый email (Enter - оставить '" + user.getEmail() + "'): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Новый возраст (Enter - оставить " + user.getAge() + "): ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));

        User updated = userDao.update(user);
        System.out.println("Пользователь обновлен: " + updated);
    }

    private static void deleteUser() {
        long id = readInt("Введите ID пользователя для удаления: ");
        userDao.deleteById(id);
        System.out.println("Пользователь с ID " + id + " удален (если существовал).");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }
}