package com.example.auth.auth;

import com.example.auth.security.JwtService;
import com.example.auth.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserRepository repository; private final PasswordEncoder encoder; private final AuthenticationManager manager;
    private final UserDetailsService users; private final JwtService jwt;
    public AuthController(UserRepository repository, PasswordEncoder encoder, AuthenticationManager manager, UserDetailsService users, JwtService jwt) {
        this.repository = repository; this.encoder = encoder; this.manager = manager; this.users = users; this.jwt = jwt;
    }
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED) @Transactional
    public TokenResponse register(@Valid @RequestBody RegisterRequest r) {
        if (repository.existsByUsername(r.username())) throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        if (repository.existsByEmail(r.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "邮箱已存在");
        repository.save(new User(r.username(), r.email(), encoder.encode(r.password())));
        return token(users.loadUserByUsername(r.username()));
    }
    @PostMapping("/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest r) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
        return token(users.loadUserByUsername(r.username()));
    }
    @GetMapping("/users/me") public Map<String,String> me(Authentication auth) { return Map.of("username", auth.getName()); }
    private TokenResponse token(UserDetails user) { return new TokenResponse(jwt.generate(user), "Bearer", jwt.expirationSeconds()); }
    public record RegisterRequest(@NotBlank @Size(min=3,max=50) String username, @NotBlank @Email String email, @NotBlank @Size(min=8,max=72) String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}
}
