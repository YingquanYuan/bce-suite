package bce.server.servicers;

import java.io.File;
import java.util.List;

import bce.java.entities.BCEPrivateKey;
import bce.java.utils.Hash;
import bce.jni.utils.BCEUtils;
import bce.server.dao.BCESystemDAO;
import bce.server.dao.PrivateKeyDAO;
import bce.server.entities.PersistentBCESystem;
import bce.server.entities.PersistentPrivateKey;
import bce.server.entities.PersistentUser;
import bce.server.util.BCEObjectConverter;

/**
 * 业务类，MainPageServlet的业务服务者
 *
 * @author robins
 *
 */
public class MainPageServicer {

    PrivateKeyDAO privateKeyDAO;

    BCESystemDAO systemDAO;

    public void setPrivateKeyDAO(PrivateKeyDAO privateKeyDAO) {
        this.privateKeyDAO = privateKeyDAO;
    }

    public void setSystemDAO(BCESystemDAO systemDAO) {
        this.systemDAO = systemDAO;
    }

    public MainPageServicer() {
    }

    /**
     * 为当前用户申请一个新的BCE私钥
     *
     * @param user 当前用户对象
     * @return 新申请的BCE私钥持久化对象
     */
    public PersistentPrivateKey applyBCEPrivateKey(PersistentUser user) {

        PersistentPrivateKey privateKey = privateKeyDAO.getFirstAvailable();
        privateKey.setBelongedUser(user);
        privateKeyDAO.update(privateKey);
        return privateKey;
    }

    /**
     * 在首次加载用户主页面时，或用户主页面刷新时，为当前用户准备已有BCE数据
     *
     * @param user 当前用户持久化对象
     * @return 当前用户已有的BCE私钥集合
     */
    public List<PersistentPrivateKey> prepareBCEData(PersistentUser user) {

        List<PersistentPrivateKey> dataList = privateKeyDAO.get(user);
        return dataList;
    }

    /**
     * 用户在下载某个已有BCE私钥时，为其准备该私钥
     *
     * @param privateKeyId 私钥的标志符
     * @return BCE私钥业务对象
     */
    public BCEPrivateKey prepareBCEPrivateKey(Integer privateKeyId) {

        PersistentPrivateKey persistentPrivateKey = privateKeyDAO.get(privateKeyId);
        BCEPrivateKey privateKey = BCEObjectConverter.transform(persistentPrivateKey);
        persistentPrivateKey.abort();
        return privateKey;
    }

    public File prepareBCEClientParams(Integer systemId) {

        PersistentBCESystem bceSystem  = systemDAO.get(systemId);
        String paramsFileName = bceSystem.getGlobalSysParamsURI();
        File paramsFile = new File(paramsFileName);
        return paramsFile;
    }

    /**
     * 用户申请新的BCE私钥时，用于验证用户二次输入的密码是否正确
     *
     * @param password 用户二次输入的密码
     * @param user 存在session中的当前登录用户
     * @return true：密码正确；false：密码错误
     */
    public boolean isValidPassword(String password, PersistentUser user) {
        if (password == null)
            return false;
        else if (BCEUtils.hex(Hash.sha1(password)).equals(user.getPassword()))
            return true;
        else
            return false;
    }

    /**
     * 用户申请新的BCE私钥时，用于验证验证码是否正确
     *
     * @param vcodeInRequest存储在request域中的验证码
     * @param codeInSession 存储在session域中的验证码
     * @return true：验证码正确；false：验证码错误
     */
    public boolean isValidVcode(String vcodeInRequest, String vcodeInSession) {
        if (vcodeInRequest == null || vcodeInSession == null)
            return false;
        else if (vcodeInRequest.equals(vcodeInSession))
            return true;
        else
            return false;
    }
}
