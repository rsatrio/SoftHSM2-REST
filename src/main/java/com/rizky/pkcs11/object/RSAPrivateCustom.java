package com.rizky.pkcs11.object;

import iaik.pkcs.pkcs11.objects.RSAPrivateKey;

public class RSAPrivateCustom extends RSAPrivateKey {


    public RSAPrivateCustom(String label) {

        super();
        super.getToken().setBooleanValue(true);
        super.getLabel().setCharArrayValue(label.toCharArray());
        super.getPrivate().setBooleanValue(true);
        super.getSensitive().setBooleanValue(true);
        super.getUnwrap().setBooleanValue(false);        
        
    }
   



}
