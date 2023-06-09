/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 
 * According to cos feature, we modify some class，comment, field name, etc.
 */


package com.qcloud.cos.internal.crypto;

import java.io.Serializable;
import java.security.Provider;

/**
 * Stores configuration parameters that will be used during encryption and
 * decryption by the COS Encryption Client. With this object, you can set
 * the encryption client to use Instruction Files or Object Metadata for storing
 * encryption information. You can also specify your own crypto provider to be
 * used during encryption and decryption.
 */
public class CryptoConfiguration implements Cloneable,Serializable {

    private static final long serialVersionUID = -8646831898339939580L;

    private CryptoMode cryptoMode;
    private CryptoStorageMode storageMode;
    private Provider cryptoProvider;
    /**
     * True to ignore instruction file that cannot be found during a GET
     * operation; false otherwise. Default is true. This property is ignored if
     * the crypto mode is {@link CryptoMode#StrictAuthenticatedEncryption} where
     * missing instruction file would always cause security exception.
     */
    private boolean ignoreMissingInstructionFile = true;

    /**
     * Used to specify the KMS region for the KMS client when such client
     * is internally instantiated instead of externally passed in by users; or
     * null if no explicit KMS region is specified.
     */
    private transient String kmsRegion;

    /**
     * Used to specify user defined iv.
     */
    private byte[] iv = null;

    /**
     * Creates a new CryptoConfiguration object with default storage mode and
     * crypto provider settings. The default storage mode is the Object Metadata
     * storage mode, and the default crypto provider is the JCE provider.
     */
    public CryptoConfiguration() {
        this(CryptoMode.AuthenticatedEncryption); // default to Authenticated encryption (AE)
    }

    /**
     * @param cryptoMode
     *            cryptographic mode to be used
     * 
     * @throws UnsupportedOperationException
     *             if the necessary security provider cannot be found or the
     *             necessary cryptographic operations are not supported for the
     *             specified crypto mode.
     */
    public CryptoConfiguration(CryptoMode cryptoMode) {
        check(cryptoMode);
        // By default, store encryption info in metadata
        this.storageMode = CryptoStorageMode.ObjectMetadata;
        // A null value implies that the default JCE crypto provider will be
        // used
        this.cryptoProvider = null;
        this.cryptoMode = cryptoMode;
    }

    /**
     * Sets the storage mode to the specified mode.
     * 
     * @param storageMode
     *            The storage mode to be used for storing encryption
     *            information.
     */
    public void setStorageMode(CryptoStorageMode storageMode) {
        this.storageMode = storageMode;
    }

    /**
     * Sets the storage mode to the specified mode, and returns the updated
     * CryptoConfiguration object.
     * 
     * @param storageMode
     *            The storage mode to be used for storing encryption
     *            information.
     * @return The updated CryptoConfiguration object.
     */
    public CryptoConfiguration withStorageMode(CryptoStorageMode storageMode) {
        this.storageMode = storageMode;
        return this;
    }

    /**
     * Returns the current storage mode of a CryptoConfiguration object.
     * 
     * @return The storage mode to be used for storing encryption information.
     */
    public CryptoStorageMode getStorageMode() {
        return this.storageMode;
    }

