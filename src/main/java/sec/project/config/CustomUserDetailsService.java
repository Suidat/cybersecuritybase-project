package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import sec.project.domain.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Message;
import sec.project.repository.AccountRepository;
import sec.project.repository.MessageRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveAccount(String username, String password){
        Account a = new Account(username, passwordEncoder.encode(password));
        accountRepository.save(a);
    }

    @PostConstruct
    public void init() {
        // this data would typically be retrieved from a database
        // A2 problem. Set a more difficult to crack password.
        Account u = new Account("admin", passwordEncoder.encode("password1"));
        u.setStatus("ADMIN");
        this.accountRepository.save(u);
        this.messageRepository.save(new Message(u, "Test message"));
        this.messageRepository.save(new Message(u, "a new message"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account u;
        try {
            u = this.accountRepository.findByUsername(username);
            if (u.getUsername() == null) {
                throw new UsernameNotFoundException("No such user: " + username);
            }
        }catch(NullPointerException n){
            throw new UsernameNotFoundException("No such user: " + username);
        }
        if(u.getStatus().equals("ADMIN")){

            return new org.springframework.security.core.userdetails.User(
                    username,
                    u.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    Arrays.asList(new SimpleGrantedAuthority("ADMIN")));
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                u.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
