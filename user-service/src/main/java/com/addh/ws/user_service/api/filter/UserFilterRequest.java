package com.addh.ws.user_service.api.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterRequest {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private String sortBy; // e.g. "email", "createdAt"
    private String direction; // ASC / DESC
    private int page;
    private int size;
}
