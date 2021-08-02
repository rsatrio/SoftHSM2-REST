package com.rizky.softhsm.main.resources;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.pkcs11.object.RSAPrivateCustom;
import com.rizky.pkcs11.object.RSAPublicCustom;
import com.rizky.pkcs11.object.RSAPublicCustomSearch;
import com.rizky.pkcs11.utils.SoftHSMUtils;
import com.rizky.softhsm.api.CustomRespGenericV1;
import com.rizky.softhsm.main.api.CreateRSAKeyReq;
import com.rizky.softhsm.main.api.PublicKeyReq;
import com.rizky.softhsm.main.api.SoftHsmSignReq;

import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.objects.KeyPair;
import iaik.pkcs.pkcs11.objects.PKCS11Object;
import iaik.pkcs.pkcs11.objects.PrivateKey;
import iaik.pkcs.pkcs11.objects.RSAPrivateKey;
import iaik.pkcs.pkcs11.objects.RSAPublicKey;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("API for SoftHSM")
@Path("/v1/softhsm")
@Produces(MediaType.APPLICATION_JSON)

public class SoftHSMRestResource {

    private Logger log1=LoggerFactory.getLogger(SoftHSMRestResource.class);
    private Logger auditLog=LoggerFactory.getLogger("auditTrail");

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("sign-data")
    @ApiOperation(value = "Data Digital Signing", 
    notes = "Returns signed data",
    response=CustomRespGenericV1.class)
    @ApiResponses(value = {
            @ApiResponse (code = 200, response=CustomRespGenericV1.class,message = "{success:true,message:,data:[docSigned:10,etc]}"),
    })
    public CustomRespGenericV1 softHsmSigning(@ApiParam(required=true) @Valid @NotNull SoftHsmSignReq req   
            ,@ApiParam(required=false,hidden=true,allowEmptyValue=true) @Context HttpServletRequest servReq)    
    {
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        Session sessP11=null;
        log1.info("Receive request {}",req.getKey_id());
        try {

            Module moduleP11=Module.getInstance("/usr/local/lib/softhsm/libsofthsm2.so");

            moduleP11.initialize(null);

            Integer slotNumber=Integer.parseInt(req.getHsm_slot_id());
            Integer timeoutSeconds=Integer.parseInt("5");
            Token token=SoftHSMUtils.getToken(moduleP11,true,slotNumber,timeoutSeconds);

            sessP11=token.openSession(Token.SessionType.SERIAL_SESSION,
                    Token.SessionReadWriteBehavior.RO_SESSION, null, null);

            try {
                sessP11.login(Session.UserType.USER, req.getHsm_pin().toCharArray());
            }
            catch(Exception ex)    {
                ex.printStackTrace();
            }
            RSAPrivateKey rsaPrivateKeyTemplate = new RSAPrivateCustom(req.getKey_id());

            sessP11.findObjectsInit(rsaPrivateKeyTemplate);

            PKCS11Object[] objFound=sessP11.findObjects(1);

            if(log1.isDebugEnabled())
                log1.debug("Key Found {} for KeyId {}",objFound.length,req.getKey_id());

            if(objFound.length<1)   {
                sessP11.findObjectsFinal();
                resp.setSuccess(false);
                resp.setMessage("Key Not Found");
                return resp;
            }

            PrivateKey privKey1=null;
            privKey1=(PrivateKey) objFound[0];
            sessP11.findObjectsFinal();

            //Start signing
            sessP11.signInit(Mechanism.get(64L), privKey1);

            byte[] dataSigned=sessP11.sign(Base64.getDecoder().decode(req.getData_to_sign()));
            if(log1.isDebugEnabled())
                log1.debug("Signing Success for KeyId {}",req.getKey_id());

            //Logging Trail
            auditLog.info("{},{},{}",
                    Instant.now().atZone(ZoneId.of("Asia/Jakarta")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME), //timestamp
                    req.getData_to_sign(), //data to sign
                    req.getKey_id() //Key ID
                    );

            //Prepare response
            resp.setSuccess(true);
            List<String> data1=Arrays.asList(Base64.getEncoder().encodeToString(dataSigned));
            resp.setData(data1);
            resp.setMessage("Signing success");

            return resp;

        }
        catch(Exception e)  {
            log1.error("Digital Sign Failed",e);
            resp.setSuccess(false);
            resp.setMessage("Signing Failed");
            return resp;
        }
        finally {
            if(sessP11!=null)    {
                try {
                    sessP11.closeSession();
                    if(log1.isDebugEnabled())
                        log1.debug("Closing PKCS11 Session");
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }


    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("create-key")
    @ApiOperation(value = "Create RSA Key", 
    notes = "Returns success/not",
    response=CustomRespGenericV1.class)
    @ApiResponses(value = {
            @ApiResponse (code = 200, response=CustomRespGenericV1.class,message = "{success:true,message:,data:[Modulus PubKey,etc]}"),
    })
    public CustomRespGenericV1 createKey(@ApiParam(required=true) @Valid @NotNull CreateRSAKeyReq req   
            ,@ApiParam(required=false,hidden=true,allowEmptyValue=true) @Context HttpServletRequest servReq)    
    {

        CustomRespGenericV1 resp=new CustomRespGenericV1();
        Session sessionP11=null;
        String errorMsg="";

        try {
            Module pkcs11=Module.getInstance("/usr/local/lib/softhsm/libsofthsm2.so");

            pkcs11.initialize(null);

            if(!req.getKey_length().equals("1024") && 
                    !req.getKey_length().equals("2048") 
                    && !req.getKey_length().equals("4096")) {
                errorMsg="Key Length must be one of: 1024,2048,4096";
                throw new RuntimeException("Wrong Key Length");
            }

            Integer slotNumber=Integer.parseInt(req.getHsm_slot_id());
            Integer timeoutSeconds=Integer.parseInt("5");
            Token token=SoftHSMUtils.getToken(pkcs11,true,slotNumber,timeoutSeconds);

            sessionP11=token.openSession(Token.SessionType.SERIAL_SESSION,
                    Token.SessionReadWriteBehavior.RW_SESSION, null, null);

            sessionP11.login(Session.UserType.USER, req.getHsm_pin().toCharArray());            

            RSAPublicCustom rsaPublicKeyTemplate = new RSAPublicCustom(req.getLabel(),
                    Integer.parseInt(req.getKey_length().trim()));
            RSAPrivateCustom rsaPrivateKeyTemplate = new RSAPrivateCustom(req.getLabel());

            KeyPair generatedKeyPair = sessionP11.generateKeyPair
                    (Mechanism.get(PKCS11Constants.CKM_RSA_PKCS_KEY_PAIR_GEN),
                            rsaPublicKeyTemplate, rsaPrivateKeyTemplate);

            resp.setSuccess(true);
            resp.setMessage("KeyLabel "+req.getLabel()+" created");
            return resp;
        }
        catch(Exception e)  {
            e.printStackTrace();
            resp.setSuccess(false);
            resp.setMessage(errorMsg);
            return resp;
        }
        finally {
            try {
                sessionP11.closeSession();
            }
            catch(Exception e)  {
                e.printStackTrace();
            }
        }
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("get-public-key")
    @ApiOperation(value = "Retrieve RSA Public Key", 
    notes = "Returns Base64 of Public Key",
    response=CustomRespGenericV1.class)
    @ApiResponses(value = {
            @ApiResponse (code = 200, response=CustomRespGenericV1.class,message = "{success:true,message:,data:[docSigned:10,etc]}"),
    })
    public CustomRespGenericV1 getPublicKey(@ApiParam(required=true) @Valid @NotNull PublicKeyReq req   
            ,@ApiParam(required=false,hidden=true,allowEmptyValue=true) @Context HttpServletRequest servReq)    
    {
        CustomRespGenericV1 resp=new CustomRespGenericV1();
        Session sessionP11=null;
        log1.info("Receive request {}",req.getKey_id());
        try {

            Module pkcs11=Module.getInstance("/usr/local/lib/softhsm/libsofthsm2.so");

            pkcs11.initialize(null);

            Integer slotNumber=Integer.parseInt(req.getHsm_slot_id());
            Integer timeoutSeconds=Integer.parseInt("5");
            Token token=SoftHSMUtils.getToken(pkcs11,true,slotNumber,timeoutSeconds);

            sessionP11=token.openSession(Token.SessionType.SERIAL_SESSION,
                    Token.SessionReadWriteBehavior.RO_SESSION, null, null);

            sessionP11.login(Session.UserType.USER, req.getHsm_pin().toCharArray()); 
            RSAPublicCustomSearch rsaPubKeyTemplate = new RSAPublicCustomSearch(req.getKey_id());

            sessionP11.findObjectsInit(rsaPubKeyTemplate);

            PKCS11Object[] objFound=sessionP11.findObjects(1);

            if(log1.isDebugEnabled())
                log1.debug("Key Found {} for KeyId {}",objFound.length,req.getKey_id());

            if(objFound.length<1)   {
                sessionP11.findObjectsFinal();
                resp.setSuccess(false);
                resp.setMessage("Key Not Found");
                return resp;
            }

            RSAPublicKey pKey=(RSAPublicKey) objFound[0];
            BigInteger intMod=new BigInteger(1,pKey.getModulus().getByteArrayValue());
            BigInteger intPexp=new BigInteger(1,pKey.getPublicExponent().getByteArrayValue());
            RSAPublicKeySpec pkSpec=new RSAPublicKeySpec(intMod, intPexp);
            KeyFactory kf=KeyFactory.getInstance("RSA");
            PublicKey pKey2=kf.generatePublic(pkSpec);

            sessionP11.findObjectsFinal();

            //Prepare response
            resp.setSuccess(true);
            List<String> data1=Arrays.asList(Base64.getEncoder().encodeToString(pKey2.getEncoded()));
            resp.setData(data1);
            resp.setMessage("Successfully finding PublicKey Label "+req.getKey_id());

            return resp;

        }
        catch(Exception e)  {
            log1.error("Failed finding publickey label {}",req.getKey_id(),e);
            resp.setSuccess(false);
            resp.setMessage("Failed");
            return resp;
        }
        finally {
            if(sessionP11!=null)    {
                try {
                    sessionP11.closeSession();
                    if(log1.isDebugEnabled())
                        log1.debug("Closing PKCS11 Session");
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
