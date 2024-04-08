package com.example.demo.Pojo;

public class Result {
    private String status;
    private String message;
    private Object data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setStatus("success");
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.setStatus("error");
        result.setMessage(message);
        return result;
    }
}
