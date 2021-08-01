package com.rizky.softhsm.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.core.exceptions.Custom401ExceptionMapper;
import com.rizky.core.exceptions.Custom403ExceptionMapper;
import com.rizky.core.exceptions.Custom500ExceptionMapper;
import com.rizky.core.exceptions.Custom500OtherExceptionMapper;
import com.rizky.core.exceptions.CustomBeanValidationExceptionMapper;
import com.rizky.softhsm.main.resources.SoftHSMRestResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class SoftHSMApplication extends Application<SoftHSMRestConfig> {

    Logger log1=LoggerFactory.getLogger(SoftHSMApplication.class);

    public static void main(final String[] args) throws Exception {
        new SoftHSMApplication().run(args);
    }

    @Override
    public String getName() {
        return "SoftHSM REST";
    }

    @Override
    public void initialize(final Bootstrap<SoftHSMRestConfig> bootstrap) {
        // TODO: application initialization
        bootstrap.addBundle(new SwaggerBundle<SoftHSMRestConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(SoftHSMRestConfig configuration) {
                return configuration.getSwagger();
            }
        });
    }


    @Override
    public void run(SoftHSMRestConfig config, Environment environment)
            throws Exception {

        //Initialize Singleton
        SoftHSMAppSingleton.getSingleton().setConfig(config);


        //Register Custom Validation Exception
        environment.jersey().register(new CustomBeanValidationExceptionMapper());
        environment.jersey().register(new Custom403ExceptionMapper());
        environment.jersey().register(new Custom401ExceptionMapper());
        environment.jersey().register(new Custom500ExceptionMapper());
        environment.jersey().register(new Custom500OtherExceptionMapper());
        
        //Register resource
        environment.jersey().register(new SoftHSMRestResource());


    }}
