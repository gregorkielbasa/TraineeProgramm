package org.lager.controller;

import org.lager.exception.UserExistsAlreadyException;
import org.lager.exception.UserIllegalLoginException;
import org.lager.exception.UserIllegalPasswordException;
import org.lager.model.dto.UserDto;
import org.lager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createUser(@RequestBody UserDto user) {
        try {
            service.createUser(user.login(), user.password());
        } catch (UserIllegalLoginException | UserIllegalPasswordException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UserExistsAlreadyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteUser(@RequestBody UserDto user) {
        try {
            service.deleteUser(user.login());
        } catch (UserIllegalLoginException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
