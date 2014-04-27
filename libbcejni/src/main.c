#include "robins_bce_jni_BCENative.h"

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_setup(JNIEnv *env, jclass obj, jbyteArray curve_file_name, jint num_user, jbyteArray sys_params_path, jbyteArray global_params_path, jbyteArray sys_priv_key_out)
{
    jint length;
    byte *cfn;
    byte *param_path;
    byte *priv_key;
    byte *global_path;
    int retval;

    length = (*env)->GetArrayLength(env, curve_file_name);
    cfn = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, curve_file_name, 0, length, (jbyte *) cfn);
    cfn[length] = '\0';

    length = (*env)->GetArrayLength(env, sys_params_path);
    param_path = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, sys_params_path, 0, length, (jbyte *) param_path);
    param_path[length] = '\0';

    length = (*env)->GetArrayLength(env, global_params_path);
    global_path = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, global_params_path, 0, length, (jbyte *) global_path);
    global_path[length] = '\0';

    length = (*env)->GetArrayLength(env, sys_priv_key_out);
    if (length != BCE_ZR_LENGTH)
        return -4;
    priv_key = (byte *) malloc(length);

    retval = BCESetup(cfn, num_user, param_path, global_path, priv_key);

    if (retval != 0)
    {
        free(cfn);
        free(param_path);
        free(global_path);
        free(priv_key);
        return retval;
    }

    (*env)->SetByteArrayRegion(env, sys_priv_key_out, 0, BCE_ZR_LENGTH, (jbyte *) priv_key);

    free(cfn);
    free(param_path);
    free(global_path);
    free(priv_key);
    return 0;
}

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_genPrivateKeys(JNIEnv *env, jclass obj, jbyteArray sys_params_path, jbyteArray sys_priv_key, jint num_user, jint start_index, jint length, jobjectArray user_priv_keys_out)
{
    jint len, check_len;
    int retval, i;
    jobject col;
    byte *params_path;
    byte *priv_key;
    byte *user_keys_out;

    len = (*env)->GetArrayLength(env, sys_params_path);
    params_path = (byte *) malloc(len + 1);
    (*env)->GetByteArrayRegion(env, sys_params_path, 0, len, (jbyte *) params_path);
    params_path[len] = '\0';

    len = (*env)->GetArrayLength(env, sys_priv_key);
    if (len != BCE_ZR_LENGTH)
        return -1;
    priv_key = (byte *) malloc(len);
    (*env)->GetByteArrayRegion(env, sys_priv_key, 0, len, (jbyte *) priv_key); //**

    user_keys_out = (byte *) malloc(length * USER_PRIV_KEY_SIZE);

    check_len = (*env)->GetArrayLength(env, user_priv_keys_out);

    retval = BCEGenPrivateKeys(params_path, priv_key, num_user, start_index, length, user_keys_out, check_len);

    if (retval != 0)
    {
        free(user_keys_out);
        free(params_path);
        free(priv_key);
        return retval;
    }

    len = (*env)->GetArrayLength(env, user_priv_keys_out);

    for (i = 0; i < len; i++)
    {
        col = (*env)->GetObjectArrayElement(env, user_priv_keys_out, i);
        (*env)->SetByteArrayRegion(env, col, 0, USER_PRIV_KEY_SIZE, (jbyte *) (user_keys_out + i * USER_PRIV_KEY_SIZE));
    }

    free(user_keys_out);
    free(params_path);
    free(priv_key);

    return 0;
}

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_encrypt(JNIEnv *env, jclass obj, jbyteArray sys_params_path, jbyteArray CT_C0_out, jbyteArray CT_C1_out, jbyteArray symmetric_key_out)
{
    jint length;
    int retval;
    byte *params_path;
    byte *CT_C0;
    byte *CT_C1;
    byte *symmetric_key;

    length = (*env)->GetArrayLength(env, sys_params_path);
    params_path = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, sys_params_path, 0, length, (jbyte *) params_path);
    params_path[length] = '\0';

    length = (*env)->GetArrayLength(env, CT_C0_out);
    if (length != CT_C0_LENGTH)
        return -2;
    CT_C0 = (byte *) malloc(length);

    length = (*env)->GetArrayLength(env, CT_C1_out);
    if (length != CT_C1_LENGTH)
        return -3;
    CT_C1 = (byte *) malloc(length);

    length = (*env)->GetArrayLength(env, symmetric_key_out);
    if (length != SYMMETRIC_KEY_LENGTH)
        return -4;
    symmetric_key = (byte *) malloc(length);

    retval = BCEEncrypt(params_path, CT_C0, CT_C1, symmetric_key);

    if (retval != 0)
    {
        free(params_path);
        free(CT_C0);
        free(CT_C1);
        free(symmetric_key);
        return retval;
    }

    (*env)->SetByteArrayRegion(env, CT_C0_out, 0, CT_C0_LENGTH, (jbyte *) CT_C0);
    (*env)->SetByteArrayRegion(env, CT_C1_out, 0, CT_C1_LENGTH, (jbyte *) CT_C1);
    (*env)->SetByteArrayRegion(env, symmetric_key_out, 0, SYMMETRIC_KEY_LENGTH, (jbyte *) symmetric_key);

    free(params_path);
    free(CT_C0);
    free(CT_C1);
    free(symmetric_key);

    return 0;
}

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_decrypt(JNIEnv *env, jclass obj, jbyteArray global_params_path, jbyteArray priv_key_block, jbyteArray CT_C0, jbyteArray CT_C1, jbyteArray symmetric_key_out)
{
    jint length;
    int retval;
    byte *global_path;
    byte *prk_block;
    byte *ct_c0;
    byte *ct_c1;
    byte *symmetric_key;

    length = (*env)->GetArrayLength(env, global_params_path);
    global_path = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, global_params_path, 0, length, (jbyte *) global_path);
    global_path[length] = '\0';

    length = (*env)->GetArrayLength(env, priv_key_block);
    if (length != USER_PRIV_KEY_SIZE)
        return -2;
    prk_block = (byte *) malloc(length);
    (*env)->GetByteArrayRegion(env, priv_key_block, 0, length, (jbyte *) prk_block);

    length = (*env)->GetArrayLength(env, CT_C0);
    if (length != CT_C0_LENGTH)
        return -3;
    ct_c0 = (byte *) malloc(length);
    (*env)->GetByteArrayRegion(env, CT_C0, 0, length, (jbyte *) ct_c0);

    length = (*env)->GetArrayLength(env, CT_C1);
    if (length != CT_C1_LENGTH)
        return -4;
    ct_c1 = (byte *) malloc(length);
    (*env)->GetByteArrayRegion(env, CT_C1, 0, length, (jbyte *) ct_c1);

    length = (*env)->GetArrayLength(env, symmetric_key_out);
    if (length != SYMMETRIC_KEY_LENGTH)
        return -5;
    symmetric_key = (byte *) malloc(length);

    retval = BCEDecrypt(global_path, prk_block, ct_c0, ct_c1, symmetric_key);

    if (retval != 0)
    {
        free(global_path);
        free(prk_block);
        free(ct_c0);
        free(ct_c1);
        free(symmetric_key);
        return retval;
    }

    (*env)->SetByteArrayRegion(env, symmetric_key_out, 0, SYMMETRIC_KEY_LENGTH, (jbyte *) symmetric_key);

    free(global_path);
    free(prk_block);
    free(ct_c0);
    free(ct_c1);
    free(symmetric_key);
    return 0;
}

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_changeEncryptionProduct(JNIEnv *env, jclass obj, jbyteArray sys_params_path, jintArray adds, jint n_adds, jintArray rems, jint n_rems)
{
    jint length;
    byte *params_path;
    jint *adds_array;
    jint *rems_array;
    int retval;

    if (adds == NULL && rems == NULL)
        return -2;

    length = (*env)->GetArrayLength(env, sys_params_path);
    params_path = (byte *) malloc(length + 1);
    (*env)->GetByteArrayRegion(env, sys_params_path, 0, length, (jbyte *) params_path);
    params_path[length] = '\0';

    if (adds != NULL)
        adds_array = (*env)->GetIntArrayElements(env, adds, 0);

    if (rems != NULL)
        rems_array = (*env)->GetIntArrayElements(env, rems, 0);

    if (adds == NULL)
        retval = BCEChangeEncryptionProduct(params_path, NULL, 0, rems_array, n_rems);
    else if (rems == NULL)
        retval = BCEChangeEncryptionProduct(params_path, adds_array, n_adds, NULL, 0);
    else
        retval = BCEChangeEncryptionProduct(params_path, adds_array, n_adds, rems_array, n_rems);

    if (retval != 0)
    {
        free(params_path);
        if (adds != NULL)
            (*env)->ReleaseIntArrayElements(env, adds, adds_array, 0);
        if (rems != NULL)
            (*env)->ReleaseIntArrayElements(env, rems, rems_array, 0);
        return retval;
    }

    free(params_path);
    if (adds != NULL)
        (*env)->ReleaseIntArrayElements(env, adds, adds_array, 0);
    if (rems != NULL)
        (*env)->ReleaseIntArrayElements(env, rems, rems_array, 0);

    return 0;
}

