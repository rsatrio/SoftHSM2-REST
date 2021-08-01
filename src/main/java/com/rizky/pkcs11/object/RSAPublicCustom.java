package com.rizky.pkcs11.object;

import iaik.pkcs.pkcs11.objects.RSAPublicKey;

public class RSAPublicCustom extends RSAPublicKey {

    public RSAPublicCustom(String label,int keyLength) {
        super();
        byte[] publicExponentBytes = { 0x01, 0x00, 0x01 };
        super.getModulusBits().setLongValue(new Long(keyLength));
        super.getPublicExponent().setByteArrayValue(publicExponentBytes);
        super.getToken().setBooleanValue(true);
        super.getWrap().setBooleanValue(false);
        super.getLabel().setCharArrayValue(label.toCharArray());        

    }


}
