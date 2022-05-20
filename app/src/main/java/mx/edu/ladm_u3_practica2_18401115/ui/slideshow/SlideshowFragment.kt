package mx.edu.ladm_u3_practica2_18401115.ui.slideshow

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ladm_u3_practica2_18401115.MascotaActualizar
import mx.edu.ladm_u3_practica2_18401115.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    val arregloPropietarios = ArrayList<String>()
    val arregloMascota = ArrayList<String>()
    var arregloIDsMascota = ArrayList<String>()
    var arregloIDsPropietarios = ArrayList<String>()
    var arregloCurps = ArrayList<String>()
    var arregloNombreMascota = ArrayList<String>()
    var arregloDatos = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        FirebaseFirestore.getInstance()//---------------P R O P I E T A R I O-------------------------------
            .collection("propietario")
            .addSnapshotListener { query, error ->
                arregloPropietarios.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //pasa salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "Curp: ${documento.getString("curp")}\n" +
                            " Nombre: ${documento.getString("nombre") }"
                    arregloPropietarios.add(cadena)
                    arregloIDsPropietarios.add(documento.id) //obtiene el ID de los documentos
                    arregloCurps.add(""+documento.getString("curp"))
                }

                binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloPropietarios)
                binding.listaPropietario.setOnItemClickListener { adapterView, view, posicion, l ->
                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea agregar como propietario a  ${arregloPropietarios.get(posicion)} ?")
                        .setPositiveButton("Si") {d,i ->
                            binding.txtcurp.setText(arregloCurps.get(posicion))
                        }
                        .setNeutralButton("No") {d,i -> }
                        .show()
                }
            } //fin evento snapshoot


        FirebaseFirestore.getInstance()//--------------M A S C O T A ---------------------------------------
            .collection("mascota")
            .addSnapshotListener { query, error ->
                arregloMascota.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //para salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "ID: ${documento.getString("id_mascota")}\n" +
                            "RAZA: ${documento.getString("raza")}\n"+
                            "MASCOTA: ${documento.getString("nombre")}\n"+
                            "DUEÑO: ${documento.getString("curp_propietario") }"
                    arregloMascota.add(cadena)
                    arregloNombreMascota.add(""+documento.getString("nombre"))
                    arregloIDsMascota.add(documento.id) //obtiene el ID de los documentos
                }

                binding.listaMascotas.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloMascota)
                binding.listaMascotas.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idsMascotasFB = arregloIDsMascota.get(posicion)
                    val idsPropietariosFB = arregloIDsPropietarios.get(posicion)

                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea Elimnar o Modificar a [ ${arregloNombreMascota.get(posicion)} ]?")
                        .setNegativeButton("Eliminar") {d,i ->
                            eliminar(idsMascotasFB)
                        }
                        .setPositiveButton("Actualizar") {d,i ->
                            actualizar(idsMascotasFB,idsPropietariosFB)
                        }
                        .setNeutralButton("Cerrar") {d,i -> }
                        .show()
                }//Clic Lista Mascotas
            } //-----------evento actualizacion de datos en FB -------------

        binding.insertarMascota.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            val datos= hashMapOf(
                "nombre" to binding.txtnombreMascota.text.toString(),
                "curp_propietario" to binding.txtcurp.text.toString(),
                "raza" to binding.txtRaza.text.toString(),
                "id_mascota" to (arregloNombreMascota.size+1).toString()
            )

            baseRemota.collection("mascota")
                .add(datos)
                .addOnSuccessListener { Toast.makeText(requireContext(),"Exito!, Si se Inserto correctamente",
                    Toast.LENGTH_LONG).show() } //si se pudo
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                } //no se pudo
            binding.txtRaza.setText("")
            binding.txtnombreMascota.setText("")
            binding.txtcurp.setText("")

        }//boton para intertar en BDremota

        binding.radBuscarCurp.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta = baseRemota.collection("mascota").orderBy("curp_propietario")
                    .startAt(binding.etBuscar.text.toString())
                    .endAt(binding.etBuscar.text.toString() + '\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena = ""
                        for (documento in it) {
                            cadena = "NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp_propietario")} \n" +
                                    "RAZA: ${documento.getString("raza")}\n" +
                                    "ID: ${documento.getString("id_mascota")}"
                            arregloDatos.add(cadena)
                        }

                        if (arregloDatos.size == 0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaMascotas.adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }//---c u r p


        binding.radBuscarNombre.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta = baseRemota.collection("mascota").orderBy("nombre")
                    .startAt(binding.etBuscar.text.toString())
                    .endAt(binding.etBuscar.text.toString() + '\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena = ""
                        for (documento in it) {
                            cadena = "NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp_propietario")} \n" +
                                    "RAZA: ${documento.getString("raza")}\n" +
                                    "ID: ${documento.getString("id_mascota")}"
                            arregloDatos.add(cadena)
                        }

                        if (arregloDatos.size == 0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaMascotas.adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }// -- n o m b r e


        binding.radBuscarRaza.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta = baseRemota.collection("mascota").orderBy("raza")
                    .startAt(binding.etBuscar.text.toString())
                    .endAt(binding.etBuscar.text.toString() + '\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena = ""
                        for (documento in it) {
                            cadena = "NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp_propietario")} \n" +
                                    "RAZA: ${documento.getString("raza")}\n" +
                                    "ID: ${documento.getString("id_mascota")}"
                            arregloDatos.add(cadena)
                        }

                        if (arregloDatos.size == 0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaMascotas.adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }//-- r a z a



        return root
    }

    private fun eliminar(idEliminar:String) {
        val baseRemota = FirebaseFirestore.getInstance()
        alerta("mensaje","El id que se elimino fue: ${idEliminar}")
        baseRemota.collection("propietario")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                toast("Se Elimino Correctamente")
            }
            .addOnFailureListener{
                alerta("Error","Hubo ERROR: ${it.message!!}")
            }
    }

    private fun actualizar(idActualizar:String,idActualizarPropietario:String) {
        var intent= Intent(requireContext(), MascotaActualizar::class.java)
        intent.putExtra("idActualizar",idActualizar)
        intent.putExtra("idActualizarPropietario",idActualizarPropietario)
        startActivity(intent)
    }



    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()

    }

    private fun toast(mensaje:String){
        Toast.makeText(requireContext(),mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}