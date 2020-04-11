package com.runcita.api.shared.models;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.*;
import java.sql.Timestamp;

@Data
@NodeEntity
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Z]+")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Z]+")
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8, max = 50)
    private String password;

    @Max(300)
    private String description;

    private String cover;

    private String picture;

    @NotNull
    private Boolean sexe;

    @Relationship(type = "TO_HAVE")
    private RunningLevel runningLevel;

    @NotNull
    private Timestamp birthday;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]+")
    private String city;

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
                ", city='" + city + '\'' +
                ", runningLevel='" + runningLevel + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
