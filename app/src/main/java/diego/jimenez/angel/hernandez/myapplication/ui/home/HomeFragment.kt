package diego.jimenez.angel.hernandez.myapplication.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import diego.jimenez.angel.hernandez.myapplication.R
import diego.jimenez.angel.hernandez.myapplication.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CompletableFuture

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

        val spTipos: Spinner = root.findViewById(R.id.spTipoSangre)

        spTipos.adapter = adaptadorTipos


        val txtNum = root.findViewById<EditText>(R.id.txtNumeroTelefono)

        formatoTelefono(txtNum)


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