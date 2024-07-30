package diego.jimenez.angel.hernandez.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [detalle_pacientes.newInstance] factory method to
 * create an instance of this fragment.
 */
class detalle_pacientes : Fragment() {
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


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.haciaHome)

            }
        })

        val root = inflater.inflate(R.layout.fragment_detalle_pacientes, container, false)

        val nombrePaciente = arguments?.getString("nombrePaciente")
        val tipoSangre = arguments?.getString("tipoSangre")
        val telefono = arguments?.getString("telefono")
        val fechaNacimiento = arguments?.getString("fechaNacimiento")
        val numCama = arguments?.getString("numCama")
        val medAsignados = arguments?.getString("medAsignados")
        val horaMed = arguments?.getString("horaMed")
        val nombreEnfermedad = arguments?.getString("nombreEnfermedad")
        val numHabitacion = arguments?.getString("numHabitacion")

        root.findViewById<TextView>(R.id.lblPaciente_Nombre).text = nombrePaciente
        root.findViewById<TextView>(R.id.lblTipoSangre).text = tipoSangre
        root.findViewById<TextView>(R.id.lblTelefono).text = telefono
        root.findViewById<TextView>(R.id.lblFechaDeNacimiento).text = fechaNacimiento
        root.findViewById<TextView>(R.id.lblNumeroCama).text = numCama
        root.findViewById<TextView>(R.id.lblMedicamentosAsignados).text = medAsignados
        root.findViewById<TextView>(R.id.lblHoraMedicamento).text = horaMed
        root.findViewById<TextView>(R.id.lblNombreEnfermedad).text = nombreEnfermedad
        root.findViewById<TextView>(R.id.lblNumeroHabitacion).text = numHabitacion




        return root


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment detalle_pacientes.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            detalle_pacientes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}