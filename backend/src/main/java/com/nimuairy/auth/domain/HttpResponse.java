package com.nimuairy.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class HttpResponse {

    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String message;
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date timeStamp;

    public HttpResponse() {

    }

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String message, String reason) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.reason = reason;
        this.timeStamp = new Date();
    }
}
