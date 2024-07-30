package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.dataClassEnfermedades
import Modelo.dataClassHabitaciones
import Modelo.dataClassPacientes
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import diego.jimenez.angel.hernandez.myapplication.R
import diego.jimenez.angel.hernandez.myapplication.informacion_pacientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdaptadorPacientes(var Datos:List<dataClassPacientes>): RecyclerView.Adapter<ViewHolderPacientes>() {

    fun actualizarListado(nuevaLista: List<dataClassPacientes>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }
    fun actualizarListadoPostEdicion(id: Int, nuevoNombre: String, nuevaEnfermedad: String) {
        val index = Datos.indexOfFirst { it.idPaciente == id }
        if (index != -1) {
            Datos[index].nombrePaciente = nuevoNombre
            Datos[index].nombreEnfermedad = nuevaEnfermedad
            notifyItemChanged(index)
        }
    }


    fun eliminarDatos(nombrePaciente: String, position: Int) {

        val listaDatos = Datos.toMutableList()

        listaDatos.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {

            val objConexion = ClaseConexion().cadenaConexion()

            val deleteTicket =
                objConexion?.prepareStatement("DELETE Pacientes WHERE nombre_paciente = ?")!!

            deleteTicket.setString(1, nombrePaciente )
            deleteTicket.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()


    }

    fun actualizarPaciente(paciente: dataClassPacientes): Boolean {
        val objConexion = ClaseConexion().cadenaConexion() ?: run {
            println("Error: La conexión a la base de datos es nula")
            return false
        }

        val updatePaciente = objConexion.prepareStatement(
            "UPDATE Pacientes SET " +
                    "nombre_paciente = ?, " +
                    "tipo_sangre = ?, " +
                    "telefono = ?, " +
                    "fecha_nacimiento = ?, " +
                    "id_habitacion = ?, " +
                    "id_enfermedad = ?, " +
                    "num_cama = ?, " +
                    "medicamentos_asignados = ?, " +
                    "hora_medicamentos = ? " +
                    "WHERE id_paciente = ?"
        ) ?: run {
            println("Error: El PreparedStatement es nulo")
            return false
        }

        try {
            updatePaciente.setString(1, paciente.nombrePaciente)
            updatePaciente.setString(2, paciente.tipoSangre)
            updatePaciente.setString(3, paciente.telefono)
            updatePaciente.setString(4, paciente.fechaNacimiento)
            updatePaciente.setInt(5, paciente.idHabitacion)
            updatePaciente.setInt(6, paciente.idEnfermedad)
            updatePaciente.setString(7, paciente.numCama)
            updatePaciente.setString(8, paciente.medAsignados)
            updatePaciente.setString(9, paciente.horaMed)
            updatePaciente.setInt(10, paciente.idPaciente)

            val result = updatePaciente.executeQuery()

            return result.next()
        } catch (e: Exception) {
            println("El error es $e")
            return false
        } finally {
            updatePaciente.close()
        }
    }







    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPacientes {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_pacientes, parent, false)

        return ViewHolderPacientes(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderPacientes, position: Int) {
        val pacientes = Datos[position]
        holder.lblNombrePaciente.text = pacientes.nombrePaciente
        holder.lblEnfermedad.text = pacientes.nombreEnfermedad

        holder.imgBorrar.setOnClickListener {

            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Desea eliminar el paciente?")

            builder.setPositiveButton("Si") { dialog, which ->

                eliminarDatos(pacientes.nombrePaciente, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()

            dialog.show()
        }

        holder.imgEditar.setOnClickListener {
            alertDialogActualizar(holder.itemView.context, pacientes)

        }

    }

    private fun alertDialogActualizar(context: Context, paciente: dataClassPacientes) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alertdialogupdate, null)

        val txtNombre: EditText = dialogView.findViewById(R.id.txtNombre)
        val spSangre: Spinner = dialogView.findViewById(R.id.spSangre)
        val spEnfermedades: Spinner = dialogView.findViewById(R.id.spEnfermedades)
        val spHabitaciones: Spinner = dialogView.findViewById(R.id.spHabitaciones)
        val txtTelefono: EditText = dialogView.findViewById(R.id.txtTelefonoNew)
        val txtFechaNacimiento: EditText = dialogView.findViewById(R.id.txtFechaNacimientoNew)
        val txtNumCama: EditText = dialogView.findViewById(R.id.txtNumCamaNew)
        val txtMedAsignadas: EditText = dialogView.findViewById(R.id.txtMedAsignadasNew)
        val txtHora: EditText = dialogView.findViewById(R.id.txtHoraMedNew)

        val tiposSangre = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        spSangre.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, tiposSangre)

        CoroutineScope(Dispatchers.IO).launch {
            val enfermedades = obtenerEnfermedades()
            val habitaciones = obtenerHabitaciones()

            val nombreEnfermedad = enfermedades.map { it.nombreEnfermedad }
            val numHabitacion = habitaciones.map { it.numHabitacion }

            withContext(Dispatchers.Main) {
                spEnfermedades.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad)
                spHabitaciones.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, numHabitacion)
            }
        }

        txtNombre.setText(paciente.nombrePaciente)
        txtTelefono.setText(paciente.telefono)
        txtFechaNacimiento.setText(paciente.fechaNacimiento)
        txtNumCama.setText(paciente.numCama)
        txtMedAsignadas.setText(paciente.medAsignados)
        txtHora.setText(paciente.horaMed)

        spSangre.setSelection(tiposSangre.indexOf(paciente.tipoSangre))

        MaterialAlertDialogBuilder(context)
            .setTitle("Actualizar Paciente")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { dialog, which ->
                val nombrePaciente = txtNombre.text.toString()
                val tipoSangre = spSangre.selectedItem.toString()
                val nuevaEnfermedad = spEnfermedades.selectedItem.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    val enfermedades = obtenerEnfermedades()
                    val habitaciones = obtenerHabitaciones()

                    val idEnfermedad = if (enfermedades.isNotEmpty()) {
                        enfermedades[spEnfermedades.selectedItemPosition].idEnfermedad
                    } else {
                        -1
                    }
                    val idHabitacion = if (habitaciones.isNotEmpty()) {
                        habitaciones[spHabitaciones.selectedItemPosition].idHabitacion
                    } else {
                        -1
                    }

                    val telefono = txtTelefono.text.toString()
                    val fechaNacimiento = txtFechaNacimiento.text.toString()
                    val numCama = txtNumCama.text.toString()
                    val medAsignados = txtMedAsignadas.text.toString()
                    val horaMed = txtHora.text.toString()

                    val pacienteActualizado = dataClassPacientes(
                        idPaciente = paciente.idPaciente,
                        nombrePaciente = nombrePaciente,
                        tipoSangre = tipoSangre,
                        telefono = telefono,
                        fechaNacimiento = fechaNacimiento,
                        idHabitacion = idHabitacion,
                        idEnfermedad = idEnfermedad,
                        numCama = numCama,
                        medAsignados = medAsignados,
                        horaMed = horaMed,
                        nombreEnfermedad = nuevaEnfermedad,
                        numHabitacion = paciente.numHabitacion
                    )

                    val exito = actualizarPaciente(pacienteActualizado)
                    withContext(Dispatchers.Main) {
                        if (exito) {
                            actualizarListadoPostEdicion(paciente.idPaciente, nombrePaciente, nuevaEnfermedad)
                        } else {
                            println("Error al actualizar el paciente")
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }



    fun obtenerHabitaciones():List<dataClassHabitaciones> {
        try {
            val objConexion = ClaseConexion().cadenaConexion()

            val statment = objConexion?.createStatement()

            val resultSet = statment?.executeQuery("select * from Habitaciones")!!

            val listadoDeResultados = mutableListOf<dataClassHabitaciones>()

            while (resultSet.next()){
                val idHabitacion = resultSet.getInt("id_habitacion")

                val numHabitacion = resultSet.getString("num_habitacion")

                val habitacionCompleta = dataClassHabitaciones(idHabitacion, numHabitacion)

                listadoDeResultados.add(habitacionCompleta)


            }
            return listadoDeResultados
        }catch (e:Exception) {

            println("El error es $e")
            return emptyList()
        }
    }

    fun obtenerEnfermedades():List<dataClassEnfermedades> {
        try {
            val objConexion = ClaseConexion().cadenaConexion()

            val statment = objConexion?.createStatement()

            val resultSet = statment?.executeQuery("select * from Enfermedades")!!

            val listadoDeResultados = mutableListOf<dataClassEnfermedades>()

            while (resultSet.next()){
                val idEnfermedad = resultSet.getInt("id_enfermedad")

                val nombreEnfermedad = resultSet.getString("nombre_enfermedad")

                val enfermedadCompleta = dataClassEnfermedades(idEnfermedad, nombreEnfermedad)

                listadoDeResultados.add(enfermedadCompleta)


            }
            return listadoDeResultados
        }catch (e:Exception) {

            println("El error es $e")
            return emptyList()
        }
    }



}