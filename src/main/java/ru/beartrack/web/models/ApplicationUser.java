package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.beartrack.web.dto.PersonDTO;
import ru.beartrack.web.enums.Role;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@Table(name = "application_user")
public class ApplicationUser implements UserDetails {
    @Id
    private UUID uuid;

    private String username;
    private String email;
    private String password;
    private String avatar;
    private String lastname;
    private String name;
    private String middleName;
    private LocalDate birthday;
    private LocalDate placedAt;
    private Role role;

    public ApplicationUser(PersonDTO personDTO, PasswordEncoder encoder) {
        setUsername(personDTO.getUsername());
        setPassword(encoder.encode(personDTO.getPassword()));
        setLastname(personDTO.getLastname());
        setName(personDTO.getName());
        setMiddleName(personDTO.getMiddleName());
        setBirthday(LocalDate.parse(personDTO.getBirthday()));
        setPlacedAt(LocalDate.parse(personDTO.getPlacedAt()));
        setEmail(personDTO.getEmail());
        setRole(Role.USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
