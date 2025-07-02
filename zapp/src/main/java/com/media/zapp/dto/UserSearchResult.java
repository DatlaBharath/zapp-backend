package com.media.zapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// UserSearchResult.java
@Data
@AllArgsConstructor
public class UserSearchResult {
    private Integer id;
    private String username;
    private String avatarUrl;
    private String fullName;
    private int connectionDepth; // 0 = self, 1 = direct, etc.
}