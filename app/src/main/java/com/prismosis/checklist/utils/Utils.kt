package com.prismosis.checklist.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.prismosis.checklist.R


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
        val tv = view.findViewById(android.support.design.R.id.snackbar_text) as? TextView
        tv?.setTextColor(Color.WHITE)
        tv?.maxLines = 10

        if (isSticky) {
            snack.setDuration(Snackbar.LENGTH_INDEFINITE)
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
}