package com.dxy.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 * @author duanxinyuan
 * 2018/5/2 20:32
 */
@Slf4j
public class FileUtil {
    /**
     * 创建文件
     */
    public static void createFile(String localPath) {
        // 本地文件的地址
        File localFile = new File(localPath);
        if (localFile.isDirectory()) {
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
        } else {
            String localPathDir = localPath.substring(0, localPath.lastIndexOf("/"));
            File localPathDirFile = new File(localPathDir);
            if (!localPathDirFile.exists()) {
                localPathDirFile.mkdirs();
            }
            if (!localFile.exists()) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 确保目录存在，不存在则创建
     */
    private static void makeDir(String filePath) {
        if (filePath.lastIndexOf('/') > 0) {
            String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * 解压到当前目录
     */
    public static String unZipFiles(String zipPath) throws IOException {
        //拿到项目文件名
        String fileName = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(zipPath, "/"), ".");
//        //项目名前面的部分
//        String beforeLast = StringUtils.substringBeforeLast(zipPath, "/");
//        //时间戳的部分
//        String beforeLast1 = StringUtils.substringAfterLast(beforeLast, "/");
        //拿到解压至的目录文件路径，往前读一个/
        String dirPath = StringUtils.substringBeforeLast(zipPath, "/");
        unZipFiles(new File(zipPath), dirPath);
//        return dirPath + "/" + beforeLast1 + "/" + fileName;
        return dirPath + "/" + fileName;
    }

    /**
     * 解压到指定目录
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     * @param zipFile 待解压的zip文件
     * @param descDir 指定目录
     */
    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        //解决中文文件夹乱码
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
//        String name = zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));

//        File pathFile = new File(descDir + name);
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
//            String outPath = (descDir + name + "/" + zipEntryName).replaceAll("\\*", "/");
            String outPath = (descDir + "/" + zipEntryName).replaceAll("\\*", "/");

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        log.info("******************解压完毕********************");
    }


    /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void zipFile(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(zipPath);
            //添加编码，如果不添加，当文件以中文命名的情况下，会出现乱码
            // ZipOutputStream的包一定是apache的ant.jar包。JDK也提供了打压缩包，但是不能设置编码
            zos = new ZipOutputStream(fos, Charset.forName("GBK"));
            zipFile(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            log.error("创建ZIP文件失败");
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败");
            }
        }
    }

    private static void zipFile(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) {
            //处理文件夹
            if (file.isDirectory()) {
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                for (File f : files) {
                    zipFile(f, parentPath, zos);
                }
            } else {
                FileInputStream fis = null;
                DataInputStream dis = null;
                try {
                    fis = new FileInputStream(file);
                    dis = new DataInputStream(new BufferedInputStream(fis));
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } catch (IOException e) {
                    log.error("创建ZIP文件失败");
                } finally {
                    try {
                        if (dis != null) {
                            dis.close();
                        }
                    } catch (IOException e) {
                        log.error("创建ZIP文件失败");
                    }finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}
