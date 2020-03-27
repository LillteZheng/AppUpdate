# AppUpdate

开发中，我们常常会需要有apk升级，或者下载某个文件的问题。所以这里就写了个通用的文件下载的功能 **ZDown**。通过这篇文章你将看到
 - 常用框架 API 接口设计
 - 多线程下载原理与实现
 - 后台下载，界面退出之后，进来继续显示下载UI的原理

[原理请参考这篇博客](https://blog.csdn.net/u011418943/article/details/85760069)



效果图
<table  align="center">

  <tr>
    <td><a href="url"><img src="https://github.com/LillteZheng/AppUpdate/raw/master/gif/update.gif" align="left"height="789" width="479"></a></td>
  </tr>

</table>

## 配置
```
allprojects {
    repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
}
```
然后把 ZDloader 写上：
[![](https://jitpack.io/v/LillteZheng/AppUpdate.svg)](https://jitpack.io/#LillteZheng/AppUpdate)
```
implementation 'com.github.LillteZheng:AppUpdate:lastest'
```
## 一、检查版本

```
 ZDown.with(this)
        .url(jsonUrlTest)
        .listener(new CheckListener<TestBean>(TestBean.class) {
            @Override
            public void onCheck(final TestBean data) {


            }

            @Override
            public void onFail(String errorMsg) {
                Log.d(TAG, "zsr onFail: " + errorMsg);
            }
        }).check();

```

在listener中，可以把要转换的数据写上，如果不想转换成实体 bean，直接 String.class 就是返回原始的字符串了。


在检查完版本，可以使用如下代码下载文件：



```
ZDown.with(MainActivity.this)
    .url(fileUrlTest)
    //线程设置为3
    .threadCount(3)
    //ui刷新时间为 500 毫秒
    .reFreshTime(500)
    //路径保存的路径，默认内部存
    .filePath(mPath)
    //.allowBackDownload(true)  是否允许后台更新

 //   .fileName("test.apk") fileName默认根绝连接去截取，也可以自己写
    .listener(new TaskListener() {
        @Override
        public void onSuccess(String filePath, String md5Msg) {
            ZCommontUitls.installApk(MainActivity.this,filePath);
            dialog.dismiss();
        }

        @Override
        public void onDownloading(ZBean bean) {
            int progress = (int) bean.progress;
            updateBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);

        }

        @Override
        public void onFail(String errorMsg) {
            Log.d(TAG, "zsr onFail: " + errorMsg);
            dialog.dismiss();
        }
    }).down();
    
```

ZDown 为程序入口，它提供以下方法：

- pause() 暂停任务
- start() 开始任务
- stopTask() 停止任务 ，如果你的apk更新不是在 activity 使用，建议在app退出的时候，使用该方法，防止内存泄漏
- stopTaskAndDeleteCache() 停止任务，并删除已文件和数据库，当任务失败时，可以使用
- isTaskExists() 任务是否存在
- isRunning() 是否正在下载
- updateListener() 从后台退回来，如果任务正在下载，直接更新接口就可以了，UI就不会乱了
- deleteCacheAndStart() 当任务失败时，可以用这个把缓存文件和数据删了

