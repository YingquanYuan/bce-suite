#include "bcejni.h"

int BCESetup(byte *curve_file_name, int num_user, byte *sys_params_path, byte * global_params_path, byte *sys_priv_key_out)
{
    global_broadcast_params_t gbs;
    broadcast_system_t sys;
    //char recip[num_user / NUM_USER_DIVISOR];
    char *recip;
    element_t sys_priv_key;

    if (curve_file_name == NULL)
        return 1;
    if (num_user % NUM_USER_DIVISOR != 0)
        return 2;
    if (sys_params_path == NULL)
        return 3;
    if (global_params_path == NULL)
        return 4;
    if (sys_priv_key_out == NULL)
        return 5;

    Setup_global_broadcast_params(&gbs, num_user, (char *) curve_file_name);

    Gen_broadcast_system(gbs, &sys);

    recip = (char *) malloc(num_user / NUM_USER_DIVISOR);

    memset(recip, BIT_VECTOR_UNIT_VALUE, num_user / NUM_USER_DIVISOR);

    Gen_encr_prod_from_bitvec(gbs, sys, recip);

    StoreParams((char *) sys_params_path, gbs, sys);
    StoreGlobalParams((char *) global_params_path, gbs);

    element_init_Zr(sys_priv_key, gbs->pairing);

    element_set(sys_priv_key, sys->priv_key);

    element_to_bytes(sys_priv_key_out, sys_priv_key);

    memset(recip, BIT_VECTOR_CLEAR_UNIT_VALUE, num_user / NUM_USER_DIVISOR);
    free(recip);
    element_random(sys_priv_key);
    element_clear(sys_priv_key);
    FreeBCS(sys);
    pbc_free(sys);
    FreeGBP(gbs);
    pbc_free(gbs);
    return 0;
}

//start_index从1开始，不算0
int BCEGenPrivateKeys(byte *sys_params_path, byte *sys_priv_key, int num_user, int start_index, int length, byte *user_priv_keys_out, int check_array)
{
    global_broadcast_params_t gbs;
    broadcast_system_t sys;
    int priv_key_read, i;
    //struct single_priv_key_s user_keys[length];  // Problematic Memory Allocation

    struct single_priv_key_s *user_keys;

    char *recip;
    // char recip[num_user / NUM_USER_DIVISOR];

    if (sys_params_path == NULL)
        return 1;

    if (sys_priv_key == NULL)
        return 2;

    if (num_user % NUM_USER_DIVISOR != 0)
        return 3;

    if (start_index % NUM_USER_DIVISOR != 1)
        return 4;

    if (user_priv_keys_out == NULL)
        return 6;

    if (length > check_array)
        return -2;

    LoadParams((char *) sys_params_path, &gbs, &sys);

    priv_key_read = element_from_bytes(sys->priv_key, sys_priv_key);

    if (priv_key_read != BCE_ZR_LENGTH)
        return -1;

    recip = (char *) malloc(num_user / NUM_USER_DIVISOR);

    memset(recip, BIT_VECTOR_UNIT_VALUE, num_user / NUM_USER_DIVISOR);

    user_keys = (priv_key_t) malloc(length * sizeof(struct single_priv_key_s)); //**

    for (i = 0; i < length; i++)
    {
        Get_priv_key(gbs, sys, start_index + i, &user_keys[i]);
        Gen_decr_prod_from_bitvec(gbs, start_index + i, recip, &user_keys[i]);
    }

    for (i = 0; i < length; i++)
    {
        int_to_bytes(user_keys[i].index, user_priv_keys_out);
        user_priv_keys_out += 4;
        user_priv_keys_out += element_to_bytes(user_priv_keys_out, user_keys[i].g_i_gamma);
        user_priv_keys_out += element_to_bytes(user_priv_keys_out, user_keys[i].g_i);
        user_priv_keys_out += element_to_bytes(user_priv_keys_out, user_keys[i].h_i);
        user_priv_keys_out += element_to_bytes(user_priv_keys_out, user_keys[i].decr_prod);
    }

    memset(recip, BIT_VECTOR_CLEAR_UNIT_VALUE, num_user / NUM_USER_DIVISOR);
    free(recip);
    for (i = 0; i < length; i++)
    {
        FreePK(&user_keys[i]);
    }
    free(user_keys);
    FreeBCS(sys);
    pbc_free(sys);
    FreeGBP(gbs);
    pbc_free(gbs);

    return 0;
}

