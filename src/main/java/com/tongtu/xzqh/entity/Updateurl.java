package com.tongtu.xzqh.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "updateurl")
public class Updateurl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "upurl", nullable = false)
    private String url;

    @Column(name = "isup")
    private Boolean isup;

}
