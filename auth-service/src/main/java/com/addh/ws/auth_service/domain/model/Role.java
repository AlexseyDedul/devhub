package com.addh.ws.auth_service.domain.model;

import com.addh.ws.auth_service.domain.enums.RoleType;
import com.addh.ws.auth_service.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleType roleType;

}
