/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.facebook.crypto.keychain;

import java.security.SecureRandom;
import java.util.Arrays;

import android.content.Context;
import android.util.Base64;

import com.facebook.crypto.cipher.NativeGCMCipher;
import com.facebook.crypto.exception.KeyChainException;

/**
 * Use user's defined publicBase64Key, that would not be saved into preference
 * @param context
 * @param publicBase64Key Can encode from any support site, ex.http://www.motobit.com/util/base64-decoder-encoder.asp
 */
public class MyKeyChain implements KeyChain {


  private final SecureRandom mSecureRandom;

  protected byte[] mCipherKey;
  protected boolean mSetCipherKey;
  private String publicKey;

  private static final SecureRandomFix sSecureRandomFix = new SecureRandomFix();

  
  public MyKeyChain(Context context,String publicBase64Key) {
    mSecureRandom = new SecureRandom();
    this.publicKey=publicBase64Key;
  }

  @Override
  public synchronized byte[] getCipherKey() throws KeyChainException {
    if (!mSetCipherKey) {
      mCipherKey = decodeKey(publicKey);
    }
    mSetCipherKey = true;
    return mCipherKey;
  }

  @Override
  public byte[] getMacKey() throws KeyChainException {
    return null;
  }

  @Override
  public byte[] getNewIV() throws KeyChainException {
    sSecureRandomFix.tryApplyFixes();
    byte[] iv = new byte[NativeGCMCipher.IV_LENGTH];
    mSecureRandom.nextBytes(iv);
    return iv;
  }

  @Override
  public synchronized void destroyKeys() {
    mSetCipherKey = false;
    Arrays.fill(mCipherKey, (byte) 0);
    mCipherKey = null;
  }



  private byte[] decodeKey(String keyString) {
    if (keyString == null) {
      return null;
    }
    return Base64.decode(keyString, Base64.DEFAULT);
  }

}
