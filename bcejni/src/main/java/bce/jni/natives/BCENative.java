package bce.jni.natives;

public final class BCENative {
	
	private BCENative() {}
	
	private static final String LIBNAME = "bcejni";
	
	static {
		// System.load("/Users/yingquan/code/bce-suite/libbcejni/target/libbcejni.jnilib");
		System.loadLibrary(LIBNAME);
	}
	
	/**
	 * 用于首次生成BCE环境参数，在本地函数内部读取椭圆函数参数，生成广播加密系统参数并存入指定文件
	 * 
	 * @param curveFileName 保存椭圆函数的文件绝对路径
	 * @param numUser BCE系统用户数目，必须为8的倍数
	 * @param sysParamsPath 用于指定BCE系统参数的生成绝对路径
	 * @param globalParamsPath 用于指定BCE全局系统参数的生成绝对路径
	 * @param sysPrivKey_out 用于返回即将被销毁的用户私钥
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int setup(byte[] curveFileName, int numUser, byte[] sysParamsPath, byte[] globalParamsPath, byte[] sysPrivKey_out);
	
	/**
	 * 从广播加密系统参数文件中读取参数，为指定批次用户生成私钥参数
	 * 
	 * @param sysParamsPath BCE系统参数文件绝对路径
	 * @param sysPrivKey 用户临时私钥，使用后必须销毁
	 * @param numUser 广播加密系统用户总人数
	 * @param startIndex 当前批次中第一个用户的索引
	 * @param length 该批次操作用户批量
	 * @param userPrivKeys_out 二维byte数组，用于返回当前批次操作后生成的用户私钥参数，字节格式
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int genPrivateKeys(byte[] sysParamsPath, byte[] sysPrivKey, int numUser, int startIndex, int length, byte[][] userPrivKeys_out);
	
	/**
	 * 加密函数，“广播”一次密文，为系统中所有合法用户生成一个共享密文块
	 * 
	 * @param sysParamsPath BCE系统参数文件绝对路径
	 * @param CT_C0_out 用于返回密文块中第一段密文体
	 * @param CT_C1_out 用于返回密文块中第二段密文体
	 * @param symmetricKey_out 用于返回广播加密系统生成的对称密钥，即消息加密密钥
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int encrypt(byte[] sysParamsPath, byte[] CT_C0_out, byte[] CT_C1_out, byte[] symmetricKey_out);
	
	/**
	 * 解密函数，客户端用户读取自己的系统参数文件（包含椭圆参数），构建解密环境，用其专有的私钥完成解密
	 * 
	 * @param globalParamsPath BCE客户端系统参数文件名（包含椭圆参数），绝对路径
	 * @param userPrivKey 用户私钥，由私钥文件各字段拼接成的字节块
	 * @param CT_C0 密文块中第一段密文体
	 * @param CT_C1 密文快中第二段密文体
	 * @param symmetricKey_out 用于返回广播加密系统客户端解密出的对称密钥，即消息加密密钥
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int decrypt(byte[] globalParamsPath, byte[] userPrivKey, byte[] CT_C0, byte[] CT_C1, byte[] symmetricKey_out);
	
	/**
	 * 根据adds队列与rems队列改变广播加密系统服务器端的加密产品
	 * 
	 * @param sysParamsPath BCE系统参数文件绝对路径
	 * @param adds 新增用户队列，其长度由nAdds指定
	 * @param nAdds 指定adds队列长度，注意一定要设为正确的值，建议使用前检查
	 * @param rems 失效用户队列，起长度由nRems指定
	 * @param nRems 指定rems队列长度，注意一定要设为正确的值，建议使用前检查
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int changeEncryptionProduct(byte[] sysParamsPath, int[] adds, int nAdds, int[] rems, int nRems);
	
	/**
	 * 根据adds队列与rems队列改变广播加密系统中每个用户私钥中的解密产品
	 * @param globalParamsPath BCE客户端系统参数文件名（包含椭圆参数），绝对路径
	 * @param offset 用户索引的起始位置，从1开始
	 * @param length 当次改变解密产品的批量
	 * @param adds 新增用户队列，其长度由nAdds指定
	 * @param nAdds 指定adds队列长度，注意一定要设为正确的值，建议使用前检查
	 * @param rems 失效用户队列，起长度由nRems指定
	 * @param nRems 指定rems队列长度，注意一定要设为正确的值，建议使用前检查
	 * @param decrProdBatch 传入本地方法等待处理的批量用户解密产品
	 * @param decrProdBatch_out 用于输出处理后更新的批量用户解密产品
	 * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
	 */
	public static native int changeDecryptionProduct(byte[] globalParamsPath, int offset, int length, int[] adds, int nAdds, int[] rems, int nRems, byte[] decrProdBatch, byte[] decrProdBatch_out);
}
