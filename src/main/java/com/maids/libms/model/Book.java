package com.maids.libms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "books")
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
@Builder @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class Book extends BaseModel<Integer> {
    @NotBlank
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")
    @Column(unique = true)
    private String isbn;

    @NotBlank
    @Column
    private String title;

    @NotBlank
    @Column
    private String author;

    @Column
    private String description;

    @NotNull
    @Min(1000) @Max(2025)
    @Column
    private Integer publicationYear;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnore
    private Set<BorrowRecord> borrowRecords = new HashSet<>();
}
