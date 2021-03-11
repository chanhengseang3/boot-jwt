package com.chs.springbootsecurity.data;

import lombok.Data;

@Data
public class UserData {
    private String username;
    private String password;
    private String role;
}
