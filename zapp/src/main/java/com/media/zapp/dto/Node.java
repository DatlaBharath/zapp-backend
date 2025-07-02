package com.media.zapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
}
