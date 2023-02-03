package com.rogersillito.medialib.common;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ApiError extends ProblemDetail {

    private List<String> errors;

    public static ApiError of(@NonNull HttpStatus status, String detail, List<String> errors) {
        var error = new ApiError();
        error.setDetail(detail);
        error.setStatus(status);
        error.errors = errors;
        return error;
    }
}
