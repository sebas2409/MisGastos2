package com.watermelon.misgastos2

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {

    private val storage: SharedPreferences = context.getSharedPreferences("MyDB",0)

    fun saveUser(name: String){
        storage.edit().putString("user",name).apply()
    }

    fun getUser(): String{
        return storage.getString("user","")!!
    }
}