package bce.jni.test;

public interface BCEConstraints {
	
	/**
	 * 椭圆函数参数文件名, 绝对路径
	 */
	String CURVE_FILE_NAME = "/home/robins/文档/codeblocks/bcejni/d201.param";
	
	/**
	 * 广播加密系统参数文件名, 绝对路径
	 */
	String SYS_PARAMS_FILE_NAME = "/home/robins/文档/codeblocks/bcejni/system.param";
	
	/**
	 * 客户端使用的广播加密系统参数文件名, 绝对路径
	 */
	String GLOBAL_PARAMS_FILE_NAME = "/home/robins/文档/codeblocks/bcejni/globalsystem.param";
	
	/**
	 * 广播加密系统中用户总数
	 */
	int USER_NUMBER = 128;
	
	/**
	 * 单次生成私钥操作的用户批次
	 */
	int PRIVATE_KEY_GEN_BATCH_SIZE = 64;
	
	int CHANGE_DECR_PROD_BATCH_SIZE = 64;
	
	/**
	 * 用户实际获取的私钥结构长度
	 *<pre>
	 *typedef struct single_priv_key_s {
	 * 	element_t g_i_gamma;    //G1组, 52bytes
	 * 	element_t g_i;          //G1组, 52bytes
	 * 	element_t h_i;          //G2组, 156bytes
	 * 	element_t decr_prod;    //G1组, 52bytes
	 * 	int index;              //用户在当前实例的索引, 4bytes
	 *}* priv_key_t;
	 *</pre>
	 */
	int USER_PRIVATE_KEY_SIZE = 316;
	
	int PRK_INDEX_LENGTH = 4;
	
	int PRK_G_I_GAMMA_LENGTH = 52;
	
	int PRK_G_I_LENGTH = 52;
	
	int PRK_H_I_LENGTH = 156;
	
	int PRK_DECR_PROD_LENGTH = 52;
	
	/**
	 * 实际私钥元素长度, Zr组, 23bytes
	 */
	int PRIVATE_KEY_LENGTH = 23;
	
	/**
	 * 密文头第一段C0长度, G2组, 156bytes
	 */
	int CT_C0_LENGTH = 156;
	
	/**
	 * 密文头第二段C1长度, G1组, 52bytes
	 */
	int CT_C1_LENGTH = 52;
	
	/**
	 * 广播加密系统生成的对称密钥（消息加密密钥）长度, GT组, 156bytes
	 */
	int SYMMETRIC_KEY_LENGTH = 156;
}
