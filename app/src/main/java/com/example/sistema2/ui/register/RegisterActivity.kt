package com.example.sistema2.ui.register

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.sistema2.databinding.ActivityRegisterBinding
import com.example.sistema2.ui.login.LoginActivity
import com.example.sistema2.viewmodels.LoginViewModel
import com.example.sistema2.viewmodels.RegisterState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: LoginViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedDate: String? = null

    // Contract para seleccionar imagen de la galería
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivProfile.setImageURI(it)
        }
    }

    // DatePicker
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        // Seleccionar foto
        binding.btnSelectPhoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Seleccionar fecha de nacimiento
        binding.etFechaNacimiento.setOnClickListener {
            showDatePickerDialog()
        }

        // Registrar usuario
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Ir a login
        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                selectedDate = dateFormatter.format(selectedCalendar.time)
                binding.etFechaNacimiento.setText(selectedDate)
            },
            year, month, day
        )

        // Establecer fecha máxima (hoy)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Establecer fecha mínima (por ejemplo, 100 años atrás)
        val minCalendar = Calendar.getInstance()
        minCalendar.set(Calendar.YEAR, year - 100)
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

        datePickerDialog.show()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterState.Idle -> {
                        hideLoading()
                        hideError()
                    }
                    is RegisterState.Loading -> {
                        showLoading()
                        hideError()
                    }
                    is RegisterState.Success -> {
                        hideLoading()
                        hideError()
                        onRegisterSuccess(state.token, state.usuario)
                    }
                    is RegisterState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun registerUser() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val ciudad = binding.etCiudad.text.toString().trim()

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Por favor completa los campos obligatorios")
            return
        }

        if (password.length < 4) {
            showError("La contraseña debe tener al menos 4 caracteres")
            return
        }

        viewModel.register(
            nombre = nombre,
            apellido = apellido,
            email = email,
            password = password,
            telefono = if (telefono.isEmpty()) null else telefono,
            dni = if (dni.isEmpty()) null else dni,
            direccion = if (direccion.isEmpty()) null else direccion,
            ciudad = if (ciudad.isEmpty()) null else ciudad,
            fechaNacimiento = selectedDate, // ← AGREGADO
            fotoPerfil = selectedImageUri?.toString()
        )
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.btnRegister.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        binding.btnRegister.isEnabled = true
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.isVisible = true
    }

    private fun hideError() {
        binding.tvError.isVisible = false
    }

    private fun onRegisterSuccess(token: String, usuario: com.example.sistema2.models.Usuario) {
        // Guardar datos de autenticación
        saveAuthData(token, usuario)

        // Mostrar mensaje de éxito
        Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()

        // Navegar a la pantalla principal
        val intent = Intent(this, com.example.sistema2.MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveAuthData(token: String, usuario: com.example.sistema2.models.Usuario) {
        val sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("token", token)
            putString("user_email", usuario.email)
            putString("user_name", "${usuario.nombre} ${usuario.apellido}")
            putLong("user_id", usuario.id ?: 0)
            apply()
        }
    }
}