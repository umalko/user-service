package com.mavs.userservice.repository;

import com.mavs.userservice.model.User;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest
@Ignore
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnUser() {
        // given
        User user = getUser();
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(found).hasValueSatisfying(foundUser -> {
            assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
            assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        });
    }

    @Test
    public void whenFindById_thenReturnUser() {
        // given
        User user = getUser();
        User persist = entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(persist.getId());

        // then
        assertThat(found).hasValueSatisfying(foundUser -> assertThat(foundUser.getId()).isNotNull());
    }

    @Test
    public void whenSaveUser_thenReturnUser() {
        // given
        User user = getUser();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).hasFieldOrProperty("id");
    }

    @Test
    public void whenDeleteUserById_thenDontFindIt() {
        // given
        User user = getUser();
        User persist = entityManager.persist(user);
        entityManager.flush();

        // when
        userRepository.deleteById(persist.getId());

        // then
        Optional<User> userById = userRepository.findById(persist.getId());
        assertThat(userById).isEmpty();
    }

    private User getUser() {
        User user = new User();
        user.setEmail("as@as");
        user.setUsername("userT");
        return user;
    }
}