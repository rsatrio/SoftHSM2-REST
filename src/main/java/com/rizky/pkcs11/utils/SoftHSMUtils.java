package com.rizky.pkcs11.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;

public class SoftHSMUtils {

    public static Token getToken(Module pkcs11,boolean exit,
            int slotNumber,int timeout) throws Exception   {


        Future<Token> futureToken=null;
        Token token=null;
        try {
            Slot[] slotWithTokens=pkcs11.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
            int nomor=0;
            
            while(nomor<slotWithTokens.length)  {
                if(slotWithTokens[nomor].getSlotID()==Long.valueOf(slotNumber)) {
                    slotNumber=nomor;
                    break;
                }
                nomor++;
            }
            
            ExecutorService executor1=Executors.newSingleThreadExecutor();
            int slot2=slotNumber;
            futureToken=executor1.submit(()->slotWithTokens[slot2].getToken());
            token=futureToken.get(timeout, 
                    TimeUnit.SECONDS);
            return token;
        }
        catch(Exception e) {

            if(futureToken!=null)   {
                futureToken.cancel(true);
            }

            LoggerFactory.getLogger(SoftHSMUtils.class)
            .info("Cannot connect to SoftHSM, please check HSM condition.",e);
            if(exit)    {
                LoggerFactory.getLogger(SoftHSMUtils.class).info("Exiting application...");               
                System.exit(10);
            }
            throw e;
        }

    }

}
