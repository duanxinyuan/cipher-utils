package com.dxy.library.util.ftp;

import com.dxy.library.util.common.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * FTP操作工具类
 * 需要依赖“Apache Commons Net”，<a href="http://mvnrepository.com/artifact/commons-net/commons-net/3.6">仓库地址
 * 在每次使用前必须先建立连接，并在每次使用完成之后关闭连接。</p>
 * FtpUtil.connect("127.0.0.1", 21, "admin", "admin");
 * // 执行操作 ...
 * FtpUtil.disconnect();
 * 所有的路径（“ftpPath”）中：凡以“/”开头代表是从FTP根目录开始的绝对路径，凡未以“/”开头代表是相对当前所在目录的路径。
 * FtpUtil.connect("127.0.0.1", 21, "admin", "admin");
 * FtpUtil.toWorkingDirectory("/upload");
 * // 以下代码会上传到FTP服务器的根路径下的“upload”目录中
 * FtpUtil.upload("D:\\123.exe", "/upload/", "123.exe");
 * // 以下代码会上传到FTP服务器的根路径下的“upload”目录下的“upload”目录中
 * FtpUtil.upload("D:\\123.exe", "upload/", "123.exe");
 * FtpUtil.disconnect();
 * 如果连接后，未进行任何目录跳转操作，则当前所在路径为用户主路径（注意不是FTP根路径）
 * 使用非管理员帐号可能会有权限问题（视FTP服务器设置而定），若出现读写某个目录或者文件失败极大可能是由于没有权限造成的。
 * @author duanxinyuan
 * 2015-02-16 20:43
 */
@Slf4j
public final class FtpUtils {

    /**
     * FTP客户端实例
     */
    private static FTPClient ftpClient;

    static {
        FTPClientConfig config = new FTPClientConfig();
        config.setServerLanguageCode("zh");

        ftpClient = new FTPClient();
        ftpClient.configure(config);
    }

    private FtpUtils() {
    }

