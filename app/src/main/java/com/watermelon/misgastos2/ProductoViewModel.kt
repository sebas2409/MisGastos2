package com.watermelon.misgastos2

import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class ProductoViewModel: ViewModel() {

    val listaPrecios = ArrayList<Double?>()
    val listaFija = ArrayList<Double?>()

    fun add2List(precio:Double?){
        listaPrecios.add(precio)
    }
    fun addListaFija(precio: Double?){
        listaFija.add(precio)
    }

    fun suma(): String{
        var total=0.0
        for (i in listaPrecios){
            if (i != null) {
                total += i
            }
        }
        return "${(total *100).roundToInt()/100.00}"
    }
    fun sumaListaFija(): String{
        var total=0.0
        for (i in listaFija){
            if (i != null) {
                total += i
            }
        }
        return "${(total *100).roundToInt()/100.00}"
    }

}