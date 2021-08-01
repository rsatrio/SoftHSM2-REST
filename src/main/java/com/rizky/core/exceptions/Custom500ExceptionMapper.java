package com.rizky.core.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.softhsm.api.CustomRespGenericV1;

public class Custom500ExceptionMapper implements ExceptionMapper<WebApplicationException>{

    Logger log1=LoggerFactory.getLogger(Custom500ExceptionMapper.class);
    @Override
    public Response toResponse(WebApplicationException arg0) {
        // TODO Auto-generated method stub
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        resp.setSuccess(false);
        resp.setMessage("Internal Server Error");
        
        if(log1.isDebugEnabled())   {
            log1.debug("Internal Server Error:",arg0);
        }
        return Response.status(500)
                .entity(resp)
                .build();
    }



}
