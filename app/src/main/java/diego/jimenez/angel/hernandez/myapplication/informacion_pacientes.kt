package diego.jimenez.angel.hernandez.myapplication

import Modelo.ClaseConexion
import Modelo.dataClassPacientes
import RecyclerViewHelpers.AdaptadorPacientes
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [informacion_pacientes.newInstance] factory method to
 * create an instance of this fragment.
 */
class informacion_pacientes : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_informacion_pacientes, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.haciaHome)

            }
        })

        val rcvPacientes = root.findViewById<RecyclerView>(R.id.rcvPacientes)

        rcvPacientes.layoutManager = LinearLayoutManager(requireContext())

        fun mostrarDatos(): List<dataClassPacientes> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery(
                "SELECT E.id_enfermedad AS EnfermedadID, E.nombre_enfermedad AS Enfermedad, H.id_habitacion AS HabitacionID, H.num_habitacion AS NumHabitacion, " +
                        "P.id_paciente AS id, P.nombre_paciente AS Paciente, P.tipo_sangre AS TipoSangre, P.fecha_nacimiento AS FechaNacimiento, " +
                        "P.num_cama AS Cama, P.medicamentos_asignados AS Medicamentos, P.hora_medicamentos AS HoraMedicamentos, P.telefono AS Telefono " +
                        "FROM Pacientes P " +
                        "INNER JOIN Enfermedades E ON P.id_enfermedad = E.id_enfermedad " +
                        "INNER JOIN Habitaciones H ON P.id_habitacion = H.id_habitacion"
            )!!

            val pacientes = mutableListOf<dataClassPacientes>()

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val nombrePaciente = resultSet.getString("Paciente")
                val tipoSangre = resultSet.getString("TipoSangre")
                val telefono = resultSet.getString("Telefono")
                val fechaNacimiento = resultSet.getString("FechaNacimiento")
                val idHabitacion = resultSet.getInt("HabitacionID")
                val idEnfermedad = resultSet.getInt("EnfermedadID")
                val numCama = resultSet.getString("Cama")
                val medAsignados = resultSet.getString("Medicamentos")
                val horaMed = resultSet.getString("HoraMedicamentos")
                nombreEnfermedad = resultSet.getString("Enfermedad")
                val numHabitacion = resultSet.getString("NumHabitacion")

                val paciente = dataClassPacientes(
                    id,
                    nombrePaciente,
                    tipoSangre,
                    telefono,
                    fechaNacimiento,
                    idHabitacion,
                    idEnfermedad,
                    numCama,
                    medAsignados,
                    horaMed,
                    nombreEnfermedad,
                    numHabitacion
                )

                pacientes.add(paciente)
            }

            return pacientes
        }

        CoroutineScope(Dispatchers.IO).launch {

            val pacienteDB = mostrarDatos()

            withContext(Dispatchers.Main) {
                val miAdaptador = AdaptadorPacientes(pacienteDB)
                rcvPacientes.adapter = miAdaptador
            }
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment informacion_pacientes.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            informacion_pacientes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        lateinit var nombreEnfermedad:String

    }
}