package com.cmcc.myinstallapk.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by 41569 on 2018/5/7.
 */

public class AssetsUtil {
    /**
     * 解压Assets文件
     *
     * @param assetsDir assets的原始目录(传入""则表示assets的根目录)
     * @param toDir     解压到的目录
     * @param filter    文件的后缀名过滤器
     * @return true -- 全部成功 false -- 失败(可能是中途某个失败)
     */
    public static boolean extractAssets(String assetsDir, File toDir, String filter) {
        if (toDir == null || assetsDir == null) {

            return false;
        }

        // 创建目标目录
        toDir.mkdirs();
        if (!toDir.exists()) {

            return false;
        }

        try {
            // 获取assets目录的文件
            AssetManager assetManager = Utils.getApp().getAssets();
            String[] assetsFiles = assetManager.list(assetsDir);
            if (assetsFiles == null || assetsFiles.length == 0) {

                return false;
            }

            for (String asset : assetsFiles) {
                // 是否匹配后缀
                if (filter != null && !asset.endsWith(filter)) {

                    continue;
                }

                // 拷贝文件到输出目录
                InputStream in;
                if (assetsDir != null && assetsDir.length() > 0) {
                    in = assetManager.open(assetsDir + "/" + asset);
                } else {
                    in = assetManager.open(asset);
                }
                OutputStream out = new FileOutputStream(new File(toDir, asset));
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 提取Assets单个文件
     *
     * @param assetsPath assets的原始文件path
     * @return 文件路径
     */
    public static String copeAssets2CacheDir(String assetsPath) {
        if (TextUtils.isEmpty(assetsPath)) {

            return "";
        }
        // 拷贝文件到输出目录
        InputStream in = null;
        BufferedOutputStream os = null;
        try {
            // 获取assets目录的文件
            AssetManager assetManager = Utils.getApp().getAssets();

            in = assetManager.open(assetsPath);
            os = new BufferedOutputStream(new FileOutputStream(new File(Utils.getApp().getExternalCacheDir() + File.separator + assetsPath)));
            byte[] data = new byte[1024];
            int len;
            while ((len = in.read(data, 0, 1024)) != -1) {
                os.write(data, 0, len);
            }
            return Utils.getApp().getExternalCacheDir() + File.separator + assetsPath;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            CloseUtils.closeIO(in, os);
        }
    }

    /**
     * 提取Assets单个文件
     *
     * @param assetsPath  assets的原始文件path
     * @param charsetName 编码格式
     * @return 文件路径
     */
    public static String getAssetsString(String assetsPath, String charsetName) {
        if (TextUtils.isEmpty(assetsPath)) {

            return "";
        }
        InputStream in = null;
        BufferedReader reader = null;

        try {
            // 获取assets目录的文件
            AssetManager assetManager = Utils.getApp().getAssets();
            // 拷贝文件到输出目录
            in = assetManager.open(assetsPath);

            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(in));
            } else {
                reader = new BufferedReader(new InputStreamReader(in, charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);// windows系统换行为\r\n，Linux为\n
            }
            // 要去除最后的换行符
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.closeIO(in);
            CloseUtils.closeIO(reader);
        }
    }
}
