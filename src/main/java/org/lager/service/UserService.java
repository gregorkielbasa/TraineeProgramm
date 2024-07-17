package org.lager.service;

import org.lager.exception.UserExistsAlreadyException;
import org.lager.exception.UserIllegalLoginException;
import org.lager.exception.UserIllegalPasswordException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String login, String password) throws UserIllegalLoginException, UserIllegalPasswordException, UserExistsAlreadyException {
        validLogin(login);
        validPassword(password);
        checkIfUserExists(login);

        userDetailsManager.createUser(User
                .withUsername(login)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build());
    }

    public void deleteUser(String login) throws UserIllegalLoginException {
        validLogin(login);
        userDetailsManager.deleteUser(login);
    }

    private void checkIfUserExists(String login) throws UserExistsAlreadyException {
        if (userDetailsManager.userExists(login))
            throw new UserExistsAlreadyException(login);
    }

    private void validLogin(String login) throws UserIllegalLoginException {
        if (login == null || login.isBlank())
            throw new UserIllegalLoginException(login);
    }

    private void validPassword(String password) throws UserIllegalPasswordException {
        if (password == null || password.isBlank())
            throw new UserIllegalPasswordException(password);
    }
}
