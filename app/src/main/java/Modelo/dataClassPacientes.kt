package Modelo

data class dataClassPacientes(
    val idPaciente: Int,
    var nombrePaciente: String,
    var tipoSangre: String,
    var telefono: String,
    var fechaNacimiento: String,
    var idHabitacion: Int,
    var idEnfermedad: Int,
    var numCama: String,
    var medAsignados: String,
    var horaMed: String,
    var nombreEnfermedad: String,
    var numHabitacion: String
)

