package com.example.clocky.utilities

import android.content.Context
import android.widget.Toast

object MyToast {
    fun showToastShort(context : Context, message : String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(context : Context, message : String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}