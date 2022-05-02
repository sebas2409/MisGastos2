package com.watermelon.misgastos2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.watermelon.misgastos2.databinding.ListasHolderBinding

class Adapter(private var c: Context, private var lista: ArrayList<Gastos>): RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding2 = ListasHolderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nombre=lista[position].nombre
        holder.binding.tvNombreLista.text=lista[position].nombre

        holder.binding.root.setOnClickListener {
            val intent= Intent(c,MainActivity2::class.java)
            intent.putExtra("nombre",nombre)
            c.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int){
        lista.drop(i)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: ListasHolderBinding): RecyclerView.ViewHolder(binding.root){

    }
}