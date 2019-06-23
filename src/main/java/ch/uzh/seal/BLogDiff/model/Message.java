package ch.uzh.seal.BLogDiff.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Message {
    private String message;
    private MessageType messageType;
}
