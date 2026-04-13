package com.familybudget_BE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FamilyMember {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Family family;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        OWNER, MEMBER
    }
}
