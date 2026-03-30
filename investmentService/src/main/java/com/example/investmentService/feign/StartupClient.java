package com.example.investmentService.feign;

import com.example.investmentService.dto.StartupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "STARTUP-SERVICE")
public interface StartupClient {
    @GetMapping("/startups/feignInternal/{id}")
    StartupResponse getStartupById(@PathVariable Long id);
}
