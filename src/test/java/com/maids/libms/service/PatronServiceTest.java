package com.maids.libms.service;

import com.maids.libms.model.Patron;
import com.maids.libms.repository.PatronRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronServiceTest {

    @Mock
    private PatronRepository patronRepository;

    @InjectMocks
    private PatronService patronService;

    private Patron testPatron;

    @BeforeEach
    void setUp() {
        testPatron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        testPatron.setId(1);
    }

    @Test
    void findById_shouldReturnPatron_whenPatronExists() {
        // given
        when(patronRepository.findById(1)).thenReturn(Optional.of(testPatron));

        // when
        Patron result = patronService.findById(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPatron.getId());
        assertThat(result.getName()).isEqualTo(testPatron.getName());
        assertThat(result.getEmail()).isEqualTo(testPatron.getEmail());
        verify(patronRepository, times(1)).findById(1);
    }

    @Test
    void findById_shouldThrowException_whenPatronDoesNotExist() {
        // given
        when(patronRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> patronService.findById(999));
        verify(patronRepository, times(1)).findById(999);
    }

    @Test
    void create_shouldReturnSavedPatron() {
        // given
        Patron patronToCreate = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();

        when(patronRepository.save(any(Patron.class))).thenReturn(testPatron);

        // when
        Patron result = patronService.create(patronToCreate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPatron.getId());
        assertThat(result.getName()).isEqualTo(testPatron.getName());
        verify(patronRepository, times(1)).save(patronToCreate);
    }

    @Test
    void update_shouldReturnUpdatedPatron_whenPatronExists() {
        // given
        Patron patronToUpdate = Patron.builder()
                .name("John Smith")  // Updated name
                .address("456 Oak Avenue")  // Updated address
                .postNo("12345")
                .city("Anytown")
                .email("john.smith@example.com")  // Updated email
                .phoneNo("+1-555-123-4567")
                .build();
        patronToUpdate.setId(1);

        Patron updatedPatron = Patron.builder()
                .name("John Smith")
                .address("456 Oak Avenue")
                .postNo("12345")
                .city("Anytown")
                .email("john.smith@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        updatedPatron.setId(1);

        when(patronRepository.findById(1)).thenReturn(Optional.of(testPatron));
        when(patronRepository.save(any(Patron.class))).thenReturn(updatedPatron);

        // when
        Patron result = patronService.update(1, patronToUpdate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Smith");
        assertThat(result.getAddress()).isEqualTo("456 Oak Avenue");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
        verify(patronRepository, times(1)).findById(1);
        verify(patronRepository, times(1)).save(any(Patron.class));
    }

    @Test
    void update_shouldThrowException_whenPatronDoesNotExist() {
        // given
        Patron patronToUpdate = Patron.builder()
                .name("John Smith")
                .address("456 Oak Avenue")
                .postNo("12345")
                .city("Anytown")
                .email("john.smith@example.com")
                .phoneNo("+1-555-123-4567")
                .build();

        when(patronRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> patronService.update(999, patronToUpdate));
        verify(patronRepository, times(1)).findById(999);
        verify(patronRepository, never()).save(any(Patron.class));
    }

    @Test
    void delete_shouldDeletePatron_whenPatronExists() {
        // given
        when(patronRepository.findById(1)).thenReturn(Optional.of(testPatron));
        doNothing().when(patronRepository).delete(any(Patron.class));

        // when
        String result = patronService.delete(1);

        // then
        assertThat(result).isEqualTo("Done.");
        verify(patronRepository, times(1)).findById(1);
        verify(patronRepository, times(1)).delete(testPatron);
    }

    @Test
    void delete_shouldThrowException_whenPatronDoesNotExist() {
        // given
        when(patronRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> patronService.delete(999));
        verify(patronRepository, times(1)).findById(999);
        verify(patronRepository, never()).delete(any(Patron.class));
    }

    @Test
    void findAll_shouldReturnAllPatrons() {
        // given
        Patron patron2 = Patron.builder()
                .name("Jane Smith")
                .address("789 Pine Street")
                .postNo("67890")
                .city("Othertown")
                .email("jane.smith@example.com")
                .phoneNo("+1-555-987-6543")
                .build();
        patron2.setId(2);

        List<Patron> patrons = Arrays.asList(testPatron, patron2);
        when(patronRepository.findAll()).thenReturn(patrons);

        // when
        List<Patron> result = patronService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testPatron, patron2);
        verify(patronRepository, times(1)).findAll();
    }
} 