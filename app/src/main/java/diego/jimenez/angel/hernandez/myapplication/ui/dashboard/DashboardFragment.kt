package diego.jimenez.angel.hernandez.myapplication.ui.dashboard

import Modelo.ClaseConexion
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import diego.jimenez.angel.hernandez.myapplication.R
import diego.jimenez.angel.hernandez.myapplication.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textoEnfermedad = root.findViewById<TextView>(R.id.mtEnfermedad)

        val enviarEnfermedad = root.findViewById<Button>(R.id.btnIngresarEnfermedad)

        enviarEnfermedad.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()

                   val agregarEnfermedad = objConexion?.prepareStatement("insert into Enfermedades(nombre_enfermedad) values(?)")!!
                    agregarEnfermedad.setString(1,textoEnfermedad.text.toString())
                    agregarEnfermedad.executeUpdate()
                    withContext(Dispatchers.Main){
                        textoEnfermedad.setText("")
                        Toast.makeText(requireContext(), "La enfermedad se ha agregado correctamente",Toast.LENGTH_SHORT).show()
                    }

                }
            }catch (e:Exception){
                println("El error es $e")
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}