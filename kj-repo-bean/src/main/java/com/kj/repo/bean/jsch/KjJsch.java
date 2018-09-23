package com.kj.repo.bean.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.kj.repo.bean.pool.KjPool;

public class KjJsch extends KjPool<ChannelSftp, KjJsch.TYPE> {

    public KjJsch(String host, int port, String username, String password) {
        super(new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
            @Override
            public PooledObject<ChannelSftp> wrap(ChannelSftp sftp) {
                return new DefaultPooledObject<ChannelSftp>(sftp) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                        try {
                            sftp.disconnect();
                            sftp.getSession().disconnect();
                        } catch (JSchException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            @Override
            public ChannelSftp create() throws Exception {
                JSch jsch = new JSch();
                Session sshSession = jsch.getSession(username, host, port);
                sshSession.setPassword(password);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                Channel channel = sshSession.openChannel("sftp");
                channel.connect();
                return (ChannelSftp) channel;
            }

        }, init()));
    }

    public KjJsch(String host, int port, String username, String prvfile, String pubfile, byte[] passphrase) {
        super(new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
            @Override
            public PooledObject<ChannelSftp> wrap(ChannelSftp sftp) {
                return new DefaultPooledObject<ChannelSftp>(sftp) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                        try {
                            sftp.disconnect();
                            sftp.getSession().disconnect();
                        } catch (JSchException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            @Override
            public ChannelSftp create() throws Exception {
                JSch jsch = new JSch();
                jsch.addIdentity(prvfile, pubfile, passphrase);
                Session sshSession = jsch.getSession(username, host, port);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                Channel channel = sshSession.openChannel("sftp");
                channel.connect();
                return (ChannelSftp) channel;
            }

        }, init()));
    }

    private static GenericObjectPoolConfig init() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(10);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(30000);
        return config;
    }

    public boolean upload(String directory, String file, InputStream is) {
        try {
            return this.execute(TYPE.UPLOAD, directory, file, is);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean download(String directory, String file, OutputStream os) {
        try {
            return this.execute(TYPE.DOWNLOAD, directory, file, os);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean upload(ChannelSftp sftp, String directory, String file, InputStream is) throws SftpException, IOException {
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

    public boolean download(ChannelSftp sftp, String directory, String file, OutputStream os) throws IOException, SftpException {
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

    @Override
    public Boolean execute(ChannelSftp channelSftp, TYPE type, Object... args) throws Exception {
        String dir = (String) args[0];
        String file = (String) args[1];
        switch (type) {
            case UPLOAD:
                InputStream is = (InputStream) args[2];
                return this.upload(channelSftp, dir, file, is);
            case DOWNLOAD:
            default:
                OutputStream os = (OutputStream) args[2];
                return this.download(channelSftp, dir, file, os);
        }
    }

    public enum TYPE {
        DOWNLOAD, UPLOAD;
    }

}
