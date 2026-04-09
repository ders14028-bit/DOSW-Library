package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.dto.UserCreateDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getUsers().stream().map(UserMapper::toDto).toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable String userId,
                               @AuthenticationPrincipal UserDetails currentUser) {
        return UserMapper.toDto(userService.getUserById(currentUser.getUsername(), userId));
    }

    // Endpoint público — registro libre
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserCreateDTO request) {
        return UserMapper.toDto(userService.registerUser(
                request.id(), request.name(), request.username(), request.password()
        ));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public UserDTO createUserByLibrarian(@RequestBody UserCreateDTO request) {
        return UserMapper.toDto(userService.createUserByLibrarian(
                request.id(), request.name(), request.username(), request.password(), request.role()
        ));
    }
}