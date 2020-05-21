package com.example.jetpackkotlin.ui.utility

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class DisplayAlert {



    fun displayTwoButtonDialog(context: Context, title: String, message: String) {

        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)

        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener(positiveButtonClick))
        dialogBuilder.show()

    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
    }

}