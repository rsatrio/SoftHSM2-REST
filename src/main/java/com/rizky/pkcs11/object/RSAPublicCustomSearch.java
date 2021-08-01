package com.rizky.pkcs11.object;

import iaik.pkcs.pkcs11.objects.RSAPublicKey;

public class RSAPublicCustomSearch extends RSAPublicKey {

    public RSAPublicCustomSearch(String label) {
        super();
        byte[] publicExponentBytes = { 0x01, 0x00, 0x01 };        
        super.getPublicExponent().setByteArrayValue(publicExponentBytes);
        super.getToken().setBooleanValue(true);
        super.getWrap().setBooleanValue(false);
        super.getLabel().setCharArrayValue(label.toCharArray());        

    }


}