    /**
     * Sets the crypto provider to the specified provider.
     * 
     * @param cryptoProvider
     *            The crypto provider whose encryption implementation will be
     *            used to encrypt and decrypt data.
     */
    public void setCryptoProvider(Provider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    /**
     * Sets the crypto provider to the specified provider, and returns the
     * updated CryptoConfiguration object.
     * 
     * @param cryptoProvider
     *            The crypto provider whose encryption implementation will be
     *            used to encrypt and decrypt data.
     * @return The updated CryptoConfiguration object.
     */
    public CryptoConfiguration withCryptoProvider(Provider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
        return this;
    }

    /**
     * Returns the crypto provider whose encryption implementation will be used
     * to encrypt and decrypt data.
     * 
     * @return the crypto provider whose encryption implementation will be used
     *         to encrypt and decrypt data.
     */
    public Provider getCryptoProvider() {
        return this.cryptoProvider;
    }

    /**
     * Returns the optionally specified crypto mode applicable only to the COS
     * encryption client; or null. This attribute is ignored if the COS
     * encryption client is not in use.
     */
    public CryptoMode getCryptoMode() {
        return cryptoMode;
    }

    /**
     * Sets the crypto mode; applicable only to the COS encryption client.
     * 
     * @throws UnsupportedOperationException
     *             if the necessary security provider cannot be found or the
     *             necessary cryptographic operations are not supported for the
     *             specified crypto mode. Note the crypto mode can and will
     *             still (intentionally) be set in such case, and it's up to the
     *             caller to decide what to do about it.
     */
    public void setCryptoMode(CryptoMode cryptoMode)
            throws UnsupportedOperationException {
        this.cryptoMode = cryptoMode;
        check(cryptoMode);
    }

    /**
     * Fluent API to set the crypto mode; applicable only to the COS encryption
     * client.
     * 
     * @throws UnsupportedOperationException
     *             if the necessary security provider cannot be found or the
     *             necessary cryptographic operations are not supported for the
     *             specified crypto mode.Note the crypto mode can and will still
     *             (intentionally) be set in such case, and it's up to the
     *             caller to decide what to do about it.
     */
    public CryptoConfiguration withCryptoMode(CryptoMode cryptoMode)
            throws UnsupportedOperationException {
        this.cryptoMode = cryptoMode;
        check(cryptoMode);
        return this;
    }

    /**
     * Returns true to ignore instruction file that cannot be found during a GET
     * operation; false otherwise. Default is true. This property is ignored if
     * the crypto mode is {@link CryptoMode#StrictAuthenticatedEncryption} where
     * missing instruction file would always cause security exception.
     */
    public boolean isIgnoreMissingInstructionFile() {
        return ignoreMissingInstructionFile;
    }

    /**
     * @param ignoreMissingInstructionFile
     *            true to ignore instruction file that cannot be found during a
     *            GET operation; false otherwise. Default is true. This property
     *            is ignored if the crypto mode is
     *            {@link CryptoMode#StrictAuthenticatedEncryption} where missing
     *            instruction file would always cause security exception.
     */
    public void setIgnoreMissingInstructionFile(
            boolean ignoreMissingInstructionFile) {
        this.ignoreMissingInstructionFile = ignoreMissingInstructionFile;
    }

    /**
     * Fluent API to set the property to ignore instruction file that cannot be
     * found during a GET operation.
     */
    public CryptoConfiguration withIgnoreMissingInstructionFile(
            boolean ignoreMissingInstructionFile) {
        this.ignoreMissingInstructionFile = ignoreMissingInstructionFile;
        return this;
    }

    /**
     * Checks if the crypto mode is supported by the runtime.
     * 
     * @throws UnsupportedOperationException
     *             if the necessary security provider cannot be found or the
     *             necessary cryptographic operations are not supported for the
     *             specified crypto mode.
     */
    private void check(CryptoMode cryptoMode) {
        if (cryptoMode == CryptoMode.AuthenticatedEncryption
                || cryptoMode == CryptoMode.StrictAuthenticatedEncryption) {
            if (!CryptoRuntime.isBouncyCastleAvailable()) {
                CryptoRuntime.enableBouncyCastle();
                if (!CryptoRuntime.isBouncyCastleAvailable()) {
                    throw new UnsupportedOperationException(
                            "The Bouncy castle library jar is required on the classpath to enable authenticated encryption");
                }
            }
            if (!CryptoRuntime.isAesGcmAvailable())
                throw new UnsupportedOperationException(
                        "More recent version of the Bouncy castle library is required to enable authenticated encryption");
        }
    }

    public boolean isReadOnly() { return false; }

    /**
     * Used to provide a read-only copy of the configuration.
     */
    private static final class ReadOnly extends CryptoConfiguration {
        private static final long serialVersionUID = -7579268925296074735L;
        private ReadOnly() {}
        @Override public boolean isReadOnly() { return true; }
        @Override public void setStorageMode(CryptoStorageMode storageMode) {
            throw new UnsupportedOperationException();
        }
        @Override public CryptoConfiguration withStorageMode(CryptoStorageMode storageMode) {
            throw new UnsupportedOperationException();
        }
        @Override public void setCryptoProvider(Provider cryptoProvider) {
            throw new UnsupportedOperationException();
        }
        @Override public CryptoConfiguration withCryptoProvider(Provider cryptoProvider) {
            throw new UnsupportedOperationException();
        }
        @Override public void setCryptoMode(CryptoMode cryptoMode) {
            throw new UnsupportedOperationException();
        }
        @Override public CryptoConfiguration withCryptoMode(CryptoMode cryptoMode) {
            throw new UnsupportedOperationException();
        }
        @Override public void setIgnoreMissingInstructionFile(
                boolean ignoreMissingInstructionFile) {
            throw new UnsupportedOperationException();
        }
        @Override public CryptoConfiguration withIgnoreMissingInstructionFile(
                boolean ignoreMissingInstructionFile) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns a read-only copy of this configuration.
     */
    public CryptoConfiguration readOnly() {
        if (isReadOnly())
            return this;
        return copyTo(new CryptoConfiguration.ReadOnly());
    }

    @Override
    public CryptoConfiguration clone() {
        return copyTo(new CryptoConfiguration());
    }

    private CryptoConfiguration copyTo(CryptoConfiguration that) {
        that.cryptoMode = this.cryptoMode;
        that.storageMode = this.storageMode;
        that.cryptoProvider = this.cryptoProvider;
        that.ignoreMissingInstructionFile = this.ignoreMissingInstructionFile;
        that.iv = this.iv;
        return that;
    }

    /**
     * Returns the the KMS region explicitly specified for the KMS client
     * when such client is internally instantiated; or null if no explicit KMS
     * region is specified. This KMS region parameter is ignored when the AWS
     * KMS client of the encryption client is explicitly passed in by the
     * users, instead of being implicitly created.
     */
    public String getKmsRegion() {
        return kmsRegion;
    }

    /**
     * Sets the KMS region for the KMS client when such client is internally
     * instantiated instead of externally passed in by users; or null if no
     * explicit KMS region is explicitly configured.This KMS region parameter is
     * ignored when the KMS client of the encryption client is explicitly
     * passed in by the users, instead of being implicitly created.
     */
    public void setKmsRegion(String kmsRegion) {
        this.kmsRegion = kmsRegion;
    }

    /**
     * Fluent API for setting the KMS region for the KMS client when such
     * client is internally instantiated instead of externally passed in by
     * users; or null if no explicit KMS region is explicitly configured.This
     * KMS region parameter is ignored when the KMS client of the encryption
     * client is explicitly passed in by the users, instead of being
     * implicitly created.
     */
    public CryptoConfiguration withKmsRegion(String kmsRegion) {
        this.kmsRegion = kmsRegion;
        return this;
    }

    public byte[] getIV() {
        return this.iv;
    }

    public void setIV(byte[] iv) {
        this.iv = iv;
    }

    public CryptoConfiguration withIV(byte[] iv) {
        this.iv = iv;
        return this;
    }
}