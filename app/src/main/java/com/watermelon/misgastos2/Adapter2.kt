package com.watermelon.misgastos2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.watermelon.misgastos2.databinding.ProductoViewHolderBinding

class Adapter2(val productos: ArrayList<Productos>): RecyclerView.Adapter<Adapter2.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding2=
            ProductoViewHolderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.productName.text=productos[position].producto
        holder.binding.productprice.text=productos[position].precio.toString()
    }

    override fun getItemCount(): Int {
        return productos.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int){
        productos.drop(i)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ProductoViewHolderBinding): RecyclerView.ViewHolder(binding.root)
}
