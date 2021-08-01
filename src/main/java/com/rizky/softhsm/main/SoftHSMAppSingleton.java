package com.rizky.softhsm.main;

import org.slf4j.LoggerFactory;

import iaik.pkcs.pkcs11.Module;

public class SoftHSMAppSingleton {

    private static SoftHSMAppSingleton mySingleton;
    private SoftHSMRestConfig config;
    private Module pkcs11;

    private SoftHSMAppSingleton() {

        LoggerFactory.getLogger(SoftHSMAppSingleton.class).info("Sign Server Singleton Created");

    }


    public Module getPkcs11() {
        return pkcs11;
    }


    void setPkcs11(Module pkcs11) {
        this.pkcs11 = pkcs11;
    }


    public static SoftHSMAppSingleton getSingleton()  {
        if(mySingleton==null)   {
            mySingleton=new SoftHSMAppSingleton();
        }

        return mySingleton;

    }

    public SoftHSMRestConfig getConfig() {
        return config;
    }

    void setConfig(SoftHSMRestConfig config) {
        this.config = config;
    }



}
