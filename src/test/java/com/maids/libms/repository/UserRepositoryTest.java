package com.maids.libms.repository;

import com.maids.libms.model.Role;
import com.maids.libms.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void shouldFindUserById() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();
        
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // when
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();
        
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> foundUserOptional = userRepository.findByEmail(user.getEmail());

        // then
        assertThat(foundUserOptional).isPresent();
        User foundUser = foundUserOptional.get();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentEmail() {
        // when
        Optional<User> foundUserOptional = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(foundUserOptional).isEmpty();
    }

    @Test
    void shouldDeleteUser() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();
        
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // when
        userRepository.deleteById(savedUser.getId());

        // then
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNull();
    }

    @Test
    void shouldUpdateUser() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();
        
        User savedUser = entityManager.persist(user);
        entityManager.flush();
        
        // when
        savedUser.setEmail("updated@example.com");
        savedUser.setRole(Role.ROLE_USER);
        User updatedUser = userRepository.save(savedUser);
        
        // then
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getRole()).isEqualTo(Role.ROLE_USER);
        
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(foundUser.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void shouldFindAllUsers() {
        // given
        User admin = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();
        
        User librarian = User.builder()
                .email("librarian@example.com")
                .password("password456")
                .role(Role.ROLE_ADMIN)
                .build();
        
        entityManager.persist(admin);
        entityManager.persist(librarian);
        entityManager.flush();
        
        // when
        List<User> users = userRepository.findAll();
        
        // then
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
        assertThat(users).extracting(User::getEmail)
                         .contains(admin.getEmail(), librarian.getEmail());
    }

    @Test
    void shouldVerifyUserDetailsImplementation() {
        // given
        User user = User.builder()
                .email("admin@example.com")
                .password("password123")
                .role(Role.ROLE_ADMIN)
                .build();

        // when & then
        assertThat(user.getUsername()).isEqualTo(user.getEmail());
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo(Role.ROLE_ADMIN.name());
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
} 