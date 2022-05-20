package mx.edu.ladm_u3_practica2_18401115

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ladm_u3_practica2_18401115.databinding.ActivityMascotaActualizarBinding


class MascotaActualizar : AppCompatActivity() {
    var idActualizar=""
    var idActualizarPropietario=""
    lateinit var binding: ActivityMascotaActualizarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMascotaActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle("Actualizar Mascota")

        idActualizar=intent.extras!!.getString("idActualizar")!!
        idActualizarPropietario=intent.extras!!.getString("idActualizarPropietario")!!

        val baseRemota= FirebaseFirestore.getInstance()// ----- M A S C O T A  (EVENTO)---------------
        baseRemota.collection("mascota")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                binding.etCurpPropietario.setText(it.getString("curp_propietario"))
                binding.etID.setText(it.getString("id_mascota"))
                binding.etNombre.setText(it.getString("nombre")) // Numerico
                binding.etRaza.setText(it.getString("raza"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }//--------------------------------------------------------------------------------


        baseRemota.collection("propietario")// ----- P R O P I E T A R I O  (EVENTO)---------------
            .document(idActualizarPropietario)
            .get()
            .addOnSuccessListener {
                binding.txtcurp.setText(it.getString("curp"))
                binding.txtedadPropietario.setText(it.getString("edad"))
                binding.txtnombrePropietario.setText(it.getString("nombre")) // Numerico
                binding.txttelefono.setText(it.getString("telefono"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }//--------------------------------------------------------------------------------

        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnActualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("propietario")//---------PROPIETARIO (BOTON)--------------------------
                .document(idActualizarPropietario)
                .update("nombre",binding.txtnombrePropietario.text.toString(),
                    "curp",binding.txtcurp.text.toString(),
                    "edad",binding.txtedadPropietario.text.toString(),
                    "telefono",binding.txttelefono.text.toString()
                )
                .addOnFailureListener() {
                    AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(it.message)
                        .show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this,"Se actualizo Correctamente", Toast.LENGTH_LONG).show()
                    binding.txtcurp.text.clear()
                    binding.txttelefono.text.clear()
                    binding.txtedadPropietario.text.clear()
                    binding.txtnombrePropietario.text.clear()
                    finish()
                }//--------------------------

            baseRemota.collection("mascota")//---------MASCOTA (BOTON)---------------------------
                .document(idActualizar)
                .update("nombre",binding.etNombre.text.toString(),
                    "curp_propietario",binding.txtcurp.text.toString(),
                    "raza",binding.etRaza.text.toString(),
                    "id_mascota",binding.etID.text.toString()
                )
                .addOnFailureListener() {
                    AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(it.message)
                        .show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this,"Se actualizo Correctamente", Toast.LENGTH_LONG).show()
                    binding.etID.text.clear()
                    binding.etCurpPropietario.text.clear()
                    binding.etNombre.text.clear()
                    binding.etRaza.text.clear()
                    finish()
                }//--MASCOTA---
        }

    }

    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()

    }

    private fun toast(mensaje:String){
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }
}
