package com.maids.libms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiSubError {
    private String field;
    private String message;
    private Object rejectedValue;
} 