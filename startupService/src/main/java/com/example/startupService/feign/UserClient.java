package com.example.startupService.feign;

import com.example.startupService.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTH-USER-SERVICE")
public interface UserClient {
    @GetMapping("/users/feignInternal/{id}")
    public UserResponse getUserById(@PathVariable Long id);
}
