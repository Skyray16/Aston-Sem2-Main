package com.skyray16.userservice.dao;

import com.skyray16.userservice.entity.User;
import com.skyray16.userservice.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static UserDao userDao;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanUp() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createNativeMutationQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Сохранение и поиск пользователя")
    void testSaveAndFind() {
        User user = new User("Иван", "ivan@test.com", 25);
        User saved = userDao.save(user);
        assertNotNull(saved.getId());

        Optional<User> found = userDao.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getName());
    }

    @Test
    @Order(2)
    @DisplayName("Обновление пользователя")
    void testUpdate() {
        User user = new User("Петр", "petr@test.com", 30);
        User saved = userDao.save(user);

        saved.setName("Петр Обновленный");
        User updated = userDao.update(saved);

        assertEquals("Петр Обновленный", updated.getName());
    }

    @Test
    @Order(3)
    @DisplayName("Удаление пользователя")
    void testDelete() {
        User user = new User("Удалить", "delete@test.com", 20);
        User saved = userDao.save(user);
        Long id = saved.getId();

        userDao.deleteById(id);

        assertFalse(userDao.findById(id).isPresent());
        assertEquals(0, userDao.findAll().size());
    }

    @Test
    @Order(4)
    @DisplayName("Поиск всех пользователей")
    void testFindAll() {
        userDao.save(new User("User1", "u1@test.com", 20));
        userDao.save(new User("User2", "u2@test.com", 21));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }
}