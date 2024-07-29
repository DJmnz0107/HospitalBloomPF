package diego.jimenez.angel.hernandez.myapplication.ui.notifications

import Modelo.ClaseConexion
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import diego.jimenez.angel.hernandez.myapplication.R
import diego.jimenez.angel.hernandez.myapplication.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textoHabitacion = root.findViewById<TextView>(R.id.mtHabitacion)
        val enviarHabitacion = root.findViewById<Button>(R.id.btnIngresarHabitacion)

        enviarHabitacion.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                     val objConexion = ClaseConexion().cadenaConexion()

                    val agregarHabitacion = objConexion?.prepareStatement("insert into Habitaciones(num_habitacion) values(?) ")!!
                    agregarHabitacion.setString(1,textoHabitacion.text.toString())
                    agregarHabitacion.executeUpdate()
                    withContext(Dispatchers.Main){
                        textoHabitacion.setText("")
                        Toast.makeText(requireContext(), "La habitacion se ha agregado correctamente", Toast.LENGTH_SHORT).show()

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