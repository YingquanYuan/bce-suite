#ifndef BCEJNI_H_INCLUDED
#define BCEJNI_H_INCLUDED

#include "pbc_bce.h"
#include "bce_utils.h"

#define BCE_ZR_LENGTH 23

#define USER_PRIV_KEY_SIZE 316

#define PRK_INDEX_LENGTH 4

#define PRK_G_I_GAMMA_LENGTH 52

#define PRK_G_I_LENGTH 52

#define PRK_H_I_LENGTH 156

#define PRK_DECR_PROD_LENGTH 52

#define BIT_VECTOR_UNIT_VALUE 254

#define BIT_VECTOR_CLEAR_UNIT_VALUE 0

#define NUM_USER_DIVISOR 8

#define CT_C0_LENGTH 156

#define CT_C1_LENGTH 52

#define SYMMETRIC_KEY_LENGTH 156

int BCESetup(byte *curve_file_name, int num_user, byte *sys_params_path, byte * global_params_path, byte *sys_priv_key_out);

int BCEGenPrivateKeys(byte *sys_params_path, byte *sys_priv_key, int num_user, int start_index, int length, byte *user_priv_keys_out, int check_array);

int BCEEncrypt(byte *sys_params_path, byte *CT_C0_out, byte *CT_C1_out, byte *symmetric_key_out);

int BCEDecrypt(byte *global_params_path, byte *priv_key_block, byte *CT_C0, byte *CT_C1, byte *symmetric_key_out);

int BCEChangeEncryptionProduct(byte *sys_params_path, int *adds, int n_adds, int *rems, int n_rems);

int BCEChangeDecryptionProduct(byte *global_params_path, int start_index, int length, int *adds, int n_adds, int *rems, int n_rems, byte *decr_prod, byte *decr_prod_out);
#endif // BCEJNI_H_INCLUDED
