package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.dto.UserCreateDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getUsers(@RequestParam String actorUserId) {
        return userService.getUsers(actorUserId).stream().map(UserMapper::toDto).toList();
    }

    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable String userId, @RequestParam String actorUserId) {
        return UserMapper.toDto(userService.getUserById(actorUserId, userId));
    }

    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserCreateDTO request) {
        return UserMapper.toDto(userService.registerUser(
                request.id(),
                request.name(),
                request.username(),
                request.password()
        ));
    }

    @PostMapping
    public UserDTO createUserByLibrarian(@RequestParam String actorUserId, @RequestBody UserCreateDTO request) {
        return UserMapper.toDto(userService.createUserByLibrarian(
                actorUserId,
                request.id(),
                request.name(),
                request.username(),
                request.password(),
                request.role()
        ));
    }
}

