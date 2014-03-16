package bce.java.utils;

import static bce.jni.utils.BCEUtils.bytesToInt;
import static bce.jni.utils.BCEUtils.intToBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import bce.java.io.SecureByteArrayInputStream;
import bce.java.io.SecureByteArrayOutputStream;

/**
 * BCECapsule的AES实现<br>
 * 加密方式 AES256_CBC_PKCS5Padding<br>
 * 密钥哈希函数 SHA-512<br>
 * 这个实现不是线程安全的
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 */
public class BCECapsuleAESImpl implements BCECapsule {

	private static final long serialVersionUID = 3514897463500469130L;
	
	/**
	 * 哈希函数算法
	 */
	private final static String HASH_ALGORITHM = "SHA-512";
	
	/**
	 * 加密算法/加密模式/加密填充方式
	 */
	private final static String CRYPTO_ALGORITHM = "AES/CBC/PKCS5Padding";
	
	/**
	 * 明文数据
	 */
	private transient byte[] data;
	
	/**
	 * AES密钥
	 */
	private transient byte[] key;
	
	/**
	 * 密文数据
	 */
	private transient byte[] cipherText;
	
	/**
	 * 密钥的SHA-512摘要，长度为512/8=64
	 */
	private transient byte[] keyHash;
	
	/**
	 * 构造函数
	 */
	public BCECapsuleAESImpl() {}

