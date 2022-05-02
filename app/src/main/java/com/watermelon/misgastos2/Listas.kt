package com.watermelon.misgastos2

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.watermelon.misgastos2.LoadSharedPrefs.Companion.prefs
import com.watermelon.misgastos2.databinding.ActivityListasBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


const val TOPIC = "/topics/Gastos"

class Listas : AppCompatActivity() {

    private lateinit var binding: ActivityListasBinding
    private val database = FirebaseDatabase.getInstance("https://misgastos2-3474b-default-rtdb.europe-west1.firebasedatabase.app/")
    val listass = ArrayList<Gastos>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MisGastos2)
        binding = ActivityListasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        title= "Hola ${prefs.getUser()}!"

        binding.rvListas.layoutManager= LinearLayoutManager(this)
        binding.rvListas.setHasFixedSize(true)
        getData()

        binding.btnAdd.setOnClickListener{
            val view = EditText(this)
            view.hint="Nombre de la lista"
            AlertDialog.Builder(this)
                .setTitle("Ingrese el nombre de la lista")
                .setView(view)
                .setPositiveButton("Añadir"){dialog, _ ->
                        if (view.text.isNotEmpty()){
                            val nombre = view.text.toString()
                            val lista = Gastos(nombre)
                            database.getReference("Listas").child(nombre).setValue(lista).addOnSuccessListener {
                                PushNotification(NotificationData("Se ha realizado una modificacion","${prefs.getUser()} Ha añadido una lista llamada $nombre"), TOPIC).also {
                                    sendNotification(it)
                                }
                                getData()
                                dialog.dismiss()
                            }
                        }else{
                            Toast.makeText(this,"Por favor ingrese un nombre de usario", Toast.LENGTH_LONG).show()
                        }
                    }
                .create().show()
        }

        val swipeGesture = object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT->{
                        Adapter(this@Listas,listass).deleteItem(viewHolder.adapterPosition+1)
                        database.getReference("Listas").child(listass[viewHolder.adapterPosition].nombre!!).removeValue().addOnSuccessListener {
                            PushNotification(NotificationData("Se ha realizado una modificacion","${prefs.getUser()} Ha eliminado una lista"), TOPIC).also {
                                sendNotification(it)
                            }
                            getData()
                        }
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvListas)
    }

    private fun getData() {
        database.getReference("Listas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                listass.clear()
                for (nombre in p0.children){
                    val listas = nombre.getValue(Gastos::class.java)
                    listass.add(listas!!)
                }
                binding.rvListas.adapter=Adapter(this@Listas,listass)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                Log.d("Error","Response: enviado")
            }else{
                Log.e("Error", response.errorBody().toString())
            }
        }catch (e: Exception){
            Log.e("Error",e.toString())
        }
    }

}

