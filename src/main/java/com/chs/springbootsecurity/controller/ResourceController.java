package com.chs.springbootsecurity.controller;

import com.chs.springbootsecurity.data.UserData;
import com.chs.springbootsecurity.service.DAOUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class ResourceController {

    private final DAOUserService userService;

    @RequestMapping("hellouser")
    public String getUser() {
        return "Hello User";
    }

    @RequestMapping("helloadmin")
    public String getAdmin() {
        return "Hello Admin";
    }

    @PostMapping("register")
    public ResponseEntity<?> saveUser(@RequestBody UserData user) {
        return ResponseEntity.ok(userService.save(user));
    }
}
