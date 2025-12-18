package com.example.sistema2.ui.agregar

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sistema2.R
import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.databinding.FragmentAgregarBinding
import com.example.sistema2.models.Categoria
import com.example.sistema2.viewmodels.ProductoViewModel
import com.example.sistema2.viewmodels.ProductosState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AgregarFragment : Fragment(R.layout.fragment_agregar) {

    private lateinit var binding: FragmentAgregarBinding
    private val viewModel: ProductoViewModel by viewModels()
    private var imageFile: File? = null
    private var categorias: List<Categoria> = emptyList()

    // ----------- SELECTOR GALERÍA ------------
    private val pickGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.imgPreview.setImageURI(uri)
                imageFile = uriToFile(uri)
            }
        }

    // ----------- CÁMARA ----------
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                binding.imgPreview.setImageBitmap(bitmap)
                imageFile = bitmapToFile(bitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAgregarBinding.bind(view)

        binding.btnGaleria.setOnClickListener { pickGallery.launch("image/*") }
        binding.btnCamara.setOnClickListener { abrirCamara() }
        binding.btnPublicar.setOnClickListener { publicarProducto() }

        cargarCategorias()

        lifecycleScope.launch {
            viewModel.productosState.collect { state ->
                when (state) {
                    is ProductosState.ProductoCreado -> {
                        Toast.makeText(requireContext(), "Producto publicado", Toast.LENGTH_SHORT).show()
                        limpiarFormulario()
                        viewModel.resetState()
                    }

                    is ProductosState.Error -> {
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    // ---------------- ABRIR CÁMARA ----------------
    private fun abrirCamara() {
        val permiso = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permiso)
            == PackageManager.PERMISSION_GRANTED
        ) {
            takePicture.launch(null)
        } else {
            requestPermissions(arrayOf(permiso), 2001)
        }
    }

    // ---------------- CARGAR CATEGORÍAS ----------------
    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val shared = requireContext().getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE)
                val token = shared.getString("token", null)

                if (token == null) {
                    Toast.makeText(requireContext(), "Token no encontrado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerCategorias("Bearer $token")
                }

                if (response.isSuccessful) {
                    categorias = response.body() ?: emptyList()

                    val nombres = categorias.map { it.nombre }
                    val adapterSpinner = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        nombres
                    )
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerCategoria.adapter = adapterSpinner
                } else {
                    Toast.makeText(requireContext(), "Error obteniendo categorías", Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------- PUBLICAR PRODUCTO ----------------
    private fun publicarProducto() {

        val titulo = binding.edtTitulo.text.toString().trim()
        val descripcion = binding.edtDescripcion.text.toString().trim()
        val direccion = binding.edtDireccion.text.toString().trim()
        val precio = binding.edtPrecio.text.toString().toDoubleOrNull()

        if (titulo.isEmpty() || descripcion.isEmpty() || direccion.isEmpty() || precio == null) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val shared = requireContext().getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE)
        val token = shared.getString("token", null)
        val usuarioId = shared.getLong("usuarioId", -1L)

        if (token == null || usuarioId == -1L) {
            Toast.makeText(requireContext(), "Error de sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaSeleccionada = categorias[binding.spinnerCategoria.selectedItemPosition]

        // ---------- JSON CORRECTO PARA TU BACKEND ----------
        val productoJsonTexto = """
        {
          "titulo": "$titulo",
          "descripcion": "$descripcion",
          "precio": $precio,
          "stock": 1,
          "direccion": "$direccion",

          "usuario": {
              "id": $usuarioId
          },

          "categoria": {
              "id": ${categoriaSeleccionada.id}
          }
        }
        """.trimIndent()

        val productoJson = productoJsonTexto
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // ---------- IMAGEN ----------
        val bodyImagen: MultipartBody.Part? = imageFile?.let { file ->
            MultipartBody.Part.createFormData(
                "imagen",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.crearProductoMultipart(
                        "Bearer $token",
                        productoJson,
                        bodyImagen
                    )
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto publicado", Toast.LENGTH_SHORT).show()
                    limpiarFormulario()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error ${response.code()} → ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------- LIMPIAR FORMULARIO ----------------
    private fun limpiarFormulario() {
        binding.edtTitulo.setText("")
        binding.edtDescripcion.setText("")
        binding.edtPrecio.setText("")
        binding.edtDireccion.setText("")
        binding.spinnerCategoria.setSelection(0)
        binding.imgPreview.setImageResource(R.drawable.ic_profile_placeholder)
        imageFile = null
    }

    // ---------------- URI → FILE ----------------
    private fun uriToFile(uri: Uri): File {
        val input = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        FileOutputStream(file).use { output ->
            input?.copyTo(output)
        }
        return file
    }

    // ---------------- BITMAP → FILE ----------------
    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "camera_image.jpg")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        return file
    }
}
