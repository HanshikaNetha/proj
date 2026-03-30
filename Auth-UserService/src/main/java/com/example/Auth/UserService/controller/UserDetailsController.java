package com.example.Auth.UserService.controller;

import com.example.Auth.UserService.dto.PageResponse;
import com.example.Auth.UserService.dto.UserResponse;
import com.example.Auth.UserService.dto.UserUpdateRequest;
import com.example.Auth.UserService.exception.UnauthorizedRoleException;
import com.example.Auth.UserService.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserDetailsController {
    private final UserDetailsService userDetailsService;



    @PutMapping("/updateProfile/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal")
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        UserResponse userResponse=userDetailsService.updateUserProfile(id, request);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/getUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userDetailsService.getUserById(id));
    }

    @GetMapping("/getUsersPage")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            throw new UnauthorizedRoleException("Access Denied");
        }
        PageResponse<UserResponse> response =
                userDetailsService.getAllUsersPage(page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/feignInternal/{id}")
    public ResponseEntity<UserResponse> getUserInternal(@PathVariable Long id) {
        UserResponse response=userDetailsService.getUserById(id);
        return ResponseEntity.ok(response);
    }
}
