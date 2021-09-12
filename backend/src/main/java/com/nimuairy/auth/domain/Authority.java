package com.nimuairy.auth.domain;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private EAuthority name;

    public Authority() {
    }

    public Authority(EAuthority name) {
        this.name = name;
    }
}
