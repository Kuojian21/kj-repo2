package com.kj.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.function.BiFunction;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Helper {

	@SuppressWarnings("unchecked")
	public static <T> T enhancer(Class<T> clazz, BiFunction<Method, Object[], Object> func) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				return func.apply(method, args);
			}
		});
		return (T) enhancer.create();
	}

	

	public boolean upload(ChannelSftp sftp, String directory, String file, InputStream is)
					throws SftpException, IOException {
		String root = null;
		try {
			root = sftp.pwd();
			if (!Strings.isNullOrEmpty(directory)) {
				String[] dirs = directory.split("/");
				for (String dir : dirs) {
					if (!Strings.isNullOrEmpty(dir)) {
						try {
							sftp.cd(dir);
						} catch (SftpException sException) {
							if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
								sftp.mkdir(dir);
								sftp.cd(dir);
							} else {
								return false;
							}
						}
					}
				}
			}
			sftp.put(is, file);
			return true;
		} finally {
			if (Strings.isNullOrEmpty(root)) {
				try {
					sftp.cd(root);
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}

		}
	}

	public boolean download(ChannelSftp sftp, String directory, String file, OutputStream os)
					throws IOException, SftpException {
		String root = null;
		try {
			root = sftp.pwd();
			if (!Strings.isNullOrEmpty(directory)) {
				sftp.cd(directory);
			}
			sftp.get(file, os);
			os.flush();
			return true;
		} finally {
			if (os != null) {
				try {
					os.close();
				} finally {
					if (Strings.isNullOrEmpty(root)) {
						sftp.cd(root);
					}
				}
			}

		}
	}

}
