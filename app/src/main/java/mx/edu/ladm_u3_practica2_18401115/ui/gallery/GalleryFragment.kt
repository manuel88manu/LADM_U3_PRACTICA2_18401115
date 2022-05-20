package mx.edu.ladm_u3_practica2_18401115.ui.gallery

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
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ladm_u3_practica2_18401115.PropietarioActualizar
import mx.edu.ladm_u3_practica2_18401115.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    val arregloDatos = ArrayList<String>()
    var arregloIDs = ArrayList<String>()
    var arregloNombres = ArrayList<String>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        FirebaseFirestore.getInstance()
            .collection("propietario")
            .addSnapshotListener { query, error ->
                arregloDatos.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //pasa salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "Curp: ${documento.getString("curp")}\n" +
                            "Nombre: ${documento.getString("nombre") }\n"+
                            "Edad: ${documento.getString("edad") +" años" }\n"+
                            "Telefono: ${documento.getString("telefono") }"
                    arregloDatos.add(cadena)
                    arregloNombres.add(""+documento.getString("nombre"))
                    arregloIDs.add(documento.id) //obtiene el ID de los documentos

                }

                if(arregloDatos.size==0)
                    arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloDatos)

                binding.listaPropietario.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idsFB = arregloIDs.get(posicion)
                    val nombresFB = arregloNombres.get(posicion)
                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea Elimnar o Modificar a  ${nombresFB} ?")
                        .setNegativeButton("Eliminar") {d,i ->
                            eliminarMascota(idsFB)
                        }
                        .setPositiveButton("Actualizar") {d,i ->
                            actualizar(idsFB)
                        }
                        .setNeutralButton("Cerrar") {d,i -> }
                        .show()

                }

            } //---------------------------------------------------------------------------------

        binding.insertar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            val datos= hashMapOf(
                "nombre" to binding.etNombre.text.toString(),
                "curp" to binding.etCurp.text.toString(),
                "edad" to binding.etEdad.text.toString(),
                "telefono" to binding.etTelefono.text.toString()
            )

            baseRemota.collection("propietario")
                .add(datos)
                .addOnSuccessListener { Toast.makeText(requireContext(),"Exito!, Si se Inserto correctamente",
                    Toast.LENGTH_LONG).show() } //si se pudo
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                } //no se pudo
            binding.etNombre.setText("")
            binding.etEdad.setText("")
            binding.etCurp.setText("")
            binding.etTelefono.setText("")

        }//boton para intertar en BDremota

        binding.radBuscar.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta = baseRemota.collection("propietario").orderBy("curp")
                    .startAt(binding.etBuscar.text.toString())
                    .endAt(binding.etBuscar.text.toString() + '\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena = ""
                        for (documento in it) {
                            cadena = "NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp")} \n" +
                                    "EDAD: ${documento.getString("edad")}\n" +
                                    "telefono: ${documento.getString("telefono")}"
                            arregloDatos.add(cadena)
                        }

                        if (arregloDatos.size == 0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaPropietario.adapter =
                            ArrayAdapter(requireContext(), R.layout.simple_list_item_1, arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }


        binding.radBuscarNombre.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta=baseRemota.collection("propietario").orderBy("nombre").startAt( binding.etBuscar.text.toString()).endAt( binding.etBuscar.text.toString()+'\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena=""
                        for (documento in it) {
                            cadena ="NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp")} \n" +
                                    "EDAD: ${documento.getString("edad")}\n" +
                                    "telefono: ${documento.getString("telefono")}"
                            arregloDatos.add(cadena)
                        }

                        if(arregloDatos.size==0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }// ------nombre


        binding.radBuscarTelefono.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta=baseRemota.collection("propietario").orderBy("telefono").startAt( binding.etBuscar.text.toString()).endAt( binding.etBuscar.text.toString()+'\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena=""
                        for (documento in it) {
                            cadena ="NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp")} \n" +
                                    "EDAD: ${documento.getString("edad")}\n" +
                                    "telefono: ${documento.getString("telefono")}"
                            arregloDatos.add(cadena)
                        }
                        if(arregloDatos.size==0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloDatos)


                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }//----telefono


        binding.radBuscarEdad.setOnClickListener {
            if(binding.etBuscar.text.toString()==""){
                toast("Pon una cadena para buscar...")
            }else{
                val baseRemota = FirebaseFirestore.getInstance()
                // var consulta= baseRemota.collection("propietario").whereEqualTo("nombre", binding.etBuscar.text.toString())
                var consulta=baseRemota.collection("propietario").orderBy("edad").startAt( binding.etBuscar.text.toString()).endAt( binding.etBuscar.text.toString()+'\uf8ff')
                consulta.get()
                    .addOnSuccessListener {
                        arregloDatos.clear()
                        var cadena=""
                        for (documento in it) {
                            cadena ="NOMBRE: ${documento.getString("nombre")} \n" +
                                    "CURP: ${documento.getString("curp")} \n" +
                                    "EDAD: ${documento.getString("edad")}\n" +
                                    "telefono: ${documento.getString("telefono")}"
                            arregloDatos.add(cadena)
                        }

                        if(arregloDatos.size==0)
                            arregloDatos.add("> NO SE ENCONTRARON DATOS <")
                        binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloDatos)
                        return@addOnSuccessListener
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
            }
        }//-----Edad

        return root

    }
    private fun toast(mensaje:String){
        Toast.makeText(requireContext(),mensaje,Toast.LENGTH_LONG).show()
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
    private fun eliminarMascota(idEliminar:String) {
        val baseRemota = FirebaseFirestore.getInstance()
        alerta("mensaje","El id que se elimino fue: ${idEliminar}")
        baseRemota.collection("mascota")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                toast("Se Elimino Correctamente")
            }
            .addOnFailureListener{
                alerta("Error","Hubo ERROR: ${it.message!!}")
            }
    }

    private fun actualizar(idActualizar:String) {
        var intent= Intent(requireContext(),PropietarioActualizar::class.java)
        intent.putExtra("idActualizar",idActualizar)
        startActivity(intent)
    }



    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}