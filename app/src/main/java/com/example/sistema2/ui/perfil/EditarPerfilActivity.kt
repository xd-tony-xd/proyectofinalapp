package com.example.sistema2.ui.perfil

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.sistema2.databinding.ActivityEditarPerfilBinding
import com.example.sistema2.models.UsuarioUpdateRequest
import com.example.sistema2.viewmodels.UsuarioState
import com.example.sistema2.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private val viewModel: UsuarioViewModel by viewModels()

    private var token: String? = null
    private var userId: Long = -1

    // URI de la imagen seleccionada de la galería
    private var selectedImageUri: Uri? = null

    // Foto que viene del backend (por si no cambia la imagen)
    private var fotoActualRemota: String? = null

    // Lanzador para abrir la galería
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri

                // Mostrar la imagen seleccionada
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivFotoEditar)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("token", null)
        userId = prefs.getLong("usuarioId", -1)

        if (token == null || userId == -1L) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos actuales
        viewModel.cargarPerfil(token!!, userId)
        observarViewModel()

        binding.btnCambiarFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.estado.collect { state ->
                when (state) {
                    is UsuarioState.Exito -> {
                        val u = state.usuario

                        // Guardamos la foto que viene del backend
                        fotoActualRemota = u.fotoPerfil

                        // Solo rellenamos si aún no hay texto (para no borrar edición)
                        if (binding.etNombre.text.isNullOrEmpty()) {
                            binding.etNombre.setText(u.nombre ?: "")
                            binding.etApellido.setText(u.apellido ?: "")
                            binding.etEmail.setText(u.email ?: "")
                            binding.etTelefono.setText(u.telefono ?: "")
                            binding.etDni.setText(u.dni ?: "")
                            binding.etDireccion.setText(u.direccion ?: "")
                            binding.etCiudad.setText(u.ciudad ?: "")
                            binding.etFechaNacimiento.setText(u.fechaNacimiento ?: "")

                            Glide.with(this@EditarPerfilActivity)
                                .load(fotoActualRemota)
                                .into(binding.ivFotoEditar)
                        }
                    }

                    is UsuarioState.Error -> {
                        Toast.makeText(
                            this@EditarPerfilActivity,
                            state.mensaje,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun guardarCambios() {
        if (token == null || userId == -1L) return

        // Si el usuario eligió foto nueva → usamos esa URI
        // Si no, mantenemos la foto que ya tenía en el backend
        val fotoPerfilString = selectedImageUri?.toString() ?: fotoActualRemota

        val datos = UsuarioUpdateRequest(
            nombre = binding.etNombre.text?.toString()?.trim() ?: "",
            apellido = binding.etApellido.text?.toString()?.trim() ?: "",
            email = binding.etEmail.text?.toString()?.trim() ?: "",
            telefono = binding.etTelefono.text?.toString()?.trim() ?: "",
            dni = binding.etDni.text?.toString()?.trim() ?: "",
            direccion = binding.etDireccion.text?.toString()?.trim() ?: "",
            ciudad = binding.etCiudad.text?.toString()?.trim() ?: "",
            fechaNacimiento = binding.etFechaNacimiento.text?.toString()?.trim() ?: "",
            fotoPerfil = fotoPerfilString
        )

        viewModel.actualizarPerfil(token!!, userId, datos)

        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
        finish()
    }
}
