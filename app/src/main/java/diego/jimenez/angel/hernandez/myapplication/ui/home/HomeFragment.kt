package diego.jimenez.angel.hernandez.myapplication.ui.home

import Modelo.ClaseConexion
import Modelo.dataClassEnfermedades
import Modelo.dataClassHabitaciones
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import diego.jimenez.angel.hernandez.myapplication.R
import diego.jimenez.angel.hernandez.myapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val txtFechaNacimiento = root.findViewById<EditText>(R.id.txtFechaNacimiento)

        val lblHora = root.findViewById<TextView>(R.id.lblHoraMed)

        val listadoTipoSangre = arrayOf(
            "A+",
            "A-",
            "B+",
            "B-",
            "AB+",
            "AB-",
            "O+",
            "O-"
        )

        val adaptadorTipos = ArrayAdapter (requireContext(),android.R.layout.simple_spinner_dropdown_item, listadoTipoSangre)

        val spTipos: Spinner = root.findViewById(R.id.spSangre)

        val btnVer = root.findViewById<Button>(R.id.btnVerRegistro)



        btnVer.setOnClickListener {
            findNavController().navigate(R.id.haciaInformacion)

        }

        spTipos.adapter = adaptadorTipos


        val txtNum = root.findViewById<EditText>(R.id.txtNumeroTelefono)

        formatoTelefono(txtNum)

        val spHabitaciones = root.findViewById<Spinner>(R.id.spHabitaciones)

        val spEnfermadades = root.findViewById<Spinner>(R.id.spEnfermedad)

        val btnRegistrar = root.findViewById<Button>(R.id.btnRegistrar)

        fun obtenerEnfermadades():List<dataClassEnfermedades>{
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

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val listadoResultados = obtenerEnfermadades()
                val nombreEnfermedad = listadoResultados.map { it.nombreEnfermedad }
                withContext(Dispatchers.Main){
                    val miAdaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad )
                    spEnfermadades.adapter = miAdaptador
                }
            }
        }catch (e:Exception){
            println("El error es $e")
        }

        val txtNombre = root.findViewById<EditText>(R.id.txtNombre)

        val txtNumCama = root.findViewById<EditText>(R.id.txtNumCama)

        val txtMedicamentos = root.findViewById<EditText>(R.id.txtMedicamentosAsignados)

        val txtHora = root.findViewById<EditText>(R.id.txtHoraMedicamentos)



        fun obtenerHabitaciones():List<dataClassHabitaciones>{
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

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val listadoResultados = obtenerHabitaciones()
                val numHabitacion = listadoResultados.map { it.numHabitacion }
                withContext(Dispatchers.Main){
                    val miAdaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, numHabitacion )
                    spHabitaciones.adapter = miAdaptador
                }
            }
        }catch (e:Exception){
            println("El error es $e")
        }

        try {
            btnRegistrar.setOnClickListener {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val objConexion = ClaseConexion().cadenaConexion()

                        val habitacion = obtenerHabitaciones()

                        val enfermedad = obtenerEnfermadades()

                        val agregarPaciente = objConexion?.prepareStatement("Insert into Pacientes (nombre_paciente, tipo_sangre, telefono, fecha_nacimiento, id_habitacion, id_enfermedad, num_cama, medicamentos_asignados, hora_medicamentos) VALUES (?,?,?,?,?,?,?,?,?)")!!

                        agregarPaciente.setString(1,txtNombre.text.toString())
                        agregarPaciente.setString(2, spTipos.selectedItem.toString())
                        agregarPaciente.setString(3, txtNum.text.toString())
                        agregarPaciente.setString(4, txtFechaNacimiento.text.toString())
                        agregarPaciente.setInt(5, habitacion[spHabitaciones.selectedItemPosition].idHabitacion)
                        agregarPaciente.setInt(6, enfermedad[spEnfermadades.selectedItemPosition].idEnfermedad)
                        agregarPaciente.setString(7,txtNumCama.text.toString())
                        agregarPaciente.setString(8, txtMedicamentos.text.toString())
                        agregarPaciente.setString(9, txtHora.text.toString())

                        agregarPaciente.executeUpdate()

                        withContext(Dispatchers.Main){
                            txtHora.setText("")
                            txtNombre.setText("")
                            txtMedicamentos.setText("")
                            txtNumCama.setText("")
                            txtFechaNacimiento.setText("")
                            txtNum.setText("")


                            Toast.makeText(requireContext(), "El paciente se ha agregado correctamente",
                                Toast.LENGTH_SHORT).show()
                        }

                    }

                }catch (e:Exception) {
                    println("El error es $e")
                }
            }
        }catch (e:Exception) {
            println("El error es $e")
        }

        fun showDatePicker() {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = Date(selection)
                txtFechaNacimiento.setText(sdf.format(date))
            }

            datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        }

        txtFechaNacimiento.setOnClickListener {
           showDatePicker()
        }

        return root
    }



    fun formatoTelefono(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var cursorPosition: Int = 0
            private var beforeLength: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!isFormatting) {
                    beforeLength = s?.length ?: 0
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isFormatting) {
                    cursorPosition = editText.selectionStart
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isFormatting) {
                    isFormatting = true

                    val currentText = s.toString().replace("-", "")
                    val formattedText = StringBuilder()

                    for (i in currentText.indices) {
                        if (i == 4) formattedText.append("-")
                        formattedText.append(currentText[i])
                    }

                    editText.setText(formattedText.toString())
                    editText.setSelection(if (cursorPosition >= formattedText.length) formattedText.length else cursorPosition)

                    isFormatting = false
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}