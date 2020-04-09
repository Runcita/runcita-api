package com.runcita.api.shared.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NodeEntity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String description;
    private String picture;
    @NotNull
    private Boolean sexe;
    private String runningLevel;
    @NotNull
    private Timestamp birthday;

    /**
     * Encode password
     * @param passwordEncoder
     */
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", sexe=" + sexe +
                ", runningLevel='" + runningLevel + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
