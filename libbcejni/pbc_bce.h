/*  Version: 0.0.1
    Implementation of Boneh-Gentry-Waters broadcast encryption scheme
    Code by:  Matt Steiner   MattS@cs.stanford.edu

    Some changes by Ben Lynn blynn@cs.stanford.edu

    bce.h

    Version: 0.0.1.x
    Some changes by Yingquan Yuan yingq.yuan@gmail.com

    bce.h
*/

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "pbc/pbc.h"


/* **********************************************************
   DEBUG having the debug flag turned on spews out lots of
   debugging output.
*********************************************************  */
#define DEBUG 0


/* **********************************************************
   PRIVATE KEY STRUCT
   This struct stores a single users' group elements and
   their private key.  It also contains the recipients
   currently in their product (a bit-vector representation),
   their decryption product (excluding their group element),
   and their index number.

   结构：私钥
   该结构体存储了一个简单用户组的元素以及他们的私钥。它也将接收者们
   临时包含在了他们的产品(一个bit-向量代表)中，还包含了他们的解密产
   品(除了他们的组元素)，和他们的索引号。
   其中，g_i, h_i, g_i_gamma为私钥部分
********************************************************** */
typedef struct single_priv_key_s
{
    element_t g_i_gamma;    //G1组，由gbp->gs[index-1]的sys->priv_key次方计算
    element_t g_i;          //G1组，由gbp->gs[index-1]set计算
    element_t h_i;          //G2组，由gbp->hs[index-1]set计算
    element_t decr_prod;    //G1组，用户的解密产品，在Gen_decr_prod_from_bitvec(global_broadcast_params_t gbp,int receiver, char *recip, priv_key_t mykey)中计算
    int index;              //用户在当前实例的id
}* priv_key_t;


/* **********************************************************
   GLOBAL BROADCAST PARAMS--
   Stores the:
   curve info--PUBLIC
   group elements--PUBLIC
   num-users-PUBLIC

   结构：全局广播参数，下含所有并行实例的参数
   存储：
        曲线信息--公有的
        组元素--公有的
        用户数目--公有的
*********************************************************  */
typedef struct global_broadcast_params_s
{
    pairing_t pairing;
    char *pairFileName; //"d201.param"
    element_t g;        //G1组，随机数
    element_t h;        //G2组，随机数, /** alpha属于Zr组，随机数 **/
    element_t *gs;      //G1组数组，由g的alpha次方计算
    element_t *hs;      //G2组数组，由h的alpha次方计算
    int num_users;      //系统所有并行实例中用户总数
}* global_broadcast_params_t;


/* **********************************************************
   BROADCAST SYSTEM stores:
   encryption product - can be public
   public key - public
   priv key - private

   结构：广播系统
   存储：
        加密产品--可以公有
        公钥--公有的
        私钥--私有的
*********************************************************  */
typedef struct broadcast_system_s
{
    element_t encr_prod;    //加密产品，初始化时不计算，在Gen_encr_prod_from_bitvec(gbp, sys, recip)中计算
    element_t pub_key;      //公钥，G1组，由gbp->g的priv_key次方计算
    element_t priv_key;     //私钥，Zr组，随机生成,作为生成每个用户私钥的生成元
}* broadcast_system_t;


/* **********************************************************
   CIPHERTEXT STRUCT
   Contains two group elements HDR C0 and HDR C1

   结构：密文
   存储：
        两个组元素头(HDR)C0和C1
*********************************************************  */
typedef struct ciphertext_s
{
    element_t C0;   //G2组
    element_t C1;   //G1组
}* ct_t;


/* **********************************************************
   These functions free the memory associated with various
   structures.  Note that the pointer you pass in will not
   be freed--you must free it manually to prevent freeing
   stack memory.

   这些函数用于释放相关不同结构体的内存。
   注意：
        你传入的指针将不会被释放，你必须手动释放它来阻止释放栈内存
********************************************************** */

void FreeCT(ct_t myct);
void FreeBCS(broadcast_system_t bcs);
void FreeGBP(global_broadcast_params_t gbp);
void FreePK(priv_key_t key);


/* **********************************************************
   Sets up a global broadcast system by generating all of
   the gs, the hs, and their inverses.  Chooses random alpha
   for the exponent.  num_users must be a multiple of 8.

   通过生成所有的gs, hs, 以及他们的逆元以初始化全局广播系统。
   为指数选择随机数alpha。
   用户数目必须为8的倍数。
*********************************************************  */
void Setup_global_broadcast_params(global_broadcast_params_t *gbp,
                                   int num_users, char *pairFileName);