	/**
	 * <pre>
	 * 持久化字段格式
	 * 加密算法名称（长度一字节 名称最多256字节）
	 * 哈希算法名称（长度一字节 名称最多256字节）
	 * 哈希值 长度由算法确定
	 * 数据有效长度 4字节
	 * 加密数据长度 4字节
	 * 加密数据
	 * </pre>
	 * @see see {@link bce.java.utils.BCEConstraints#writeExternal(OutputStream)}
	 */
	@Override
	public void writeExternal(OutputStream out) throws IOException {
		ensureNotNull(out, data, key);
		byte[] key = Hash.sha256(this.key);
		byte[] iv = Hash.md5(this.key);
		byte[] keyHash = Hash.sha512(this.key);
		byte[] crypto = null;
		try {
			Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
			crypto = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
			throw new IOException(e);
		} catch (InvalidAlgorithmParameterException e) {
		} catch (IllegalBlockSizeException e) {
			throw new IOException(e);
		} catch (BadPaddingException e) {
			throw new IOException(e);
		} finally {
			Arrays.fill(key, (byte) 0);
			Arrays.fill(iv, (byte) 0);
		}
		if (crypto == null)
			throw new IOException("Cannot encrypt data!");
		byte cl = (byte) CRYPTO_ALGORITHM.length();
		out.write(cl);
		out.write(CRYPTO_ALGORITHM.getBytes());
		out.write((byte) HASH_ALGORITHM.length());
		out.write(HASH_ALGORITHM.getBytes());
		out.write(keyHash);
		out.write(intToBytes(data.length));
		out.write(intToBytes(crypto.length));
		out.write(crypto);
		out.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCEConstraints#readExternal(java.io.InputStream)
	 */
	@Override
	public void readExternal(InputStream in) throws IOException, ClassNotFoundException {
		
		int cLength = in.read();
		byte[] cBuffer = new byte[cLength];
		cLength = in.read(cBuffer);
		if (cLength != cBuffer.length)  //有问题？
			throw new IOException("Not enough bytes!");
		
		int hLength = in.read();
		byte[] hnBuffer = new byte[hLength];
		hLength = in.read(hnBuffer);
		if (hLength != hnBuffer.length)
			throw new IOException("Not enough bytes!");
		
		this.keyHash = new byte[64];
		in.read(this.keyHash);
		
		byte[] tmp = new byte[4];
		int tLength = in.read(tmp);
		if (tLength != tmp.length)
			throw new IOException("Not enough bytes!");
		int dataLength = bytesToInt(tmp);
		
		tLength = in.read(tmp);
		if (tLength != tmp.length)
			throw new IOException("Not enough bytes!");
		int cipherLength = bytesToInt(tmp);
		
		// 由于是AES/CBC块加密模式，为了补全块，密文长度可能略大于明文长度，所以不检查密文与明文长度是否相等
//		if (dataLength != cipherLength)
//			throw new IOException("dataLength not equals to cipherLength!");
		
		this.data = new byte[dataLength];
		this.cipherText = new byte[cipherLength];
		cipherLength = in.read(this.cipherText);
		if (cipherLength != this.cipherText.length)
			throw new IOException("Not enough bytes!");
		
		if (this.key != null)
			decrypt();
	}
	
	/**
	 * Capsule内置的解密函数
	 * 
	 * @throws IOException
	 */
	private void decrypt() throws IOException {
		byte[] key = Hash.sha256(this.key);
		byte[] iv = Hash.md5(this.key);
		try {
			Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
			byte[] tmp = cipher.doFinal(this.cipherText);
			System.arraycopy(tmp, 0, this.data, 0, this.data.length);
			Arrays.fill(tmp, (byte) 0);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
			throw new IOException(e);
		} catch (InvalidAlgorithmParameterException e) {
		} catch (IllegalBlockSizeException e) {
			throw new IOException(e);
		} catch (BadPaddingException e) {
			throw new IOException(e);
		} finally {
			Arrays.fill(key, (byte) 0);
			Arrays.fill(iv, (byte) 0);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#protect(byte[])
	 */
	@Override
	public void protect(byte[] data) {
		ensureNotNull(data);
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#protect(java.io.Serializable)
	 */
	@Override
	public void protect(Serializable object) {
		ensureNotNull(object);
		
		try {
			SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buffer);
			out.writeObject(object);
			out.flush();
			buffer.flush();
			this.data = buffer.toByteArray();
			out.close();
			buffer.reset();
			buffer.close();
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#getData()
	 */
	@Override
	public byte[] getData() {
		return this.data;
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#getDataAsObject()
	 */
	@Override
	public Object getDataAsObject() throws ClassNotFoundException {
		try {
			SecureByteArrayInputStream buffer = new SecureByteArrayInputStream(this.getData());
			ObjectInputStream in = new ObjectInputStream(buffer);
			Object obj = in.readObject();
			in.close();
			buffer.reset();
			buffer.close();
			return obj;
		} catch (IOException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#setKey(byte[])
	 */
	@Override
	public void setKey(byte[] key) {
		ensureNotNull(key);
		this.key = key;
		if (this.cipherText != null) {
			byte[] exc = Hash.sha512(key);
			boolean eq = Arrays.equals(exc, keyHash);
			if (!eq)
				throw new IllegalArgumentException("Invalid key");
			
			try {
				decrypt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#getHashAlgorithm()
	 */
	@Override
	public String getHashAlgorithm() {
		return HASH_ALGORITHM;
	}

	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#getCrypto()
	 */
	@Override
	public String getCrypto() {
		return CRYPTO_ALGORITHM;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		if (this.data != null)
			Arrays.fill(this.data, (byte) 0);
		if (this.key != null)
			Arrays.fill(this.key, (byte) 0);
		if (this.cipherText != null)
			Arrays.fill(this.cipherText, (byte) 0);
		if (this.keyHash != null)
			Arrays.fill(this.keyHash, (byte) 0);
		super.finalize();
	}
	
	private void ensureNotNull(Object ... objs) throws NullPointerException {
		if (objs == null)
			throw new NullPointerException();
		for (Object obj : objs) {
			if (obj == null)
				throw new NullPointerException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.java.utils.BCECapsule#abort()
	 */
	@Override
	public void abort() {
		if (this.key != null)
			Arrays.fill(this.key, (byte) 0);
		if (this.keyHash != null)
			Arrays.fill(this.keyHash, (byte) 0);
		if (this.data != null)
			Arrays.fill(this.data, (byte) 0);
		if (this.cipherText != null)
			Arrays.fill(this.cipherText, (byte) 0);
	}

}
