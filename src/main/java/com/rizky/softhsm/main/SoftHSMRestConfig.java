package com.rizky.softhsm.main;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.*;

import javax.validation.constraints.*;



public class SoftHSMRestConfig extends Configuration {

    private final SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

}
