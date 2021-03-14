package com.chs.springbootsecurity.service;

import com.chs.springbootsecurity.data.UserData;
import com.chs.springbootsecurity.entity.DAOUser;
import com.chs.springbootsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DAOUserService {

    private final PasswordEncoder bcryptEncoder;
    private final UserRepository userDao;

    public DAOUser save(UserData user) {
        DAOUser newUser = new DAOUser();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());
        return userDao.save(newUser);
    }
}
