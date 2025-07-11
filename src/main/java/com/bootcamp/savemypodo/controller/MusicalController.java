package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.service.MusicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class MusicalController {

    private final MusicalService musicalService;

    @GetMapping("/musicals")
    public ResponseEntity<List<MusicalResponse>> getMusicals(
            @RequestParam(name = "sort", defaultValue = "latest") String sortParam) {

        Long userId = null;
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                userId = user.getId();
            }
        }

        
        SortType sort;
        
        try {
            sort = SortType.from(sortParam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); 
        }

        List<MusicalResponse> response = musicalService.getPerformances(sort, userId);
        return ResponseEntity.ok(response);
    }
}
