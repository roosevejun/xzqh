package com.tongtu.xzqh.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "ahui_code_2021")
public class AhuiCode2021 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区划代码
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * 名称
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 级别1-5,省市县镇村
     */
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * 父级区划代码
     */
    @Column(name = "pcode")
    private String pcode;

    /**
     * 城乡分类代码
     */
    @Column(name = "cxfxdm")
    private String cxfxdm;

}
