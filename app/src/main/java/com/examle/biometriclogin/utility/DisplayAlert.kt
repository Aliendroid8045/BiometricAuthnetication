package com.examle.biometriclogin.utility

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.biometriclogin.R

class DisplayAlert {

    fun displayTwoButtonDialog(context: Context, title: String, message: String) {

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener(positiveButtonClick))
        dialogBuilder.show().window?.setBackgroundDrawableResource(R.drawable.button_background)
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
    }

}