package com.loan.staffmgr.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.global.AppManager
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.model.Response

abstract class BaseActivity : AppCompatActivity() {

    fun toFragment(fragment: BaseFragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction() // 开启一个事务
            transaction.replace(getFragmentContainerRes(), fragment)
            transaction.commitAllowingStateLoss()
        }
    }

    @IdRes
    protected open fun getFragmentContainerRes(): Int {
//        return R.id.fl_sign_up_container
        return -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppManager.sInstance.addActivity(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        AppManager.sInstance.finishActivity(this)
        super.onDestroy()
    }

    fun addFragment(fragment: BaseFragment?, tag: String?) {
        if (fragment != null) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(getFragmentContainerRes(), fragment, tag)
            beginTransaction.addToBackStack(tag)
            beginTransaction.commitAllowingStateLoss()
        }
    }

    protected fun isDestroy() : Boolean{
        if (isFinishing || isDestroyed) {
            return true
        }
        return false
    }

    fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
        return CheckResponseUtils.checkResponseSuccess(response, clazz)
    }

    fun checkResponseSuccess(response: Response<String>): String? {
        return CheckResponseUtils.checkResponseSuccess(response)
    }

    protected fun checkClickFast(): Boolean {
        return checkClickFast(true)
    }

    private var lastClickMillions: Long = 0
    protected fun checkClickFast(showFlag : Boolean): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 3000) {
            if (showFlag) {
                ToastUtils.showShort("Click too fast, please try again later")
            }
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }

    protected fun checkShortClickFast(): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 500) {
            ToastUtils.showShort("Click too fast, please try again later")
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }
}