/* **********************************************************
   Stores the global broadcast system parameters to a file.
   WARNING: FILE WILL BE LARGE for large numbers of users

   将全局广播系统参数存入文件。
   存入：
        参数文件名长度：strlen(gbp->pairFileName) + 1
        参数文件名：gbp->pairFileName
        所有实例用户总数：gbp->num_users
        加密产品：sys->encr_prod
        广播加密系统公钥：sys->pub_key
        全局参数g：gbp->g
        所有并行实例参数gs：gbp->gs[i]
        全局参数h：gbp->h
        所有并行实例参数hs：gbp->hs[i]
   未存入：
        gbp->pairing
        sys->priv_key
   警告：
        文件可能因用户数目众多而变得很大。
*********************************************************  */
void StoreParams(char *systemFileName,
                 global_broadcast_params_t gbp,
                 broadcast_system_t sys);

void StoreGlobalParams(char *systemFileName, global_broadcast_params_t gbp);

/* **********************************************************
   Loads the global broadcast system paramters from a file.

   从文件中载入全局广播系统参数。
*********************************************************  */
void LoadParams(char *systemFileName,
                global_broadcast_params_t *gbp,
                broadcast_system_t *sys);

void LoadGlobalParams(char *systemFileName, global_broadcast_params_t *gbp);

/* **********************************************************
   Stores a single private key to a file.  The pairing file
   should be distributed with the private key file.

   将一个简单私钥存入文件。
   对文件应该与私钥文件一同被分配。
   对文件可能用于构建加密环境，原理同IBE。

   存入：
        用户在当前实例的id：mykey->index
        私钥参数g_i_gamma：mykey->g_i_gamma
        私钥参数g_i：mykey->g_i
        私钥参数h_i：mykey->h_i
        用户的解密产品：mykey->decr_prod
**********************************************************  */
void StorePrivKey(char *keyFileName, priv_key_t mykey);


/* **********************************************************
   Loads a single private key into a private key structure.
   Should be done after loading the pairing file.

   将一个简单密钥载入私钥结构。
   改过程应在载入对文件之后执行。
**********************************************************  */
void LoadPrivKey(char *keyFileName, priv_key_t *mykey,
                 global_broadcast_params_t gbp);


/* **********************************************************
   Sets up one instance of a broadcast system, generating
   system specific global public and private keys.

   初始化一个广播系统的实例。
   生成系统特定的全局公钥和私钥。
*********************************************************  */
void Gen_broadcast_system(global_broadcast_params_t gbp,
                          broadcast_system_t *sys);


/* **********************************************************
   This function gets the private key for a user at index i.

   该函数为索引为i的用户获取私钥。
*********************************************************  */
void Get_priv_key(global_broadcast_params_t gbp,
                  broadcast_system_t sys,
                  int i, priv_key_t mykey);


/* **********************************************************
   This function generates the encryption product from
   a bit vector representing the users who should be able
   to decrypt the message.

   该函数从一个bit向量中生成加密产品，该向量代表能解密消息的用户。
   加密产品被存放在sys->encr_prod中，G1组类型。
*********************************************************  */
void Gen_encr_prod_from_bitvec(global_broadcast_params_t gbp,
                               broadcast_system_t sys, char *recip);


/* **********************************************************
   This function generates the encryption product from
   an array of indicies corresponding to the users who
   should be able to decrypt the message.  You must
   pass in the correct array size.

   该函数从一个索引数组中生成加密产品，该数组中的索引匹配能解密消息
   的用户。
   必须传入正确的数组大小。
*********************************************************  */
void Gen_encr_prod_from_indicies(global_broadcast_params_t gbp,
                                 broadcast_system_t sys,
                                 int *in_recip, int num_recip);


/* **********************************************************
   This function changes the encryption product of the system
   first by removing the N_rems elements in the rems array
   and then by adding the N_adds elements in the adds array.

   该函数用于改变系统的加密产品，首先通过移除rems队列中的N_rems个
   元素，再将adds队列中的N_adds个元素加入。
*********************************************************  */
void Change_encr_prod_indicies(global_broadcast_params_t gbp,
                               broadcast_system_t sys,
                               int *adds, int N_adds,
                               int *rems, int N_rems);


/* **********************************************************
   This function generates the decryption product from
   a bit vector representing the users who should be able
   to decrypt the message.  The product gets stored into
   mykey.  Group element corresponding to receiver is not
   included.

   该函数从一个bit向量中生成解密产品，该向量代表能解密消息的用户。
   解密产品被存入参数mykey中。
   对应于接收者的组元素没有被包括进来。
*********************************************************  */
void Gen_decr_prod_from_bitvec(global_broadcast_params_t gbp,
                               int receiver,
                               char *recip, priv_key_t mykey);


/* **********************************************************
   This function generates the decryption product from
   an array of indicies corresponding to the users who
   should be able to decrypt the message.  You must
   pass in the correct array size.  The product gets stored
   into mykey.  Group element corresponding to receiver is
   not included.

   该函数从一个索引数组中生成解密产品，数组中的索引对应能解密消息的
   用户。
   必须传入正确的数组大小。
   解密产品被存入参数mykey中。
   对应于接收者的组元素没有被包括进来。
*********************************************************  */
void Gen_decr_prod_from_indicies(global_broadcast_params_t gbp,
                                 int receiver, int *in_recip,
                                 int num_recip, priv_key_t mykey);


