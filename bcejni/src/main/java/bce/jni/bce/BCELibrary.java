package bce.jni.bce;

import bce.jni.natives.BCENative;
import bce.jni.utils.BCEConstants;

public final class BCELibrary {

    private BCELibrary() {}

    /**
     * 用于首次生成BCE环境参数，在本地函数内部读取椭圆函数参数，生成广播加密系统参数并存入指定文件
     *
     * @param curveFileName 保存椭圆函数的文件绝对路径
     * @param numUser BCE系统用户数目，必须为8的倍数
     * @param sysParamsPath 用于指定BCE系统参数的生成绝对路径
     * @param globalParamsPath 用于指定BCE全局系统参数的生成绝对路径
     * @param sysPrivKeyOut 用于返回即将被销毁的用户私钥
     * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
     */
    public final static int setup(byte[] curveFileName, int numUser, byte[] sysParamsPath, byte[] globalParamsPath, byte[] sysPrivKeyOut) {

        ensureArrayCapacity(sysPrivKeyOut, BCEConstants.PRIVATE_KEY_LENGTH);

        return BCENative.setup(curveFileName, numUser, sysParamsPath, globalParamsPath, sysPrivKeyOut);
    }

    /**
     * 从广播加密系统参数文件中读取参数，为指定批次用户生成私钥参数
     *
     * @param sysParamsPath BCE系统参数文件绝对路径
     * @param sysPrivKey 用户临时私钥，使用后必须销毁
     * @param numUser 广播加密系统用户总人数
     * @param startIndex 当前批次中第一个用户的索引
     * @param length 该批次操作用户批量
     * @param userPrivKeysOut 二维byte数组，用于返回当前批次操作后生成的用户私钥参数，字节格式
     * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
     */
    public final static int genPrivateKeys(byte[] sysParamsPath, byte[] sysPrivKey, int numUser, int startIndex, int length, byte[][] userPrivKeysOut) {

        ensureArrayCapacity(userPrivKeysOut, length);

        for (byte[] bs : userPrivKeysOut) {
            ensureArrayCapacity(bs, BCEConstants.USER_PRIVATE_KEY_SIZE);
        }

        return BCENative.genPrivateKeys(sysParamsPath, sysPrivKey, numUser, startIndex, length, userPrivKeysOut);
    }

    /**
     * 加密函数，“广播”一次密文，为系统中所有合法用户生成一个共享密文块
     *
     * @param sysParamsPath BCE系统参数文件绝对路径
     * @param CTC0Out 用于返回密文块中第一段密文体
     * @param CTC1Out 用于返回密文块中第二段密文体
     * @param symmetricKeyOut 用于返回广播加密系统生成的对称密钥，即消息加密密钥
     * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
     */
    public final static int encrypt(byte[] sysParamsPath, byte[] CTC0Out, byte[] CTC1Out, byte[] symmetricKeyOut) {

        ensureArrayCapacity(CTC0Out, BCEConstants.CT_C0_LENGTH);
        ensureArrayCapacity(CTC1Out, BCEConstants.CT_C1_LENGTH);
        ensureArrayCapacity(symmetricKeyOut, BCEConstants.SYMMETRIC_KEY_LENGTH);

        return BCENative.encrypt(sysParamsPath, CTC0Out, CTC1Out, symmetricKeyOut);
    }

    /**
     * 解密函数，客户端用户读取自己的系统参数文件（包含椭圆参数），构建解密环境，用其专有的私钥完成解密
     *
     * @param globalParamsPath BCE客户端系统参数文件名（包含椭圆参数），绝对路径
     * @param userPrivKey 用户私钥，由私钥文件各字段拼接成的字节块
     * @param CTC0 密文块中第一段密文体
     * @param CTC1 密文快中第二段密文体
     * @param symmetricKeyOut 用于返回广播加密系统客户端解密出的对称密钥，即消息加密密钥
     * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
     */
    public final static int decrypt(byte[] globalParamsPath, byte[] userPrivKey, byte[] CTC0, byte[] CTC1, byte[] symmetricKeyOut) {

        ensureArrayCapacity(symmetricKeyOut, BCEConstants.SYMMETRIC_KEY_LENGTH);

        return BCENative.decrypt(globalParamsPath, userPrivKey, CTC0, CTC1, symmetricKeyOut);
    }

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
    public final static int changeEncryptionProduct(byte[] sysParamsPath, int[] adds, int nAdds, int[] rems, int nRems) {

        ensureArrayCapacity(adds, nAdds);
        ensureArrayCapacity(rems, nRems);

        return BCENative.changeEncryptionProduct(sysParamsPath, adds, nAdds, rems, nRems);
    }

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
     * @param decrProdBatchOut 用于输出处理后更新的批量用户解密产品
     * @return 返回0：执行成功；其他整型数：出错参数索引；其中，负值表示JNI层错误码；正值表示业务层错误码。
     */
    public final static int changeDecryptionProduct(byte[] globalParamsPath, int offset, int length, int[] adds, int nAdds, int[] rems, int nRems, byte[] decrProdBatch, byte[] decrProdBatchOut) {

        ensureArrayCapacity(adds, nAdds);
        ensureArrayCapacity(rems, nRems);
        ensureArrayCapacity(decrProdBatchOut, BCEConstants.PRK_DECR_PROD_LENGTH * length);

        return BCENative.changeDecryptionProduct(globalParamsPath, offset, length, adds, nAdds, rems, nRems, decrProdBatch, decrProdBatchOut);
    }

    private final static void ensureArrayCapacity(int[] array, int capacity) {
        if (array == null) {
            if (capacity == 0)
                return;
            else
                throw new IllegalArgumentException(new StringBuilder("array is null, but capacity is not 0!").toString());
        }
        if (capacity < 0)
            throw new IllegalArgumentException(new StringBuilder("minimum size must be above zero:").append(capacity).toString());
        if (array.length != capacity)
            throw new IllegalArgumentException("unmatched array length and capacity");
    }

    private final static void ensureArrayCapacity(byte[] array, int min) {
        if (array == null)
            throw new IllegalArgumentException(new StringBuilder("array size must be at least:").append(min).append(",actural:0").toString());
        if (min < 1)
            throw new IllegalArgumentException(new StringBuilder("minimum size must be above zero:").append(min).toString());
        if (array.length < min)
            throw new IllegalArgumentException(new StringBuilder("array size must be at least:").append(min).append(",actural:").append(array.length).toString());
    }

    private final static void ensureArrayCapacity(byte[][] array, int min) {
        if (array == null)
            throw new IllegalArgumentException(new StringBuilder("array size must be at least:").append(min).append(",actural:0").toString());
        if (min < 1)
            throw new IllegalArgumentException(new StringBuilder("minimum size must be above zero:").append(min).toString());
        if (array.length < min)
            throw new IllegalArgumentException(new StringBuilder("array size must be at least:").append(min).append(",actural:").append(array.length).toString());
    }

}
