package osa.dev.petproject.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import osa.dev.petproject.models.db.AppUser;
import osa.dev.petproject.models.Status;

public class SecurityUser{

    public static UserDetails fromUser(AppUser user){
        return new User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getRole().getAuthorities()
        );
    }
}