int BCEEncrypt(byte *sys_params_path, byte *CT_C0_out, byte *CT_C1_out, byte *symmetric_key_out)
{
    global_broadcast_params_t gbs;
    broadcast_system_t sys;
    ct_t shared_CT;
    element_t symmetric_key;
    int retlen;

    if (sys_params_path == NULL)
        return 1;

    if (CT_C0_out == NULL)
        return 2;

    if (CT_C1_out == NULL)
        return 3;

    if (symmetric_key_out == NULL)
        return 4;

    LoadParams((char *) sys_params_path, &gbs, &sys);

    shared_CT = (ct_t) pbc_malloc(sizeof(struct ciphertext_s));

    BroadcastKEM_using_product(gbs, sys, shared_CT, symmetric_key);

    retlen = element_to_bytes(CT_C0_out, shared_CT->C0);
    if (retlen != CT_C0_LENGTH)
        return 5;

    retlen = element_to_bytes(CT_C1_out, shared_CT->C1);
    if (retlen != CT_C1_LENGTH)
        return 6;

    retlen = element_to_bytes(symmetric_key_out, symmetric_key);
    if (retlen != SYMMETRIC_KEY_LENGTH)
        return 7;

    element_random(symmetric_key);
    element_clear(symmetric_key);
    FreeCT(shared_CT);
    pbc_free(shared_CT);
    FreeBCS(sys);
    pbc_free(sys);
    FreeGBP(gbs);
    pbc_free(gbs);

    return 0;
}

int BCEDecrypt(byte *global_params_path, byte *priv_key_block, byte *CT_C0, byte *CT_C1, byte *symmetric_key_out)
{
    global_broadcast_params_t gbs;
    priv_key_t priv_key;
    ct_t shared_CT;
    element_t symmetric_key;
    int suffix = 0, retlen;

    if (global_params_path == NULL)
        return 1;
    if (priv_key_block == NULL)
        return 2;
    if (CT_C0 == NULL)
        return 3;
    if (CT_C1 == NULL)
        return 4;
    if (symmetric_key_out == NULL)
        return 5;

    LoadGlobalParams((char *) global_params_path, &gbs);

    priv_key = (priv_key_t) pbc_malloc(sizeof(struct single_priv_key_s));
    // restore index
    priv_key->index = bytes_to_int(priv_key_block);
    suffix += PRK_INDEX_LENGTH;

    // restore g_i_gamma
    element_init(priv_key->g_i_gamma, gbs->pairing->G1);
    retlen = element_from_bytes(priv_key->g_i_gamma, priv_key_block + suffix);
    if (retlen != PRK_G_I_GAMMA_LENGTH)
        return 6;
    suffix += PRK_G_I_GAMMA_LENGTH;

    // restore g_i
    element_init(priv_key->g_i, gbs->pairing->G1);
    retlen = element_from_bytes(priv_key->g_i, priv_key_block + suffix);
    if (retlen != PRK_G_I_LENGTH)
        return 7;
    suffix += PRK_G_I_LENGTH;

    // restore h_i
    element_init(priv_key->h_i, gbs->pairing->G2);
    retlen = element_from_bytes(priv_key->h_i, priv_key_block + suffix);
    if (retlen != PRK_H_I_LENGTH)
        return 8;
    suffix += PRK_H_I_LENGTH;

    // restore decr_prod
    element_init(priv_key->decr_prod, gbs->pairing->G1);
    retlen = element_from_bytes(priv_key->decr_prod, priv_key_block + suffix);
    if (retlen != PRK_DECR_PROD_LENGTH)
        return 9;

    shared_CT = (ct_t) pbc_malloc(sizeof(struct ciphertext_s));
    element_init(shared_CT->C0, gbs->pairing->G2);
    element_init(shared_CT->C1, gbs->pairing->G1);
    element_from_bytes(shared_CT->C0, CT_C0);
    element_from_bytes(shared_CT->C1, CT_C1);

    DecryptKEM_using_product(gbs, priv_key, symmetric_key, shared_CT);

    retlen = element_to_bytes(symmetric_key_out, symmetric_key);
    if (retlen != SYMMETRIC_KEY_LENGTH)
        return 10;

    element_random(symmetric_key);
    element_clear(symmetric_key);
    FreeCT(shared_CT);
    pbc_free(shared_CT);
    FreePK(priv_key);
    pbc_free(priv_key);
    FreeGBP(gbs);
    pbc_free(gbs);

    return 0;
}

