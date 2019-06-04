# 健康管家

## 功能模块
### 本应用主要设计和实现了四个功能模块：
- 用户信息管理模块：个人信息的填写和维护，运动历史查看，运动计划制定等服务，用户登录注册。
- 健康管理模块：对用户进行运动管理和体重管理，综合多种数据生成身体健康报告。
- 健康资讯模块：推送体育、运动和饮食等与健康相关的文章及新闻。
- 健康商城模块：展示，推荐和售卖与健康管理相关的商品，并实现了购物车功能。

## 功能实现
### 用户模块
![登录](https://github.com/plzspara/image/raw/master/healthManager/login.jpg)
![注册](https://github.com/plzspara/image/raw/master/healthManager/register.jpg?token=AIBNGWO44LYTSBDMKFHYSVS46XIE4)
![个人](https://github.com/plzspara/image/raw/master/healthManager/personal.jpg?token=AIBNGWKQJEELK3HJDMAVFYS46XIDK)
![周运动历史](https://github.com/plzspara/image/raw/master/healthManager/week_sportHis.jpg?token=AIBNGWLMGSPP4WLOMPDJMGC46XIIK)
![月运动历史](https://github.com/plzspara/image/raw/master/healthManager/month_sportHis.jpg?token=AIBNGWIOE2ZYJEGVUYZ3UNK46XH4K)
![运动报告](https://github.com/plzspara/image/raw/master/healthManager/healthReport.jpg?token=AIBNGWNBSCCQ4VVTBJFDWW246XHZK)

### 健康管理模块
![运动](https://github.com/plzspara/image/raw/master/healthManager/run.jpg?token=AIBNGWMV7R5RRRU6F7ONFKS46XIGA)
![体重管理](https://github.com/plzspara/image/raw/master/healthManager/weight.jpg?token=AIBNGWKK5GVSEZFF2NHSRAK46XIJI)

### 体育新闻模块
![新闻列表](https://github.com/plzspara/image/raw/master/healthManager/newsList.jpg?token=AIBNGWKXHDQA4UBTOKOTLAK46XIB6)
![新闻](https://github.com/plzspara/image/raw/master/healthManager/news.jpg?token=AIBNGWNOYD2LN6E3OQ25UP246XH7E)

### 商城模块
![商品列表](https://github.com/plzspara/image/raw/master/healthManager/goods.jpg?token=AIBNGWJRCSKUGGECXEDQ4RC46XHQW)
![商品详情](https://github.com/plzspara/image/raw/master/healthManager/goodsDe.jpg?token=AIBNGWIB53R2CWRJUPYFTMC46XHYE)
![购物车](https://github.com/plzspara/image/raw/master/healthManager/shopCar.jpg?token=AIBNGWLKQLW6YONC6DQ2VTS46XIHG)


## 关键技术
### 百度地图API SDK介绍
百度地图 Android SDK是基于Android 4.0及以上版本设备的应用程序接口，通过使用百度地图的SDK接口，开发者能够轻松访问百度地图的服务和数据，创建功能多样、交互性强的地图类的应用程序。百度地图 的Android定位SDK，能够支持全球定位，可以精准的获取经纬度信息。
本系统中利用百度地图SDK对手机GPS和方向传感器的监听，GPS会返回当前坐标点，方向传感器会返回当前方向度数，然后将当前坐标点和方向度数显示在百度地图控件上，所有的这些点连接起来就形成一条线，即是用户的运动轨迹。

### 检测步数技术介绍
在Android 4.4之前，Android只支持加速度传感step_detector, 用户每迈出一步，此传感器就会触发一个事件。对于每个用户步伐，此传感器提供一个返回值为 1.0 的事件和一个指示此步伐发生时间的时间戳, 如果检测到了波峰，符合时间差条件，波峰波谷差值比initialValue大，将该差值纳入阈值的计算中，判定为1步。但此传感器只能检测到单个有效的步伐，获取单个步伐的有效数据，如果需要统计一段时间内的步伐总数，则需要使用step_counter计步传感器。
在Android4.4之后，Android在硬件中支持内置计步传感器，例如微信运动，支付宝运动等常用软件都是直接调用了Android中的Sensor传感器服务，从而获取到每日的步数。此传感器会针对检测到的每个步伐触发一个事件，但提供的步数是自设备启动激活该传感器以来累计的总步数，在每次设备重启后会清零，在每次运动开始时，记录该传感器的值，在运动过程中，减去开始的步数，即为本次运动的步数，另外需要将每天运动的步数持久化保存起来，在每天0点的时候就初始化为0，这样就可以显示并记录每天的步数。该传感器需要硬件支持的，优点是非常省电。

### Bmob后端云服务器介绍
开发者使用Bmob提供的数据与文件存储功能，能够快速实现应用数据的存储。数据存储不仅能存储常规应用文本信息，还能存储图片、视频、音频、地理位置等信息存储。本系统中采用Bmob后端云作为服务器，通过使用Bmob SDK API完成对数据库增删查改、文件上传和短信注册功能。

### QQ第三方登录介绍
QQ互联给开发者提供了多种的接入选择，使开发者能够根据程序的要求选择合适的选择方案。对于需要快速接入的应用，可以接入QQ登录，QQ登录能够使应用免去注册的流程，并且可以充分利用庞大的QQ用户群实现快速传播。本系统使用open SDK的API，实现用户使用QQ第三方登录注册，减少了用户注册登录的时间。

### 系统使用的框架介绍
- OkHttp网络请求框架。HTTP是现代应用常用的一种交换数据和媒体的网络请求方式，高效地使用HTTP能让资源加载更高效，节省带宽。当网络出现问题的时候OkHttp依然能够坚守职责，它会自动恢复一般的连接问题。本系统用OkHttp进行聚合数据新闻资讯API的网络请求。
- Glide图片加载框架。Glide是Google官方推荐使用的一个图片加载和缓存的开源库，它不仅能够实现平滑的图片列表滚动效果，还可以支持远程图片的获取、大小调整和展示，而且可以加载GIF图片。本系统用Glide加载并缓存网络图片。
- CircleImageView圆形图片视图框架。CircleImageView可以截取部分图片并显示中间圆形部分。本系统用此框架作为用户头像显示。
- MPAndroidChart图表框架。MPAndroidChart是一个基于Android的开源图表库，它能实现很多常用的图表类型，如：线型图、饼图、柱状图和散点图等图。另外，它还提供了对图表的操作功能，如拖拽、缩放、显示动画效等效果。本系统使用此框架的柱形图显示用户运动的历史记录。
- Rxjava处理异步事件框架。Rxjava是一个基于观察者模式，使用链式编程，异步消息处理的程序的库。本系统用此框架对Bmob做网络请求进行异步处理。
