package com.maids.libms.repository;

import com.maids.libms.model.Patron;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatronRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatronRepository patronRepository;

    @Test
    void shouldSavePatron() {
        // given
        Patron patron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();

        // when
        Patron savedPatron = patronRepository.save(patron);

        // then
        assertThat(savedPatron).isNotNull();
        assertThat(savedPatron.getId()).isNotNull();
        assertThat(savedPatron.getEmail()).isEqualTo(patron.getEmail());
    }

    @Test
    void shouldFindPatronById() {
        // given
        Patron patron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        
        Patron savedPatron = entityManager.persist(patron);
        entityManager.flush();

        // when
        Patron foundPatron = patronRepository.findById(savedPatron.getId()).orElse(null);

        // then
        assertThat(foundPatron).isNotNull();
        assertThat(foundPatron.getName()).isEqualTo(patron.getName());
        assertThat(foundPatron.getEmail()).isEqualTo(patron.getEmail());
    }

    @Test
    void shouldDeletePatron() {
        // given
        Patron patron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        
        Patron savedPatron = entityManager.persist(patron);
        entityManager.flush();

        // when
        patronRepository.deleteById(savedPatron.getId());

        // then
        Patron foundPatron = entityManager.find(Patron.class, savedPatron.getId());
        assertThat(foundPatron).isNull();
    }

    @Test
    void shouldUpdatePatron() {
        // given
        Patron patron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        
        Patron savedPatron = entityManager.persist(patron);
        entityManager.flush();
        
        // when
        savedPatron.setName("Jane Doe");
        savedPatron.setEmail("jane.doe@example.com");
        Patron updatedPatron = patronRepository.save(savedPatron);
        
        // then
        assertThat(updatedPatron.getName()).isEqualTo("Jane Doe");
        assertThat(updatedPatron.getEmail()).isEqualTo("jane.doe@example.com");
        
        Patron foundPatron = entityManager.find(Patron.class, savedPatron.getId());
        assertThat(foundPatron.getName()).isEqualTo("Jane Doe");
        assertThat(foundPatron.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void shouldFindAllPatrons() {
        // given
        Patron patron1 = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        
        Patron patron2 = Patron.builder()
                .name("Jane Smith")
                .address("456 Oak Avenue")
                .postNo("67890")
                .city("Othertown")
                .email("jane.smith@example.com")
                .phoneNo("+1-555-987-6543")
                .build();
        
        entityManager.persist(patron1);
        entityManager.persist(patron2);
        entityManager.flush();
        
        // when
        List<Patron> patrons = patronRepository.findAll();
        
        // then
        assertThat(patrons).isNotEmpty();
        assertThat(patrons.size()).isGreaterThanOrEqualTo(2);
        assertThat(patrons).extracting(Patron::getEmail)
                           .contains(patron1.getEmail(), patron2.getEmail());
    }
} 