    /**
     * 连接到FTP服务器
     * 通过FTP协议的默认端口，使用匿名用户访问FTP服务器</p>
     * @param url FtpServer地址
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static void connect(String url) throws IOException {
        connect(url, 21);
    }

    /**
     * 连接到FTP服务器
     * 通过指定的FTP服务器端口，使用匿名用户访问FTP服务器</p>
     * @param url FtpServer地址
     * @param port FtpServer端口
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static void connect(String url, int port) throws IOException {
        connect(url, port, "anonymous", "");
    }

    /**
     * 连接到FTP服务器
     * 通过FTP协议的默认端口，使用指定的用户名和密码访问FTP服务器</p>
     * @param url FtpServer地址
     * @param username 登陆用户名
     * @param password 登录密码
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static void connect(String url, String username, String password) throws IOException {
        connect(url, 21, username, password);
    }

    /**
     * 连接到FTP服务器
     * 通过指定的FTP服务器端口，使用指定的用户名和密码访问FTP服务器</p>
     * @param url FtpServer地址
     * @param port FtpServer端口
     * @param username 登陆用户名
     * @param password 登录密码
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static void connect(String url, int port, String username, String password) throws IOException {
        // 自动与服务器协商使用“UTF-8”编码，必须在连接建立之前调用
        ftpClient.setAutodetectUTF8(true);

        // 设置编码，必须在连接建立之前调用
        ftpClient.setControlEncoding("UTF-8");

        // 连接到FTP服务器
        ftpClient.connect(url, port);

        // 连接超时，30s
        ftpClient.setConnectTimeout(1000 * 30);

        // 数据传输超时，30s
        ftpClient.setDataTimeout(1000 * 30);

        // 缓冲区大小，必须在FTP连接之后设定，10MB
        ftpClient.setBufferSize(1024 * 1024 * 10);

        // 检查连接状态
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            disconnect();
            throw new IOException("Unable to connect to your FTP server, please check if it is available.");
        }

        // 如果未设置用户名则使用匿名用户登录
        if (username == null || "".equals(username)) {
            // 匿名用户名称
            username = "anonymous";
        }

        // 登录
        if (!ftpClient.login(username, password)) {
            disconnect();
            throw new IOException("Login failed, please confirm the username or password is correct.");
        }
    }

    /**
     * 从FTP服务器断开连接
     */
    public static void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 创建目录，如果目录存在则不创建
     * @param ftpPath 要创建的目录路径
     * @return 创建成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean createDirectory(String ftpPath) throws IOException {
        StringBuilder pathName = new StringBuilder();
        // 是否是绝对路径，绝对路径是从“/”开始
        if (ftpPath.startsWith("/")) {
            pathName.append("/");
        }

        StringTokenizer tokenizer = new StringTokenizer(ftpPath, "/");
        ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
        while (tokenizer.hasMoreElements()) {
            pathName.append(tokenizer.nextElement()).append("/");
            if (!isDirectoryExist(pathName.toString())) {
                if (!ftpClient.makeDirectory(pathName.toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 上传文件到FTP服务器
     * @param localFileName 要上传的文件名
     * @param ftpPath 上传到FTP服务器的哪个目录
     * @param ftpFileName 上传到FTP服务器后新的文件名
     * @return 上传成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean upload(String localFileName, String ftpPath, String ftpFileName) throws IOException {
        return upload(new FileInputStream(localFileName), ftpPath, ftpFileName);
    }

    /**
     * 上传文件到FTP服务器
     * @param localFile 要上传的文件
     * @param ftpPath 上传到FTP服务器的哪个目录
     * @param ftpFileName 上传到FTP服务器后新的文件名
     * @return 上传成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean upload(File localFile, String ftpPath, String ftpFileName) throws IOException {
        return upload(new FileInputStream(localFile), ftpPath, ftpFileName);
    }

    /**
     * 上传文件到FTP服务器
     * @param bytes 要上传的文件字节数组
     * @param ftpPath 上传到FTP服务器的哪个目录
     * @param ftpFileName 上传到FTP服务器后新的文件名
     * @return 上传成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean upload(byte[] bytes, String ftpPath, String ftpFileName) throws IOException {
        return upload(new ByteArrayInputStream(bytes), ftpPath, ftpFileName);
    }

    /**
     * 上传文件到FTP服务器
     * @param inputStream 要上传的流
     * @param ftpPath 上传到FTP服务器的哪个目录
     * @param ftpFileName 上传到FTP服务器后新的文件名
     * @return 上传成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean upload(InputStream inputStream, String ftpPath, String ftpFileName) throws IOException {

        // 转换为BufferedInputStream以提高传输效率
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            // 开启主动模式绕过防火墙
            ftpClient.enterLocalPassiveMode();
            // 使用二进制传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 使用流的形式传输
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

            // 自动创建不存在的目录
            if (!isDirectoryExist(ftpPath)) {
                createDirectory(ftpPath);
            }

            // 开始上传文件
            return ftpClient.storeFile(makeFTPFileName(ftpPath, ftpFileName), bufferedInputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 删除文件
     * @param ftpPath 要删除的文件在FTP服务器的哪个目录里
     * @param ftpFileName 要删除的文件在FTP服务器上的文件名
     * @return 删除成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean remove(String ftpPath, String ftpFileName) throws IOException {
        return ftpClient.deleteFile(makeFTPFileName(ftpPath, ftpFileName));
    }

    /**
     * 重命名文件或移动文件位置
     * @param fromPath 要重命名的文件在FTP服务器的哪个目录里
     * @param fromFileName 要重命名的文件在FTP服务器上的文件名
     * @param toPath 重命名到FTP服务器的哪个目录里
     * @param toFileName 重命名后的新文件名
     * @return 重命名成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean rename(String fromPath, String fromFileName, String toPath, String toFileName)
            throws IOException {
        String from = makeFTPFileName(fromPath, fromFileName);
        String to = makeFTPFileName(toPath, toFileName);
        return ftpClient.rename(from, to);
    }

    /**
     * 获取指定目录下所有文件名的列表
     * @param ftpPath 从FTP服务器的哪个目录里获取
     * @return 指定目录下所有文件名的列表
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static List<String> listFileNames(String ftpPath) throws IOException {
        // 开启主动模式绕过防火墙
        ftpClient.enterLocalPassiveMode();

        FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath);
        List<String> fileList = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            fileList.add(ftpFile.getName());
        }
        return fileList;
    }

    /**
     * 获取指定目录下所有文件对象的列表
     * @param ftpPath 从FTP服务器的哪个目录里获取
     * @return 指定目录下所有文件对象的列表
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static List<FTPFile> listFiles(String ftpPath) throws IOException {
        // 开启主动模式绕过防火墙
        ftpClient.enterLocalPassiveMode();

        FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath);
        return new ArrayList<>(Arrays.asList(ftpFiles));
    }

    /**
     * 检查路径是否存在
     * @param ftpPath 要检查FTP服务器的哪个目录
     * @return 路径是否存在
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean isDirectoryExist(String ftpPath) throws IOException {
        String current = ftpClient.printWorkingDirectory();
        boolean isExists = ftpClient.changeWorkingDirectory(ftpPath);
        if (isExists) {
            ftpClient.changeWorkingDirectory(current);
        }
        return isExists;
    }

    /**
     * 下载文件到本地
     * @param ftpPath 要下载的文件在FTP服务器的哪个目录里
     * @param ftpFileName 要下载的文件名称
     * @param localFileName 下载到哪个文件
     * @return 下载成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean download(String ftpPath, String ftpFileName, String localFileName) throws IOException {
        FileUtils.createFile(localFileName);
        OutputStream outputStream = new FileOutputStream(new File(localFileName));
        return download(ftpPath, ftpFileName, outputStream);
    }

    /**
     * 下载文件到本地
     * @param ftpPath 要下载的文件在FTP服务器的哪个目录里
     * @param ftpFileName 要下载的文件名称
     * @param file 下载到哪个文件
     * @return 下载成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean download(String ftpPath, String ftpFileName, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        return download(ftpPath, ftpFileName, outputStream);
    }

    /**
     * 下载文件到流
     * @param ftpPath 要下载的文件在FTP服务器的哪个目录里
     * @param ftpFileName 要下载的文件名称
     * @param outputStream 下载到哪个流
     * @return 下载成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean download(String ftpPath, String ftpFileName, OutputStream outputStream) throws IOException {
        // 转换为BufferedOutputStream以提高传输效率
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            // 开启主动模式绕过防火墙
            ftpClient.enterLocalPassiveMode();
            // 使用二进制传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 设置文件传输类型为二进制
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            // 开始下载文件
            return ftpClient.retrieveFile(makeFTPFileName(ftpPath, ftpFileName), bufferedOutputStream);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 下载文件到流
     * @param ftpPath 要下载的文件在FTP服务器的哪个目录里
     * @return 下载成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean download(String ftpPath, String localPath) throws IOException {
        // 开启主动模式绕过防火墙
        ftpClient.enterLocalPassiveMode();
        // 使用二进制传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        // 设置文件传输类型为二进制
        ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

        // 转移到FTP服务器目录至指定的目录下
        ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(StandardCharsets.UTF_8)));
        FileUtils.createFile(localPath);
        try {
            // 获取文件列表
            FTPFile[] fs = ftpClient.listFiles();
            for (FTPFile ff : fs) {
                File localFile = new File(localPath + "/" + ff.getName());
                OutputStream outputStream = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ff.getName(), outputStream);
                outputStream.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取当前所在目录名称
     * @return 当前所在目录名称
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static String getWorkingDirectory() throws IOException {
        return ftpClient.printWorkingDirectory();
    }

    /**
     * 进入当前目录的上级目录
     * @return 进入成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean toParentDirectory() throws IOException {
        return ftpClient.changeToParentDirectory();
    }

    /**
     * 进入指定目录
     * @param ftpPath 要进入的目录的路径
     * @return 进入成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean toWorkingDirectory(String ftpPath) throws IOException {
        return ftpClient.changeWorkingDirectory(ftpPath);
    }

    /**
     * 向FTP发送命令
     * @param args 命令
     * @return 执行成功返回true，否则返回false
     * @throws IOException 在向服务器发送命令或从服务器接收到答复时发生I/O错误
     */
    public static boolean sendSiteCommand(String args) throws IOException {
        return ftpClient.isConnected() && ftpClient.sendSiteCommand(args);
    }

    /**
     * 创建保存在FTP上的文件全路径名
     * @param ftpPath FTP上的目录路径
     * @param ftpFileName FTP上的文件名
     * @return 保存在FTP上的文件全名
     */
    public static String makeFTPFileName(String ftpPath, String ftpFileName) {
        // 文件名前不应该出现“/”
        ftpFileName = ftpFileName.trim();
        while (ftpFileName.startsWith("/")) {
            ftpFileName = ftpFileName.substring(0, 1);
        }

        if (ftpPath == null || "".equals(ftpPath)) {
            return ftpFileName;
        } else {
            ftpPath = ftpPath.trim();

            final char dirSeparator = '/';
            if (ftpPath.charAt(ftpPath.length() - 1) != dirSeparator) {
                ftpPath = ftpPath + dirSeparator;
            }
            return ftpPath + ftpFileName;
        }
    }

}
