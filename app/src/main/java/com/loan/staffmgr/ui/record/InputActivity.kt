package com.loan.staffmgr.ui.record

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity

class InputActivity : BaseActivity() {

    private var et: AppCompatEditText? = null
//    private var viewClear: View? = null
    private var viewCommit: View? = null

    companion object {
        var mText : String? = null
        fun getIntent(context: Context, text : String?) : Intent {
            mText = text
            val intent = Intent(context, InputActivity::class.java)
//            context.startActivityForResult(intent, 111)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.bg_color))
        BarUtils.setStatusBarLightMode(this@InputActivity, true)
        setContentView(R.layout.activity_input_note)
        et = findViewById<AppCompatEditText>(R.id.et_diary_edit_note_content)
//        viewClear = findViewById<View>(R.id.iv_clear)
        et?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                var text = ""
                if (et != null) {
                    text = et!!.text.toString()
                }
//                if (text != null && matches(text)) {
//                    finishAndInput()
//                } else {
//                    notMatchToast()
//                }
                return true
            }

        })
        et?.isFocusableInTouchMode = true
        et?.requestFocus()
        et?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) {
                    return
                }
                try {
                    val str = s.toString().trim()
//                    if (str.length > 0) {
//                        viewClear?.visibility = View.VISIBLE
//                    } else {
//                        viewClear?.visibility = View.GONE
//                    }
                } catch (e : java.lang.Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (isFinishing || isDestroyed) {
                return@Runnable
            }
            if (et != null) {
                KeyboardUtils.showSoftInput(et!!)
            }
        }, 300)
//        viewClear?.setOnClickListener(object : OnClickListener {
//            override fun onClick(v: View?) {
//                et?.setText("")
//            }
//
//        })
        viewCommit = findViewById<View>(R.id.fl_submit)
        viewCommit?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finishAndInput()
            }

        })
        if (!TextUtils.isEmpty(mText)) {
            et?.setText(mText)
            et?.setSelection(mText!!.length)
        }
        KeyboardUtils.registerSoftInputChangedListener(this@InputActivity, object :
            KeyboardUtils.OnSoftInputChangedListener {
            override fun onSoftInputChanged(height: Int) {
               val layoutParams =  viewCommit?.layoutParams as ViewGroup.MarginLayoutParams
                if (height == 0) {
                    layoutParams.bottomMargin = resources.getDimension(R.dimen.dp_15).toInt()
                } else {
                    layoutParams.bottomMargin = height + resources.getDimension(R.dimen.dp_2).toInt()
                }
                viewCommit?.layoutParams = layoutParams
            }

        })
    }

    private fun finishAndInput(needSetData : Boolean = true) {
        val intent = Intent()
        var data = ""
        if (et != null) {
            data = et!!.text.toString()
        }
        intent.putExtra("data", data)
        if (needSetData) {
            setResult(111, intent)
        } else {
            setResult(111)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (et != null) {
            KeyboardUtils.hideSoftInput(et!!)
        }
    }

    override fun onBackPressed() {
        finishAndInput(false)
    }
}