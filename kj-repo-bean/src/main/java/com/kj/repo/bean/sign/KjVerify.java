package com.kj.repo.bean.sign;

public interface KjVerify {

    boolean verify(byte[] src, byte[] sign) throws Exception;

}
