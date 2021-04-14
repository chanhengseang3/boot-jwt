package com.chs.springbootsecurity.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class AuthenticationExceptionData {
    private final int status;
    private final String message;
    private final String path;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time;
}
