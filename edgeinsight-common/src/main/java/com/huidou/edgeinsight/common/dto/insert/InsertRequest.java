package com.huidou.edgeinsight.common.dto.insert;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class InsertRequest {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String realName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private List<Integer> roleIds;
}
