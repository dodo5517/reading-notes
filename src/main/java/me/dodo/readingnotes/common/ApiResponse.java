package me.dodo.readingnotes.common;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    public boolean isSuccess() {
        return success;
    }
    public T getData() {
        return data;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
