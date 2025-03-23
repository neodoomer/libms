package com.maids.libms.controller;

import com.maids.libms.model.Patron;
import com.maids.libms.service.PatronService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronControllerTest {

    @Mock
    private PatronService patronService;

    @InjectMocks
    private PatronController patronController;

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
    void findAll_ShouldReturnPatronsPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patron> expectedPage = new PageImpl<>(List.of(testPatron));
        when(patronService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<?> response = patronController.findAll(pageable, false);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPage);
        verify(patronService).findAll(pageable);
    }

    @Test
    void findAll_ShouldReturnAllPatronsWhenUnpaged() {
        // Arrange
        List<Patron> expectedPatrons = List.of(testPatron);
        when(patronService.findAll()).thenReturn(expectedPatrons);

        // Act
        ResponseEntity<?> response = patronController.findAll(Pageable.unpaged(), true);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPatrons);
        verify(patronService).findAll();
    }

    @Test
    void findById_ShouldReturnPatronWhenExists() {
        // Arrange
        when(patronService.findById(1)).thenReturn(testPatron);

        // Act
        ResponseEntity<Patron> response = patronController.findById(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testPatron);
        verify(patronService).findById(1);
    }

    @Test
    void findById_ShouldThrowNotFoundWhenDoesNotExist() {
        // Arrange
        when(patronService.findById(999)).thenThrow(new EntityNotFoundException("Patron not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patronController.findById(999));
        verify(patronService).findById(999);
    }

    @Test
    void create_ShouldReturnCreatedPatron() {
        // Arrange
        Patron newPatron = testPatron;
        testPatron.setId(null);
        when(patronService.create(any(Patron.class))).thenReturn(testPatron);

        // Act
        ResponseEntity<Patron> response = patronController.create(newPatron);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testPatron);
        verify(patronService).create(newPatron);
    }

    @Test
    void update_ShouldReturnUpdatedPatron() {
        // Arrange
        Patron updatedPatron = testPatron;
        testPatron.setName("John Smith");
        testPatron.setEmail("john.smith@example.com");
        when(patronService.update(eq(1), any(Patron.class))).thenReturn(updatedPatron);

        // Act
        ResponseEntity<Patron> response = patronController.update(1, updatedPatron);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedPatron);
        verify(patronService).update(1, updatedPatron);
    }

    @Test
    void delete_ShouldReturnSuccessMessage() {
        // Arrange
        when(patronService.delete(1)).thenReturn("Done.");

        // Act
        ResponseEntity<String> response = patronController.delete(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Done.");
        verify(patronService).delete(1);
    }
}