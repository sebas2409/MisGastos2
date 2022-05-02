package com.watermelon.misgastos2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.watermelon.misgastos2.LoadSharedPrefs.Companion.prefs
import com.watermelon.misgastos2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MisGastos2)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUser()
        binding.btnSave.setOnClickListener { guardarUsuario() }
    }

    private fun guardarUsuario() {
        if (binding.etUser.text.isNotEmpty()){
            val nombre = binding.etUser.text.toString()
            prefs.saveUser(nombre)
            startActivity(Intent(this,Listas::class.java))
        }else{
            Toast.makeText(this,"Por favor ingrese un nombre de usario",Toast.LENGTH_LONG).show()
        }
    }

    private fun checkUser(){
        if (prefs.getUser().isNotEmpty()){
            startActivity(Intent(this,Listas::class.java))
        }
    }
}