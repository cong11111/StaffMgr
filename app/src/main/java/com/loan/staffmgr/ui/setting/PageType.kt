package com.loan.staffmgr.ui.setting

import androidx.annotation.IntDef
import com.loan.staffmgr.ui.setting.PageType.Companion.ABOUT
import com.loan.staffmgr.ui.setting.PageType.Companion.FEED_BACK
import com.loan.staffmgr.ui.setting.PageType.Companion.LOGOUT


@IntDef(FEED_BACK, LOGOUT, ABOUT)
@Retention(AnnotationRetention.SOURCE)
annotation class PageType {
    companion object {
        const val FEED_BACK = 1
        const val LOGOUT = 2
        const val ABOUT = 3

    }
}