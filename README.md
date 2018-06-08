# WebViewDemo

在安卓开发中，当使用到WebView加载H5页面时，因为看不到网页的具体加载进度，这点用户体验不是很好，所以为了提高用户体验，我们可以在加载WebView时在其上方加入进度条以显示加载进度，待页面加载完成后隐藏掉。

万能的google已经在WebChromeClient类的onProgressChanged函数中给我们提供了当前的页面加载进度newProgress，我们只需要在布局中写个style="@android:style/Widget.ProgressBar.Horizontal" 的ProgressBar,然后再代码中给它设置newProgress待加载完成后将其隐藏即可。

看看效果图：

 ![image](https://github.com/weioule/WebViewDemo/blob/master/app/img/img_01.png)

当然了，这只是普普通通的进度条，网页返回的进度多少它就显示多少，一般的我们都知道，加载h5的页面嘛，进度基本都是一下停一下走的，速度很不协调想。

想想也是很影响用户体验啊，所以我想写个加载速度比较协调的进度条，主要思路是将网页返回的速度跨度拆分显示进度条，再利用Timer和TimerTask定时显示进度。

使用的时候把项目down下来，需要用的类copy到你的项目，代码里都有注释说明的。用法和项目里面的MainActivity一样，只需要将url传入打开的WebViewH5Activity即可。WebViewH5Activity基本支持正常的使用了，WebSettings需要特殊的设置时再自行添加。

另外我还写了当网络异常时的提示页面，跟微信差不多，点击重新加载。看看效果图：

 ![image](https://github.com/weioule/WebViewDemo/blob/master/app/img/img_02.png)
 
 本次分享，希望能够帮助各位童鞋节省时间精力，也随便记录一波。
 
 如有修正的地方，欢迎指出！
