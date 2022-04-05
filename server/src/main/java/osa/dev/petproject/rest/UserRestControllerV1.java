package osa.dev.petproject.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.models.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {

    private List<User>  users = Stream.of(
            new User(1L, "osa", "owchin.s@yandex.ru"),
            new User(2L, "avo", "avo7@yandxe.ru")
    ).collect(Collectors.toList());

    @GetMapping
    @PreAuthorize("hasAuthority('users:read')")
    public List<User> getAll(){
        return users;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:read')")
    public User getById(@PathVariable Long id){
        return users.stream().filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('users:write')")
    public void add(@RequestBody User user){
        users.add(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:write')")
    public void deleteById(@PathVariable Long id){
        users.removeIf(user -> user.getId().equals(id));
    }
}
