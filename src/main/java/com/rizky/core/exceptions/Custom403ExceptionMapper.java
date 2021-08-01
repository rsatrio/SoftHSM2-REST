package com.rizky.core.exceptions;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.rizky.softhsm.api.CustomRespGenericV1;

public class Custom403ExceptionMapper implements ExceptionMapper<ForbiddenException> {
    
    @Override
    public Response toResponse(ForbiddenException arg0) {
        // TODO Auto-generated method stub
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        resp.setSuccess(false);
        resp.setMessage("Unauthorized");
        
        return Response.status(403)
                .entity(resp)
                .build();
    }

}
