package bce.jni.natives;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Boneh-Gentry-Waters Broadcast Encryption (BCE) JNI wrapper</p>
 * <p>
 * This is the most udnerlying JNI wrapper of libbcejni, all native methods of
 * libbcejni are defined here.
 * </p>
 * <p>
 * BCE has the standard 4 phases for describing a public key cryptographic system:
 * <ul>
 * <li>Setup</li>
 * <li>KeysGeneration</li>
 * <li>Encrypt</li>
 * <li>Decrypt</li>
 * </ul>
 * Besides, changeEncryptionProduct and changeDecryptionProduct is BCE specific, and
 * they implemented the dynamically adding and revoking users in a BCE crypto group
 * features.
 * This feature has been described and proved in Boneh-Gentry-Waters BCE scheme.
 * </p>
 * @author Yingquan
 */
public final class BCENative {

    private BCENative() {}

    private static final String LIBNAME = "libbcejni";
    private static final String CONFNAME = "d201.param";

    static {
        String runtimeDirName = createRuntimeDirectory();
        writeConf(runtimeDirName, CONFNAME);
        String libPath = writeLibrary(runtimeDirName, LIBNAME, getLibSuffix());
        System.load(libPath);
    }

    /**
     * Set up the runtime directory, and returns the name of the runtime
     * native library directory
     */
    private static String createRuntimeDirectory() {
        File libRuntimeDir = new File("/tmp/libbcejni.run");
        // pre-clean
        if (libRuntimeDir.exists() && libRuntimeDir.isDirectory()) {
            libRuntimeDir.delete();
        }
        libRuntimeDir.mkdir();
        return libRuntimeDir.getAbsolutePath();
    }

    /**
     * Returns the platform dependent suffix of the native library
     */
    private static String getLibSuffix() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac"))
            return "jnilib";
        else if (osName.contains("linux"))
            return "so";
        else
            throw new RuntimeException("Unsupported OS type");
    }

    /**
     * Externalize the d201.param configuration file
     */
    private static void writeConf(String outPath, String confName) {
        final String confAbsoluteName = String.format("%s/%s", outPath, confName);
        unpackFromJar(confName, confAbsoluteName);
    }

    /**
     * Externalize the native library file, and returns its absolute path
     */
    private static String writeLibrary(String outPath, String libName, String suffix) {
        final String libFullName = String.format("%s.%s", libName, suffix);
        final String libAbsoluteName = String.format("%s/%s", outPath, libFullName);
        unpackFromJar(libFullName, libAbsoluteName);
        return libAbsoluteName;
    }

    /**
     * Unpack file 'fileNameInJar' to 'fileNameExtern'
     */
    private static void unpackFromJar(String fileNameInJar, String fileNameExtern) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = BCENative.class.getResourceAsStream("/" + fileNameInJar);
            out = new FileOutputStream(new File(fileNameExtern));
            byte[] buf = new byte[128];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Caught IOException: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ei) {}
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException eo) {}
            }
        }
    }

    /**
     * Initializes a BCE system, and generates the BCE environment parameters.
     * This method will load a elliptic curve parameters file, and generates
     * 2 BCE system parameters files.
     *
     * @param curveFileName The absolute name of the curve parameter file
     * @param numUser The user number of the current BCE system instance, must be times of 8
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
