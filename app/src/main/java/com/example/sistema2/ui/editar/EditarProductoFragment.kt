package com.example.sistema2.ui.editar

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
import androidx.lifecycle.lifecycleScope
import com.example.sistema2.R
import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.databinding.FragmentEditarProductoBinding
import com.example.sistema2.models.Categoria
import com.example.sistema2.models.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditarProductoFragment : Fragment(R.layout.fragment_editar_producto) {

    private var _binding: FragmentEditarProductoBinding? = null
    private val binding get() = _binding!!

    private var categorias: List<Categoria> = emptyList()
    private var productoId: Long = -1L
    private var imageFile: File? = null
    private var productoActual: Producto? = null

    private val pickGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.imgPreview.setImageURI(uri)
                imageFile = uriToFile(uri)
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                binding.imgPreview.setImageBitmap(bitmap)
                imageFile = bitmapToFile(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productoId = it.getLong("productoId", -1L)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditarProductoBinding.bind(view)

        binding.btnGaleria.setOnClickListener { pickGallery.launch("image/*") }
        binding.btnCamara.setOnClickListener { abrirCamara() }
        binding.btnGuardar.setOnClickListener { guardarCambios() }

        cargarCategoriasYProducto()
    }

    private fun abrirCamara() {
        val permiso = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permiso) == PackageManager.PERMISSION_GRANTED) {
            takePicture.launch(null)
        } else {
            requestPermissions(arrayOf(permiso), 2001)
        }
    }

    private fun cargarCategoriasYProducto() {
        lifecycleScope.launch {
            try {
                val token = obtenerToken()

                // CATEGORÍAS
                val catResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerCategorias("Bearer $token")
                }

                if (catResponse.isSuccessful) {
                    categorias = catResponse.body() ?: emptyList()

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        categorias.map { it.nombre }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerCategoria.adapter = adapter
                }

                // PRODUCTO
                val prodResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerProductoPorId("Bearer $token", productoId)
                }

                if (prodResponse.isSuccessful) {
                    productoActual = prodResponse.body()
                    productoActual?.let { p ->
                        binding.edtTitulo.setText(p.titulo)
                        binding.edtDescripcion.setText(p.descripcion)
                        binding.edtPrecio.setText(p.precio.toString())
                        binding.edtDireccion.setText(p.direccion ?: "")

                        p.categoria?.let { cat ->
                            val index = categorias.indexOfFirst { it.id == cat.id }
                            if (index >= 0) binding.spinnerCategoria.setSelection(index)
                        }
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        val titulo = binding.edtTitulo.text.toString().trim()
        val descripcion = binding.edtDescripcion.text.toString().trim()
        val precio = binding.edtPrecio.text.toString().toDoubleOrNull()
        val direccion = binding.edtDireccion.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty() || precio == null || direccion.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val shared = requireContext().getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE)
        val token = shared.getString("token", null) ?: return
        val usuarioId = shared.getLong("usuarioId", -1L)
        val categoriaSeleccionada = categorias[binding.spinnerCategoria.selectedItemPosition]

        // JSON DEL PRODUCTO
        val jsonProducto = """
            {
              "titulo": "$titulo",
              "descripcion": "$descripcion",
              "precio": $precio,
              "direccion": "$direccion",
              "usuario": { "id": $usuarioId },
              "categoria": { "id": ${categoriaSeleccionada.id} }
            }
        """.trimIndent()

        val productoBody: RequestBody =
            jsonProducto.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // IMAGEN OPCIONAL
        val imagenPart: MultipartBody.Part? = imageFile?.let { file ->
            MultipartBody.Part.createFormData(
                "imagen",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarProductoMultipart(
                        "Bearer $token",
                        productoId,
                        productoBody,
                        imagenPart
                    )
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error ${response.code()} → ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun obtenerToken(): String {
        val prefs = requireContext().getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }

    private fun uriToFile(uri: Uri): File {
        val input = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_image_edit.jpg")
        FileOutputStream(file).use { output -> input?.copyTo(output) }
        return file
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "camera_image_edit.jpg")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
