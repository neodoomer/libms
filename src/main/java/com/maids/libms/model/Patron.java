package com.maids.libms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patrons")
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
@Builder @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class Patron extends BaseModel<Integer> {
    @NotBlank
    @Column
    private String name;

    @NotBlank
    @Column
    private String address;

    @NotBlank
    @Column
    private String postNo;

    @NotBlank
    @Column
    private String city;

    @NotNull @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{1,4}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$")
    @Column
    private String phoneNo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patron")
    @JsonIgnore
    private Set<BorrowRecord> borrowRecords = new HashSet<>();
}
