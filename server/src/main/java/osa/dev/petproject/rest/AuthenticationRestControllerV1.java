package osa.dev.petproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.dto.AuthenticationRequestDTO;
import osa.dev.petproject.dto.NewAppUserRequestDTO;
import osa.dev.petproject.models.AppUser;
import osa.dev.petproject.models.Role;
import osa.dev.petproject.models.Status;
import osa.dev.petproject.repository.AppUserRepository;
import osa.dev.petproject.security.JwtTokenProvider;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider provider;
    private final AppUserRepository userRepository;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager,
                                          JwtTokenProvider provider,
                                          AppUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.provider = provider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            String email = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            AppUser user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
            String token = provider.createToken(email, user.getRole().name());
            Map<Object, Object> response = new HashMap<>();
            response.put("user_id", user.getId());
            response.put("first_name", user.getFirstName());
            response.put("last_name", user.getLastName());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    /**
     * пароль передается в захэшированном виде Bcrypt strength 12
     *
     */
    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('users:create_users')")
    public ResponseEntity<?> createNewUser(@RequestBody NewAppUserRequestDTO dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent())
            return new ResponseEntity<>("User with this email already exists", HttpStatus.CONFLICT);
        try{
            AppUser user = new AppUser();
            user.setEmail(dto.getEmail());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setPassword(dto.getPassword());
            user.setRole(Role.USER);
            user.setStatus(Status.ACTIVE);
            user = userRepository.save(user);
            Map<Object, Object> response = new HashMap<>();
            response.put("id", user.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return new ResponseEntity<>("Incorrect data", HttpStatus.BAD_REQUEST);
        }
    }
}
