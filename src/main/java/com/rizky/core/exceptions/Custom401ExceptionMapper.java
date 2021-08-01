package com.rizky.core.exceptions;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.rizky.softhsm.api.CustomRespGenericV1;

import io.dropwizard.auth.UnauthorizedHandler;

public class Custom401ExceptionMapper implements UnauthorizedHandler,ExceptionMapper<NotAuthorizedException> {


    @Override
    public Response buildResponse(String arg0, String arg1) {
        // TODO Auto-generated method stub
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        resp.setSuccess(false);
        resp.setMessage("Unauthenticated");

        return Response.status(401)
                .entity(resp)
                .build();
    }

    @Override
    public Response toResponse(NotAuthorizedException arg0) {
        // TODO Auto-generated method stub
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        resp.setSuccess(false);
        resp.setMessage("Unauthenticated");

        return Response.status(401)
                .entity(resp)
                .build();
    }



}
