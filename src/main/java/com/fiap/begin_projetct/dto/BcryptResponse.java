package com.fiap.begin_projetct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BcryptResponse {
    private String senhaOriginal;
    private String hashBcrypt;
}
