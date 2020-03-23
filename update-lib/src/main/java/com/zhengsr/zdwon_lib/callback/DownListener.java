package com.zhengsr.zdwon_lib.callback;

import com.zhengsr.zdwon_lib.bean.ZBean;

/**
 * @author by  zhengshaorui on 2019/9/6
 * Describe:下载监听
 */
public interface DownListener extends BaseListener{
    void onSuccess(String filePath,String md5Msg);
    void onDownloading(ZBean bean);

}
