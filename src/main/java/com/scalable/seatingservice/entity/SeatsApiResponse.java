package com.scalable.seatingservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatsApiResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    private String code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public SeatsApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
