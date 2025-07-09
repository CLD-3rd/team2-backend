package com.bootcamp.savemypodo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bootcamp.savemypodo.dto.performance.PerformanceResponseDto;
import com.bootcamp.savemypodo.entity.PerformanceSortType;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.service.PerformanceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class PerformanceController {
	
	private final PerformanceService performanceService;
	
	@GetMapping("/performances")
    @ResponseBody
    public List<PerformanceResponseDto> getPerformances(
            @RequestParam(defaultValue = "LATEST") PerformanceSortType sort) {

        Long userId = null;

        if (sort == PerformanceSortType.MINE) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                if (principal instanceof User user) {
                    userId = user.getId();
                }
            }
        }

        return performanceService.getPerformances(sort, userId);
    }

}
