package com.user.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class UsersResponse {
    private List<AdminDTO> users;
}
