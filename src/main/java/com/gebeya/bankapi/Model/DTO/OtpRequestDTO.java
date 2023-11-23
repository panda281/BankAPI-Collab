package com.gebeya.bankapi.Model.DTO;

public class OtpRequestDTO {
    private String username;
    private String password;
    private String to;
    private String message;
    private String template_id;

    public OtpRequestDTO()
    {

    }
    public OtpRequestDTO(String username, String password, String to, String message, String template_id) {
        this.username = username;
        this.password = password;
        this.to = to;
        this.message = message;
        this.template_id = template_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    @Override
    public String toString() {
        return "OtpRequestDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", template_id='" + template_id + '\'' +
                '}';
    }
}
