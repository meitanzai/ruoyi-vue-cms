package com.chestnut.contentcore.core;

import com.chestnut.contentcore.domain.CmsSite;
import com.chestnut.contentcore.util.SiteUtils;
import jodd.io.ZipBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 站点主题导出上下文
 *
 * 保存导出过程中的临时数据供各模块使用
 * 导出文件可保存至siteResourceRoot/_export/theme/目录下，数据库数据默认位于db目录下
 * theme目录下的导出临时文件会在导出逻辑最后按目录名打包
 * 目前已知目录：
 * 站点文件：wwwroot/
 * 数据库文件：db/
 */
@Getter
@Setter
public class SiteExportContext implements ISiteThemeContext {

    /**
     * 导出临时目录
     */
    static final String ExportDir = "_export/theme/";

    /**
     * 引用资源IDS
     */
    private Set<Long> resourceIds = new HashSet<>();

    /**
     * 导出内容ID列表
     */
    private Set<Long> contentIds = new HashSet<>();

    private final CmsSite site;

    /**
     * 是否在打包后清理临时目录
     */
    private boolean clearTempFile = true;

    public SiteExportContext(CmsSite site) {
        this.site = site;
    }

    public void createZipFile(String zipPath) throws IOException {
        String siteResourceRoot = SiteUtils.getSiteResourceRoot(site);
        String zipFile = siteResourceRoot + zipPath;
        ZipBuilder zipBuilder = ZipBuilder.createZipFile(new File(zipFile));
        File exportDir = new File(siteResourceRoot + ExportDir);
        File[] files = exportDir.listFiles();
        if (Objects.nonNull(files)) {
            for (File f : files) {
                zipBuilder.add(f).path(f.getName()).recursive().save();
            }
        }
        zipBuilder.toZipFile();
        if (clearTempFile) {
            this.clearTempFiles();
        }
    }

    /**
     * 保存文件到${SiteDirPath}目录
     *
     * @param source 源文件
     * @param dest 目标路径，项目资源根目录（resourceRoot）
     */
    public void saveFile(File source, String dest) {
        try {
            dest = ExportDir + SiteDirPath + dest;
            File destFile = new File(SiteUtils.getSiteResourceRoot(site) + dest);
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, destFile);
            } else {
                FileUtils.copyFile(source, destFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(String tableName, String jsonData) {
        saveData(tableName, jsonData, 1);
    }

    public void saveData(String tableName, String jsonData, int index) {
        try {
            String path = ExportDir + DataDirPath + tableName + SPLITER + index + ".json";
            File f = new File(SiteUtils.getSiteResourceRoot(site) + path);
            FileUtils.writeStringToFile(f, jsonData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearTempFiles() throws IOException {
        String siteResourceRoot = SiteUtils.getSiteResourceRoot(site);
        FileUtils.deleteDirectory(new File(siteResourceRoot + ExportDir));
    }
}