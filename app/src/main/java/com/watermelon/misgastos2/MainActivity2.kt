package com.watermelon.misgastos2

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.watermelon.misgastos2.LoadSharedPrefs.Companion.prefs
import com.watermelon.misgastos2.databinding.ActivityMain2Binding
import com.watermelon.misgastos2.databinding.AlertDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val database = FirebaseDatabase.getInstance("https://misgastos2-3474b-default-rtdb.europe-west1.firebasedatabase.app/")
    val listaProductos = arrayListOf<Productos>()
    private val viewModel: ProductoViewModel by lazy{
        ViewModelProvider(this)[ProductoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent=intent
        val nombre=intent.getStringExtra("nombre")
        title=nombre

        binding.rvProductos.layoutManager= LinearLayoutManager(this)
        binding.rvProductos.setHasFixedSize(true)
        getData()


        binding.btnTotal.setOnClickListener {
            binding.precioTotal.text=viewModel.suma()
        }
        binding.btnAddProducts.setOnClickListener {
            val alertdialog = AlertDialogBinding.inflate(layoutInflater)
            val dialog= AlertDialog.Builder(this)

            dialog.setTitle("Ingrese el nombre y el precio del producto")
            dialog.setView(alertdialog.root)
            dialog.setPositiveButton("añadir"){dialog,_ ->
                if (alertdialog.etNombre.text.isNotEmpty() && alertdialog.etPrecio.text.isNotEmpty()){
                    val productName=alertdialog.etNombre.text.toString()
                    val productprice=alertdialog.etPrecio.text.toString()
                    val producto=Productos(productName,productprice.toDouble())
                    database.getReference("Listas").child(nombre.toString()).child("productos").child(productName).setValue(producto).addOnSuccessListener {
                        PushNotification(NotificationData("Se ha realizado una modificacion","${prefs.getUser()} ha añadido un producto $productName con valor $productprice"), TOPIC).also {
                            sendNotification(it)
                        }
                        getData()
                        binding.precioTotal.text=viewModel.suma()
                        dialog.dismiss()
                    }
                    database.getReference("Listas").child(nombre.toString()).child("GastosTotales").child(productName).setValue(productprice).addOnSuccessListener {
                        Toast.makeText(this,"Valor ingresado", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Por favor no deje los campos en blanco", Toast.LENGTH_LONG).show()
                }
            }
            dialog.create().show()
        }

        val swipeGesture = object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT->{
                        Adapter2(listaProductos).deleteItem(viewHolder.adapterPosition+1)
                        Log.i("Mensaje","${listaProductos[viewHolder.adapterPosition].producto}")
                        database.getReference("Listas").child(nombre.toString()).child("productos").child(listaProductos[viewHolder.adapterPosition].producto!!).removeValue().addOnSuccessListener {
                            PushNotification(NotificationData("Se ha realizado una modificacion","${prefs.getUser()} ha eliminado un producto de la lista "), TOPIC).also {
                                sendNotification(it)
                            }
                            getData()
                            binding.precioTotal.text=viewModel.suma()
                        }
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvProductos)
    }

    private fun getData() {
        val intent=intent
        val nombre=intent.getStringExtra("nombre")
        database.getReference("Listas").child(nombre.toString()).child("productos").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                viewModel.listaPrecios.clear()
                for (product in snapshot.children){
                    val productos= product.getValue(Productos::class.java)
                    listaProductos.add(productos!!)
                    viewModel.add2List(productos.precio)
                }
                binding.rvProductos.adapter=Adapter2(listaProductos)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        database.getReference("Listas").child(nombre.toString()).child("GastosTotales").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModel.listaFija.clear()
                    for (valor in snapshot.children){
                        val precio = valor.value.toString().toDouble()
                        viewModel.addListaFija(precio)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.showTotalMoney){
            AlertDialog.Builder(this)
                .setTitle("Gasto mensual TOTAL")
                .setMessage("Hola ${prefs.getUser()}, en este mes has gastado ${viewModel.sumaListaFija()}")
                .create().show()
        }
        return true
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