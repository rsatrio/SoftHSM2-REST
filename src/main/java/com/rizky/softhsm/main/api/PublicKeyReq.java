package com.rizky.softhsm.main.api;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicKeyReq {

    @JsonProperty
    @NotEmpty(message="cannot be empty")
    @Length(max=1000,message="not more than 1000 characters")
    private String key_id;

    @JsonProperty
    @NotEmpty(message="cannot be empty")
    @Length(max=10000,message="not more than 10Kb")
    private String hsm_slot_id;

    @JsonProperty
    @NotEmpty(message="cannot be empty")
    @Length(max=300,message="not more than 300 characters")
    private String hsm_pin;



    public String getHsm_slot_id() {
        return hsm_slot_id;
    }

    public void setHsm_slot_id(String hsm_slot_id) {
        this.hsm_slot_id = hsm_slot_id;
    }

    public String getHsm_pin() {
        return hsm_pin;
    }

    public void setHsm_pin(String hsm_pin) {
        this.hsm_pin = hsm_pin;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }



}
