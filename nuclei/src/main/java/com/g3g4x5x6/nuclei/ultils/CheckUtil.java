package com.g3g4x5x6.nuclei.ultils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class CheckUtil {
    private CheckUtil() {
    }

    public static void checkEnv() {
        // 检查程序工作目录
        String workspace = NucleiConfig.getWorkPath();
        String[] pathArray = new String[]{
                NucleiConfig.getHomePath() + "/.nuclei-plus",   // 检测 nuclei-plus 工作空间目录
                workspace + "/bin/",                            // 检查终端配色目录
                workspace + "/config/theme",                    // 检查终端配色目录
                workspace + "/temp",                            // 检查缓存目录
                workspace + "/temp/nuclei",                     // 检查 Nuclei 运行缓存目录
                workspace + "/report/nuclei",                   // 检查 Nuclei 报告保存目录
                workspace + "/templates",                       // 检查定制模板目录
        };
        for (String path : pathArray) {
            File temp = new File(path);
            if (!temp.exists()) {
                if (!temp.mkdirs()) {
                    log.debug("目录创建失败：" + path);
                }
            }
        }
    }
}
