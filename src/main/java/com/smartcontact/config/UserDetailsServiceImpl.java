package com.smartcontact.config;

import com.smartcontact.model.User;
import com.smartcontact.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.getUserByUserName(s);
        if (user == null)
            throw new UsernameNotFoundException("Could not found user with given email !");
        return new CustomUserDetails(user);
    }
}
