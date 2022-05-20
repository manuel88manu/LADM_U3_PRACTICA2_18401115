package mx.edu.ladm_u3_practica2_18401115

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.ladm_u3_practica2_18401115.databinding.ActivityPropietarioActualizarBinding

class PropietarioActualizar : AppCompatActivity() {
    var idActualizar=""
    lateinit var binding: ActivityPropietarioActualizarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPropietarioActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Actualizar Propietario")

        idActualizar=intent.extras!!.getString("idActualizar")!!

        val baseRemota= FirebaseFirestore.getInstance()
        baseRemota.collection("propietario")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                binding.txtcurp.setText(it.getString("curp"))
                binding.txtnombrePropietario.setText(it.getString("nombre"))
                binding.txtedadPropietario.setText(it.getString("edad")) // Numerico
                binding.txttelefono.setText(it.getString("telefono"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }

        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnActualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("propietario")
                .document(idActualizar)
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
                    binding.txttelefono.text.clear()
                    binding.txtedadPropietario.text.clear()
                    binding.txtnombrePropietario.text.clear()
                    binding.txtcurp.text.clear()
                    finish()
                }
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
