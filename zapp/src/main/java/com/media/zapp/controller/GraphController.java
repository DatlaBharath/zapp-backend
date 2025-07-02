package com.media.zapp.controller;

import com.media.zapp.dto.GraphResponse;
import com.media.zapp.dto.UserSearchResult;
import com.media.zapp.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class GraphController {

    @GetMapping("/{userId}/search")
    public ResponseEntity<List<UserSearchResult>> searchUsers(
            @PathVariable Integer userId,
            @RequestParam String prefix) {
        try {
            List<UserSearchResult> results = graphService.searchUsersByNamePrefix(userId, prefix);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    private final GraphService graphService;

    @GetMapping("/graph/{userId}")
    public ResponseEntity<GraphResponse> getGraph(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "3") @Min(1) @Max(5) Integer depth) {

        try {
            GraphResponse response = graphService.getGraph(userId, depth);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}