/* **********************************************************
   This function changes the decryption product of the user
   first by removing the N_rems elements in the rems array
   and then by adding the N_adds elements in the adds array.
   You must pass in the correct array size.  The product gets
   stored into mykey.  Group element corresponding to receiver
   is not included.

   该函数改变用户的解密产品，首先通过移除rems队列中的N_rems个元素，
   再将adds队列中的N_adds个元素加入。
   必须传入正确的数组大小。
   解密产品被存入参数mykey中。
   对应于接收者的组元素没有被包括进来。
*********************************************************  */
void Change_decr_prod_indicies(global_broadcast_params_t gbp,
                               int receiver,
                               int *adds, int N_adds,
                               int *rems, int N_rems, priv_key_t mykey);


/* **********************************************************
   An extremely useful function for validating your results
   This function takes a bit-string and prints out the first
   length bytes of it.  Accordingly, you must give the
   function length = Num_Users/8

   一个非常有用的函数，可以用来验证你的结果。
   该函数取一个bit字符串，输出其byte长度。
   因此，你必须传给函数参数length = Num_Users/8。
*********************************************************  */
void PrintBitString(char *bs, int length);


/* **********************************************************
   This function generates a broadcast key and a cipher-text
   header, once the encryption product has been calculated.

   当加密产品已经被计算时，该函数生成一个广播密钥和一个密文头。

   参数：
        key：GT组元素，用于存放消息加密密钥K(对称密钥)
*********************************************************  */
void BroadcastKEM_using_product(global_broadcast_params_t gbp,
                                broadcast_system_t sys,
                                ct_t myct, element_t key);


/* **********************************************************
   This function generates a broadcast key and a cipher-text
   header, by calling the Gen-prod-from-bitvec and then
   BroadcastKEM-using-product routines.  Just a wrapper.

   该函数生成一个广播密钥和一个密文头，通过调用Gen_encr_prod_from_bitvec
   和BroadcastKEM_using_product程序。
   仅仅是个封装器。
*********************************************************  */
void BroadcastKEM_using_bitvec(global_broadcast_params_t gbp,
                               broadcast_system_t sys,
                               char *recip, ct_t myct, element_t key);


/* **********************************************************
   This function generates a broadcast key and a cipher-text
   header, by calling the Gen-prod-from-indicies and then
   BroadcastKEM-using-product routines.  Just a wrapper.

   该函数生成一个广播密钥和一个密文头，通过调用Gen_encr_prod_from_indicies
   和BroadcastKEM_using_product程序。
   仅仅是个封装器。
*********************************************************  */
void BroadcastKEM_using_indicies(global_broadcast_params_t gbp,
                                 broadcast_system_t sys, ct_t myct,
                                 int *in_recip, int num_recip,
                                 element_t key);


/* **********************************************************
   This function retrieves a broadcast key from a cipher-text
   header, once the decryption product has been calculated.

   当解密产品已经被计算时，该函数从一个密文头中重获一个广播密钥。
   输出：
        消息加密密钥K(非对称)，存放在key中，K属于GT组
*********************************************************  */
void DecryptKEM_using_product(global_broadcast_params_t gbp,
                              priv_key_t mykey, element_t key,
                              ct_t myct);



/* **********************************************************
   This function retrieves a broadcast key from a cipher-text
   header, once the decryption product has been calculated,
   by calling the Gen-decr-prod-from-bitvec and then
   DecryptKEM-using-product routines.  Just a wrapper.

   当解密产品已经被计算时，该函数从一个密文头中重获一个广播密钥，通
   过调用Gen_decr_prod_from_bitvec和DecryptKEM_using_product
   程序。
   仅仅是个封装器。
*********************************************************  */
void Decrypt_BC_KEM_using_bitvec(global_broadcast_params_t gbp,
                                 priv_key_t mykey, element_t key,
                                 ct_t myct, char *recip);


/* **********************************************************
   This function retrieves a broadcast key from a cipher-text
   header, once the decryption product has been calculated,
   by calling the Gen_decr_prod_from_bitvec and then
   DecryptKEM_using_product routines.  Just a wrapper.

   当解密产品已经被计算时，该函数从一个密文头中重获一个广播密钥，通过
   调用 Gen_decr_prod_from_indicies和DecryptKEM_using_product
   程序。
   仅仅是个封装器。
*********************************************************  */
void Decrypt_BC_KEM_using_indicies(global_broadcast_params_t gbp,
                                   priv_key_t mykey, element_t key,
                                   ct_t myct, int *in_recip,
                                   int num_recip);


