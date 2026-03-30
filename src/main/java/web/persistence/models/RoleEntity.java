package web.persistence.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)  // ✅ Stores enum name as string in DB
    private RoleName name;

    public RoleEntity(RoleName name) {
        this.name = name;
    }

    /**
     * Enum defining all possible roles in the system.
     * Easy to add new roles - just add new enum value!
     */
    public enum RoleName {
        ROLE_ADMIN,           // Administrator with full access
        ROLE_USER,            // Basic user (no subscription)
        ROLE_USER_SUBSCRIBED  // User with active subscription
    }
}