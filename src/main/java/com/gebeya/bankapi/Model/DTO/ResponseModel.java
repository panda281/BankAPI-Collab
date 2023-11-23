package com.gebeya.bankapi.Model.DTO;

public class ResponseModel {
    private boolean success;
    private String message;

    public ResponseModel() {
    }

    public ResponseModel(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
