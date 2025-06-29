package com.claude.customapi.Exception;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExceptionTemplate {
    private String errorCode;
    private String message;
    private LocalDateTime time;
}
