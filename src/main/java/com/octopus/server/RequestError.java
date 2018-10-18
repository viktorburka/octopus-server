package com.octopus.server;

class RequestError {

    private String message;

    RequestError(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
