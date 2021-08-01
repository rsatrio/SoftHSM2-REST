package com.rizky.core.exceptions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.model.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.softhsm.api.CustomRespGenericV1;

import io.dropwizard.jersey.validation.ConstraintMessage;
import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;

@Provider
public class CustomBeanValidationExceptionMapper implements ExceptionMapper<JerseyViolationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyViolationExceptionMapper.class);

    @Override
    public Response toResponse(final JerseyViolationException exception) {

        LOGGER.debug("Object validation failure", exception);

        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final Invocable invocable = exception.getInvocable();
        final List<String> errors = exception.getConstraintViolations().stream()
                .map(violation -> ConstraintMessage.getMessage(violation, invocable))
                .collect(Collectors.toList());

        final int status = ConstraintMessage.determineStatus(violations, invocable);
        
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        resp.setSuccess(false);
        resp.setMessage("Failed Validationa");
        resp.setData(errors);
        return Response.status(status)
                .entity(resp)
                .build();
    }
}