int BCEChangeEncryptionProduct(byte *sys_params_path, int *adds, int n_adds, int *rems, int n_rems)
{
    global_broadcast_params_t gbs;
    broadcast_system_t sys;

    if (sys_params_path == NULL)
        return 1;
    if (adds == NULL && rems == NULL)
        return 2;

    LoadParams((char *) sys_params_path, &gbs, &sys);

    if (n_adds > gbs->num_users)
        return 3;
    if (n_rems > gbs->num_users)
        return 5;

    Change_encr_prod_indicies(gbs, sys, adds, n_adds, rems, n_rems);

    StoreParams((char *) sys_params_path, gbs, sys);

    FreeBCS(sys);
    pbc_free(sys);
    FreeGBP(gbs);
    pbc_free(gbs);

    return 0;
}

// start_index从1开始
int BCEChangeDecryptionProduct(byte *global_params_path, int start_index, int length, int *adds, int n_adds, int *rems, int n_rems, byte *decr_prod, byte *decr_prod_out)
{
    global_broadcast_params_t gbs;
    struct single_priv_key_s *priv_key;
    int i, writelen = 0;

    if (global_params_path == NULL)
        return 1;
    if (start_index % NUM_USER_DIVISOR != 1)
        return 2;
    if (adds == NULL && rems == NULL)
        return 4;
    if (decr_prod == NULL)
        return 8;
    if (decr_prod_out == NULL)
        return 9;

    printf("fuck\n");
    LoadGlobalParams((char *) global_params_path, &gbs);
    printf("fvck\n");

    if (n_adds > gbs->num_users)
        return 5;
    if (n_rems > gbs->num_users)
        return 7;

    priv_key = (priv_key_t) malloc(length * sizeof(struct single_priv_key_s));

    for (i = 0; i < length; i++)
    {
        // restore index
        priv_key[i].index = start_index + i;

        // restore fake g_i_gamma
        element_init(priv_key[i].g_i_gamma, gbs->pairing->G1);

        // restore fake g_i
        element_init(priv_key[i].g_i, gbs->pairing->G1);

        // restore fake h_i
        element_init(priv_key[i].h_i, gbs->pairing->G2);

        // restore real decr_prod
        element_init(priv_key[i].decr_prod, gbs->pairing->G1);
        decr_prod += element_from_bytes(priv_key[i].decr_prod, decr_prod);
    }

    for (i = 0; i < length; i++)
    {
        Change_decr_prod_indicies(gbs, priv_key[i].index, adds, n_adds, rems, n_rems, &priv_key[i]);
        writelen += element_to_bytes(decr_prod_out + writelen, priv_key[i].decr_prod);
    }

    for (i = 0; i < length; i++)
        FreePK(&priv_key[i]);
    free(priv_key);
    FreeGBP(gbs);
    pbc_free(gbs);

    return 0;
}
