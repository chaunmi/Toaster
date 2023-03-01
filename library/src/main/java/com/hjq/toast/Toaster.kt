package com.hjq.toast

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.res.Resources.NotFoundException
import android.widget.Toast
import com.hjq.toast.config.IToastInterceptor
import com.hjq.toast.config.IToastStrategy
import com.hjq.toast.config.IToastStyle
import com.hjq.toast.style.BlackToastStyle
import com.hjq.toast.style.CustomToastStyle
import com.hjq.toast.style.LocationToastStyle

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/Toaster
 * time   : 2018/09/01
 * desc   : Toast 框架（专治 Toast 疑难杂症）
 */
object Toaster {
    /** Application 对象  */
    private var sApplication: Application? = null

    /** Toast 处理策略  */
    private var toastStrategy: IToastStrategy? = null
    /**
     * 初始化全局的 Toast 样式
     *
     * 样式实现类，框架已经实现两种不同的样式
     * 黑色样式：[BlackToastStyle]
     * 白色样式:
     */
    /** Toast 样式  */
    @JvmStatic
    var toastStyle: IToastStyle<*>? = null
    /**
     * 设置 Toast 拦截器（可以根据显示的内容决定是否拦截这个Toast）
     * 场景：打印 Toast 内容日志、根据 Toast 内容是否包含敏感字来动态切换其他方式显示（这里可以使用我的另外一套框架 XToast）
     */
    /** Toast 拦截器（可空）  */
    var interceptor: IToastInterceptor? = null

    /** 调试模式  */
    private var sDebugMode: Boolean? = null
    private const val MAX_LENGTH = 24

    /**
     * 初始化 Toast,需要在 Application.create 中初始化
     *
     * @param application       应用的上下文
     * @param strategy          Toast 策略
     * @param style             Toast 样式
     */
    @JvmStatic
    fun init(application: Application, strategy: IToastStrategy? = null, style: IToastStyle<*>? = null) {
        sApplication = application

        // 初始化 Toast 策略
        Toaster.strategy = strategy ?: ToastStrategy()
        // 设置 Toast 样式
        toastStyle = style ?: BlackToastStyle(application)
    }

    /**
     * 判断当前框架是否已经初始化
     */
    @JvmStatic
    val isInit: Boolean
        get() = sApplication != null && toastStrategy != null && toastStyle != null

    /**
     * 延迟显示 Toast
     */
    @JvmStatic
    fun delayedShow(resId: Int, delayMillis: Long) {
        delayedShow(stringIdToCharSequence(resId), delayMillis)
    }

    @JvmStatic
    fun delayedShow(text: CharSequence, delayMillis: Long) {
        val params = ToastParams()
        params.text = text
        params.delayMillis = delayMillis
        show(params)
    }

    /**
     * debug 模式下显示 Toast
     */
    @JvmStatic
    fun debugShow(resId: Int) {
        debugShow(stringIdToCharSequence(resId))
    }
    @JvmStatic
    fun debugShow(text: CharSequence) {
        if (!isDebugMode) {
            return
        }
        val params = ToastParams()
        params.text = text
        show(params)
    }

    /**
     * 显示一个短 Toast
     */
    @JvmStatic
    fun showShort(resId: Int) {
        showShort(stringIdToCharSequence(resId))
    }
    @JvmStatic
    fun showShort(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        params.duration = Toast.LENGTH_SHORT
        show(params)
    }

    /**
     * 显示一个长 Toast
     */
    @JvmStatic
    fun showLong(resId: Int) {
        showLong(stringIdToCharSequence(resId))
    }

    @JvmStatic
    fun showLong(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        params.duration = Toast.LENGTH_LONG
        show(params)
    }

    /**
     * 显示 Toast
     */
    @JvmStatic
    fun show(resId: Int) {
        show(stringIdToCharSequence(resId))
    }
    @JvmStatic
    fun show(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        show(params)
    }

    @JvmStatic
    fun show(params: ToastParams) {
        checkInitStatus()

        // 如果是空对象或者空文本就不显示
        if (params.text == null || params.text.isEmpty()) {
            return
        }
        if (params.strategy == null) {
            params.strategy = toastStrategy
        }
        if (params.interceptor == null) {
            if (interceptor == null) {
                interceptor = ToastLogInterceptor()
            }
            params.interceptor = interceptor
        }
        if (params.style == null) {
            params.style = toastStyle
        }
        if (params.interceptor.intercept(params)) {
            return
        }
        if (params.duration == -1) {
            params.duration = if (params.text.length > 20) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        }

//        if(params.text.length() > MAX_LENGTH) {
//            params.text = params.text.subSequence(0, 24) + "...";
//        }
        params.strategy.showToast(params)
    }

    /**
     * 取消吐司的显示
     */
    @JvmStatic
    fun cancel() {
        toastStrategy?.cancelToast()
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity           重心
     */
    @JvmStatic
    fun setGravity(gravity: Int) {
        setGravity(gravity, 0, 0)
    }
    @JvmStatic
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        setGravity(gravity, xOffset, yOffset, 0f, 0f)
    }
    @JvmStatic
    fun setGravity(
        gravity: Int,
        xOffset: Int,
        yOffset: Int,
        horizontalMargin: Float,
        verticalMargin: Float
    ) {
        toastStyle =
            LocationToastStyle(toastStyle, gravity, xOffset, yOffset, horizontalMargin, verticalMargin)
    }

    /**
     * 给当前 Toast 设置新的布局
     */
    @JvmStatic
    fun setView(resId: Int) {
        if (resId <= 0) {
            return
        }
        toastStyle = CustomToastStyle(
            resId, toastStyle!!.gravity,
            toastStyle!!.xOffset, toastStyle!!.yOffset,
            toastStyle!!.horizontalMargin, toastStyle!!.verticalMargin
        )
    }

    /**
     * 设置 Toast 显示策略
     */
    @JvmStatic
    var strategy: IToastStrategy?
        get() = toastStrategy
        set(strategy) {
            toastStrategy = strategy
            toastStrategy!!.registerStrategy(sApplication)
        }

    /**
     * 检查框架初始化状态，如果未初始化请先调用[Toaster.init]
     */
    private fun checkInitStatus() {
        // 框架当前还没有被初始化，必须要先调用 init 方法进行初始化
        checkNotNull(sApplication) { "Toaster has not been initialized" }
    }

    /**
     * 是否为调试模式
     */
    @JvmStatic
    var isDebugMode: Boolean
        get() {
            if (sDebugMode == null) {
                checkInitStatus()
                sDebugMode =
                    sApplication!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            }
            return sDebugMode!!
        }
        set(debug) {
            sDebugMode = debug
        }

    private fun stringIdToCharSequence(resId: Int): CharSequence {
        checkInitStatus()
        return try {
            // 如果这是一个资源 id
            sApplication!!.resources.getText(resId)
        } catch (ignored: NotFoundException) {
            // 如果这是一个 int 整数
            resId.toString()
        }
    }
}