JNIEXPORT jint JNICALL Java_bce_jni_natives_BCENative_changeDecryptionProduct
(JNIEnv *env, jclass obj, jbyteArray global_params_path, jint start_index, jint length, jintArray adds, jint n_adds, jintArray rems, jint n_rems, jbyteArray decr_prod, jbyteArray decr_prod_out)
{
    jint len;
    byte *global_path;
    jint *adds_array;
    jint *rems_array;
    byte *decr_prod_block;
    byte *decr_prod_block_out;
    int retval;

    if (adds == NULL && rems == NULL)
        return -2;

    /** TODO used to solve JVM complaint, but I don't know why */
    // printf("1\n");
    len = (*env)->GetArrayLength(env, global_params_path);
    global_path = (byte *) malloc(len + 1);
    (*env)->GetByteArrayRegion(env, global_params_path, 0, len, (jbyte *) global_path);
    global_path[len] = '\0';

    len = (*env)->GetArrayLength(env, decr_prod);
    if (len != length * PRK_DECR_PROD_LENGTH)
        return -8;
    decr_prod_block = (byte *) malloc(len);
    (*env)->GetByteArrayRegion(env, decr_prod, 0, len, (jbyte *) decr_prod_block);

    len = (*env)->GetArrayLength(env, decr_prod_out);
    if (len != length * PRK_DECR_PROD_LENGTH)
        return -9;
    decr_prod_block_out = (byte *) malloc(len);

    if (adds != NULL)
        adds_array = (*env)->GetIntArrayElements(env, adds, 0);

    if (rems != NULL)
        rems_array = (*env)->GetIntArrayElements(env, rems, 0);

    if (adds == NULL)
        retval = BCEChangeDecryptionProduct(global_path, start_index, length, NULL, 0, rems_array, n_rems, decr_prod_block, decr_prod_block_out);
    else if (rems == NULL)
        retval = BCEChangeDecryptionProduct(global_path, start_index, length, adds_array, n_adds, NULL, 0, decr_prod_block, decr_prod_block_out);
    else
        retval = BCEChangeDecryptionProduct(global_path, start_index, length, adds_array, n_adds, rems_array, n_rems, decr_prod_block, decr_prod_block_out);

    if (retval != 0)
    {
        free(global_path);
        free(decr_prod_block);
        free(decr_prod_block_out);
        if (adds != NULL)
            (*env)->ReleaseIntArrayElements(env, adds, adds_array, 0);
        if (rems != NULL)
            (*env)->ReleaseIntArrayElements(env, rems, rems_array, 0);
        return retval;
    }

    (*env)->SetByteArrayRegion(env, decr_prod_out, 0, length * PRK_DECR_PROD_LENGTH, (jbyte *) decr_prod_block_out);

    free(global_path);
    free(decr_prod_block);
    free(decr_prod_block_out);
    if (adds != NULL)
        (*env)->ReleaseIntArrayElements(env, adds, adds_array, 0);
    if (rems != NULL)
        (*env)->ReleaseIntArrayElements(env, rems, rems_array, 0);
    return 0;
}
