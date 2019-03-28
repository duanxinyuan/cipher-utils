package com.dxy.library.util.common;

import com.google.common.collect.Lists;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * @author duanxinyuan
 * 2018/5/2 20:32
 */
public class FileUtils {

    /**
     * 创建文件
     */
    public static File createFile(String localPath) {
        File localFile = new File(localPath);
        if (localFile.isDirectory()) {
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
        } else {
            String localPathDir = localPath.substring(0, localPath.lastIndexOf(File.separator));
            File localPathDirFile = new File(localPathDir);
            if (!localPathDirFile.exists()) {
                localPathDirFile.mkdirs();
            }
            if (!localFile.exists()) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new File(localPath);
    }

    /**
     * 删除文件或者文件夹
     * @param path 文件路径
     */
    public static void delete(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        } else if (file.exists() && file.isDirectory()) {
            String[] fileNames = file.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    delete(path + File.separator + fileName);
                }
            }
            file.delete();
        }
    }

    /**
     * 把文件压缩成zip格式
     * @param path 需要压缩的文件的路径
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void zip(String path, String zipFilePath) {
        File file = new File(path);
        List<String> files = getFiles(file);
        zip(file.getAbsolutePath(), files.toArray(new String[0]), zipFilePath);
    }

    /**
     * 把文件压缩成zip格式
     * @param file 需要压缩的文件 ,如"D:/test;
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void zip(File file, String zipFilePath) {
        List<String> files = getFiles(file);
        zip(file.getAbsolutePath(), files.toArray(new String[0]), zipFilePath);
    }

    /**
     * 把文件压缩成zip格式
     * @param dirPath 父目录路径
     * @param paths 需要压缩的文件的路径集合
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    private static void zip(String dirPath, String[] paths, String zipFilePath) {
        if (paths != null && paths.length > 0) {
            if (!dirPath.endsWith(File.separator)) {
                dirPath += File.separator;
            }
            if (isZip(zipFilePath)) {
                try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(zipFilePath))) {
                    //使用Zip64扩展
                    zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

                    // 将每个文件用ZipArchiveEntry封装，再用ZipArchiveOutputStream写到压缩文件中
                    for (String path : paths) {
                        File file = new File(path);
                        if (file.exists()) {
                            String name = path.replace(dirPath, "").replace("\\", File.separator);
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, name);
                            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
                            if (file.isDirectory()) {
                                continue;
                            }
                            IOUtils.copy(new FileInputStream(file), zipArchiveOutputStream);
                            zipArchiveOutputStream.closeArchiveEntry();
                        }
                    }
                    zipArchiveOutputStream.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 把文件压缩成zip格式
     * @param files 需要压缩的文件集合
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void zip(List<File> files, String zipFilePath) {
        zip(files.toArray(new File[0]), zipFilePath);
    }

    /**
     * 把文件压缩成zip格式
     * @param files 需要压缩的文件集合
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void zip(File[] files, String zipFilePath) {
        if (files != null && files.length > 0) {
            if (isZip(zipFilePath)) {
                try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(zipFilePath))) {
                    //使用Zip64扩展
                    zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

                    // 将每个文件用ZipArchiveEntry封装，再用ZipArchiveOutputStream写到压缩文件中
                    for (File file : files) {
                        if (file.exists()) {
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
                            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
                            if (file.isDirectory()) {
                                continue;
                            }
                            IOUtils.copy(new FileInputStream(file), zipArchiveOutputStream);
                            zipArchiveOutputStream.closeArchiveEntry();
                        }
                    }
                    zipArchiveOutputStream.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 解压到当前目录
     */
    public static void unzip(String zipFilePath) {
        unzip(zipFilePath, new File(zipFilePath).getParent());
    }

    /**
     * 把zip文件解压到指定的文件夹
     * @param zipFilePath zip文件路径, 如 "D:/test/aa.zip"
     * @param saveFileDir 解压后的文件存放路径, 如"D:/test/" ()
     */
    public static void unzip(String zipFilePath, String saveFileDir) {
        if (!saveFileDir.endsWith(File.separator)) {
            saveFileDir += File.separator;
        }
        File dir = new File(saveFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(zipFilePath);
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file);
                 ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(is)) {
                ArchiveEntry archiveEntry;
                while ((archiveEntry = zipArchiveInputStream.getNextEntry()) != null) {
                    // 获取文件名
                    String entryFileName = archiveEntry.getName();
                    // 构造解压出来的文件存放路径
                    String entryFilePath = saveFileDir + entryFileName;
                    // 把解压出来的文件写到指定路径
                    File entryFile = new File(entryFilePath);
                    if (entryFileName.endsWith(File.separator)) {
                        entryFile.mkdirs();
                    } else {
                        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                            IOUtils.copy(zipArchiveInputStream, outputStream);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 把文件压缩成tar格式
     * @param path 需要压缩的文件的路径
     * @param tarFilePath 压缩后的tar文件路径 ,如"D:/test/aa.tar.gz";
     */
    public static void tar(String path, String tarFilePath) {
        File file = new File(path);
        List<String> files = getFiles(file);
        tar(file.getAbsolutePath(), files.toArray(new String[0]), tarFilePath);
    }

    /**
     * 把文件压缩成tar格式
     * @param file 需要压缩的文件 ,如"D:/test;
     * @param tarFilePath 压缩后的tar文件路径 ,如"D:/test/aa.tar.gz";
     */
    public static void tar(File file, String tarFilePath) {
        List<String> files = getFiles(file);
        tar(file.getAbsolutePath(), files.toArray(new String[0]), tarFilePath);
    }

    /**
     * 把文件压缩成tar格式
     * @param dirPath 父目录路径
     * @param paths 需要压缩的文件的路径集合
     * @param tarFilePath 压缩后的tar文件路径 ,如"D:/test/aa.tar.gz";
     */
    private static void tar(String dirPath, String[] paths, String tarFilePath) {
        if (paths != null && paths.length > 0) {
            if (!dirPath.endsWith(File.separator)) {
                dirPath += File.separator;
            }
            if (isTar(tarFilePath)) {
                try (TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(tarFilePath)))) {
                    // 将每个文件用ZipArchiveEntry封装，再用ZipArchiveOutputStream写到压缩文件中
                    for (String path : paths) {
                        File file = new File(path);
                        if (file.exists()) {
                            String name = path.replace(dirPath, "").replace("\\", File.separator);
                            TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, name);
                            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                            if (file.isDirectory()) {
                                continue;
                            }
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                IOUtils.copy(fileInputStream, tarArchiveOutputStream);
                                tarArchiveOutputStream.closeArchiveEntry();
                            }
                        }
                    }
                    tarArchiveOutputStream.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 把文件压缩成tar格式
     * @param files 需要压缩的文件集合
     * @param tarFilePath 压缩后的tar文件路径 ,如"D:/test/aa.tar.gz";
     */
    public static void tar(List<File> files, String tarFilePath) {
        tar(files.toArray(new File[0]), tarFilePath);
    }

    /**
     * 把文件压缩成tar格式
     * @param files 需要压缩的文件集合
     * @param tarFilePath 压缩后的tar文件路径 ,如"D:/test/aa.tar.gz";
     */
    public static void tar(File[] files, String tarFilePath) {
        if (files != null && files.length > 0) {
            if (isTar(tarFilePath)) {
                try (TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(tarFilePath)))) {
                    for (File file : files) {
                        if (file.exists()) {
                            TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, file.getName());
                            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                            if (file.isDirectory()) {
                                continue;
                            }
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                IOUtils.copy(fileInputStream, tarArchiveOutputStream);
                                tarArchiveOutputStream.closeArchiveEntry();
                            }
                        }
                    }
                    tarArchiveOutputStream.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 解压tar文件到当前目录
     */
    public static void untar(String tarFilePath) {
        untar(tarFilePath, new File(tarFilePath).getParent());
    }

    /**
     * 把tar文件解压到指定的文件夹
     * @param tarFilePath tar文件路径, 如 "D:/test/aa.tar.gz"
     * @param saveFileDir 解压后的文件存放路径, 如"D:/test/" ()
     */
    public static void untar(String tarFilePath, String saveFileDir) {
        if (!saveFileDir.endsWith(File.separator)) {
            saveFileDir += File.separator;
        }
        File dir = new File(saveFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(tarFilePath);
        if (file.exists()) {
            try (GzipCompressorInputStream gzipCompressorInputStream = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(file)));
                 TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipCompressorInputStream)) {
                ArchiveEntry archiveEntry;
                while ((archiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                    // 获取文件名
                    String entryFileName = archiveEntry.getName();
                    // 构造解压出来的文件存放路径
                    // 把解压出来的文件写到指定路径
                    File entryFile = new File(saveFileDir + entryFileName);
                    if (entryFileName.endsWith(File.separator)) {
                        entryFile.mkdirs();
                    } else {
                        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                            IOUtils.copy(tarArchiveInputStream, outputStream);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 判断文件名是否是zip文件
     * @param fileName 需要判断的文件路径
     * @return 是zip文件返回true, 否则返回false
     */
    public static boolean isZip(String fileName) {
        return StringUtils.isNotEmpty(fileName) && (fileName.endsWith(".ZIP") || fileName.endsWith(".zip"));
    }

    /**
     * 判断文件名是否是tar文件
     * @param path 需要判断的文件路径
     * @return 是tar文件返回true, 否则返回false
     */
    public static boolean isTar(String path) {
        return StringUtils.isNotEmpty(path) && (path.endsWith(".TAR.GZ") || path.endsWith(".tar.gz"));
    }

    /**
     * 递归取到文件夹下的所有文件
     */
    public static List<String> getFiles(File file) {
        if (file.isFile()) {
            return Lists.newArrayList(file.getAbsolutePath());
        } else {
            List<String> lstFiles = new ArrayList<>();
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        lstFiles.add(f.getAbsolutePath());
                        lstFiles.addAll(getFiles(f));
                    } else {
                        String str = f.getAbsolutePath();
                        lstFiles.add(str);
                    }
                }
            }
            return lstFiles;
        }
    }

}
