package com.watermelon.misgastos2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.watermelon.misgastos2.databinding.ActivityMain2Binding
import com.watermelon.misgastos2.databinding.AlertDialogBinding

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
                val productName=alertdialog.etNombre.text.toString()
                val productprice=alertdialog.etPrecio.text.toString()
                val producto=Productos(productName,productprice.toDouble())
                database.getReference("Listas").child(nombre.toString()).child("productos").child(productName).setValue(producto).addOnSuccessListener {
                    getData()
                    binding.precioTotal.text=viewModel.suma()
                    dialog.dismiss()
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
                    Log.i("precio","${productos.precio}")
                    Log.i("lista","${viewModel.listaPrecios}")
                }
                binding.rvProductos.adapter=Adapter2(listaProductos)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}