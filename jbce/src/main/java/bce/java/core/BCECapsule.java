package bce.java.core;

import java.io.Serializable;

/**
 * This interface defined the specifications of BCE encryption
 * data structure, assuming it is a capsule, where data comes
 * in, and gets protect. You can retrieve the original data
 * with the valid key from the capsule.
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 */
public interface BCECapsule extends Serializable, BCEIOSpec {

    /**
     * Store the raw data for encryption
     * @param data data to be encrypted
     */
    void protect(byte[] data);

    /**
     * Store a serializable object for encryption
     * @param object serialzable object to be encrypted
     */
    void protect(Serializable object);

    /**
     * Retrieve the original raw data
     */
    byte[] getData();

    /**
     * Retrieve the original serializable object
     */
    Object getDataAsObject() throws ClassNotFoundException;

    /**
     * Set the encryption key
     */
    void setKey(byte[] key);

    /**
     * Return the algorithm name of the Hashing function
     * used in the implementation
     */
    String getHashAlgorithm();

    /**
     * Return the algorithm name of the encryption name
     */
    String getCrypto();

    /**
     * Purge the secret data in the current object
     */
    void abort();
}
