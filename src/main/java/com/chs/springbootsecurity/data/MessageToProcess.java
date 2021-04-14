package com.chs.springbootsecurity.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MessageToProcess {

    private final String message;
    private final int priority;

    @JsonCreator
    public MessageToProcess(@JsonProperty("message") String message,
                            @JsonProperty("priority") int priority) {
        this.message = message;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "MessageToProcess{" + "message='" + message + '\'' + ", priority=" + priority + '}';
    }
}
