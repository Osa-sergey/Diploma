package osa.dev.petproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.db.AppUser;
import osa.dev.petproject.repository.AppUserRepository;

@Service("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
        return SecurityUser.fromUser(user);
    }
}
