package com.example.demo.dtos;

import com.example.demo.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private String name,email,password;
    private Role role;
}
