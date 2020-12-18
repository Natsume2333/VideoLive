# 时遇Android项目说明

### 开发环境
开发工具：Android Studio 3.0 
开发环境：Mac OS Windows
JDK 8

注意：一定要跑真机，不要跑模拟器

建议真机跑项目


### 第三方配置

- 修改配置微信、QQ、等三方登录分享KEY在 app/src/main/assets/ShareSDK.xml中

- 修改高德地图、Umeng、腾讯云APPID 在valuse/system_string.xml

- 签名文件密码在build.gradle中，签名文件一般在项目根目录

### 项目Model说明

- timImUi 腾讯云IM UI库
- timPresentation 腾讯云IM 自带相关业务
- timSdk 腾讯云IM SDK
- utilscode 工具类库
- imagelibrary 图片选择库
- bogo 项目主Model
- bogoBeauty 美颜SDK

### 第三方使用说明：

* 即时通讯SDK：腾讯云即时通讯
* 视频通话SDK：声网-视频通话
* 图片视频存储SDK：七牛云
* 定位SDK：高德地图
* 美颜SDK：布谷科技内部渠道
* 推送SDK：umeng推送
* 分享登录SDK：ShareSDK
* 邀请溯源SDK：Openinstall
