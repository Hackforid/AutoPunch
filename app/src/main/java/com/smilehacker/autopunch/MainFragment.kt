package com.smilehacker.autopunch

import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import com.smilehacker.Megatron.KitFragment
import kotlinx.android.synthetic.main.frg_main.*


/**
 * Created by kleist on 2017/2/8.
 */

class MainFragment: KitFragment() {
    val TAG = MainFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.frg_main, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switch_boot.isChecked = Prefs.enable
        switch_boot.setOnCheckedChangeListener { compoundButton, b ->
            run {
                if (b) {
                    if (isServiceEnabled()) {
                        Prefs.enable = true
                    } else {
                        Prefs.enable = false
                        switch_boot.isChecked = false
                        askEnableService()
                    }
                } else {
                    Prefs.enable = false
                }
            }
        }

    }

    private fun isServiceEnabled() : Boolean {
        val am = hostActivity.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val installedAccessibilityServiceList = am.getEnabledAccessibilityServiceList(FEEDBACK_GENERIC)
        return installedAccessibilityServiceList.any { "com.smilehacker.autopunch/.AutoService" == it.id }
    }

    private fun askEnableService() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("服务未开启")
        builder.setMessage("请开启服务")
        builder.setPositiveButton("去设置",
                { dialog, i -> gotoOpenService(); dialog.dismiss() }
        )
        builder.setNegativeButton("取消", {dialog, i -> dialog.dismiss()})
        builder.setCancelable(true)
        builder.create().show()
    }

    private fun gotoOpenService() {
        startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                123)
    }
}
