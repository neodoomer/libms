package com.maids.libms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity @Table(name = "borrow_records")
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
@Builder @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class BorrowRecord extends BaseModel<Integer> {
    @Column
    @NotNull
    private LocalDateTime borrowingDate;

    @Column
    private LocalDateTime returnDate;

    @JsonIgnoreProperties(value = "borrowRecords")
    @ManyToOne @JoinColumn(name = "patron_id")
    private Patron patron;

    @JsonIgnoreProperties(value = "borrowRecords")
    @ManyToOne @JoinColumn(name = "book_id")
    private Book book;
}
