package com.mavs.userservice.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUserDto {

    private String email;
    private String phone;
    private String username;

}
