package com.georeference.appregca.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CM_USER")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "CD_USER")
    private Long id;
    @Column(name = "NU_ID_USER")
    private String nuIdUser;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "TX_CREATION_USER")
    private String txCreationUser;
    @Column(name = "DT_CREATION")
    private String dtCreation;
    @Column(name = "TX_UPDATE_USER")
    private String txUpdateUser;
    @Column(name = "DT_UPDATE")
    private String dtUpdate;
    @Column(name = "TX_CHANGE_PASSWORD")
    private String txChangePassword;
    @Column(name = "TX_EMAIL")
    private String txEmail;
    @Column(name = "CONFIRMATION_TOKEN")
    private String confirmationToken;
    @Column(name = "ESTADO")
    private Boolean status;
    @Column(name = "SECOND_FIRSTNAME")
    private String secondFirstName;
    @Column(name = "SECOND_LASTNAME")
    private String secondLastName;
    @Column(name = "NU_TRY_LOGIN")
    private String nuTryLogin;
    @Column(name = "DT_BLOCK_LOGIN")
    private String dtBlockLogin;
    @Column(name = "CD_VALID_RECAPTCHA")
    private String cdValidRecaptcha;
}
