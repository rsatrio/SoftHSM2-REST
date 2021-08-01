package com.rizky.softhsm.main.api;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRSAKeyReq {
	
	@JsonProperty
	@NotEmpty(message="cannot be empty")
	@Length(max=10000,message="not more than 10Kb")
	private String hsm_slot_id;
	
	@JsonProperty
	@NotEmpty(message="cannot be empty")
	@Length(max=300,message="not more than 300 characters")
	private String hsm_pin;
	
	@JsonProperty
    @NotEmpty(message="cannot be empty")
    @Length(max=1000,message="not more than 1000 characters")
    private String label;
	
	@JsonProperty
    @NotEmpty(message="cannot be empty")
    @Length(max=5,message="not more than 5 characters")
    private String key_length;

   

    public String getKey_length() {
        return key_length;
    }

    public void setKey_length(String key_length) {
        this.key_length = key_length;
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
	
	


}
