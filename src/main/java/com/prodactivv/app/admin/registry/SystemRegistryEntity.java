package com.prodactivv.app.admin.registry;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SystemRegistryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String regKey;

    @Column
    private String value;
}