package com.prismosis.checklist.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.prismosis.checklist.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Ehsan Saddique on 2020-03-13
 */

object Utils {
    fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus != null) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

    fun showSoftKeyboard(activity: Activity, view: View) {
        if (activity.currentFocus != null) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showSnackBar(rootView: View, text: String, isSticky: Boolean = true) {
        var textWithBottomMargin = text + "\n"
        val snack = Snackbar.make(rootView, textWithBottomMargin, Snackbar.LENGTH_LONG)
        val view = snack.getView()
        val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as? TextView
        tv?.setTextColor(Color.WHITE)
        tv?.maxLines = 10

        if (isSticky) {
            snack.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE)
            snack.setAction("Dismiss", View.OnClickListener {
                //--
            })
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post { snack.show() }
    }

    fun showDialog(
        context: Context,
        title: String?,
        message: String,
        positiveBtnText: String,
        showNegativeBtn: Boolean,
        positiveBtnClickListener: DialogInterface.OnClickListener?
    ) {
        val alertTitle = title ?: ""
        val dialogBuilder: AlertDialog.Builder
        dialogBuilder = AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(alertTitle)
            .setMessage(message)
            .setPositiveButton(positiveBtnText, positiveBtnClickListener)


        if (title == null) {
            dialogBuilder.setIcon(R.mipmap.ic_launcher)
        }

        if (showNegativeBtn) {
            dialogBuilder.setNegativeButton("Cancel", null)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post { dialogBuilder.create().show() }
    }

    fun stringFromDate(date: Date) : String {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        return format.format(date)
    }

    fun dateFromString(dateString: String) : Date {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        var date: Date
        try {
            date = format.parse(dateString)
        }
        catch (ex: Exception) {
            date = Date()
        }

        return date
    }
}