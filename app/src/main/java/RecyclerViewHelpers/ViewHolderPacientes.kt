package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diego.jimenez.angel.hernandez.myapplication.R

class ViewHolderPacientes(view:View):RecyclerView.ViewHolder(view) {

    val lblNombrePaciente:TextView= view.findViewById(R.id.lblNombrePaciente)
    val lblEnfermedad:TextView = view.findViewById(R.id.lblEnfermedad)
    val imgBorrar:ImageView = view.findViewById(R.id.imgBorrar)
    val imgEditar:ImageView = view.findViewById(R.id.imgEditar)
}