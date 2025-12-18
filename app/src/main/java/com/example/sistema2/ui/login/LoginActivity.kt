package com.example.sistema2.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.sistema2.databinding.ActivityLoginBinding
import com.example.sistema2.ui.register.RegisterActivity  // ← IMPORTANTE: Agregar este import
import com.example.sistema2.viewmodels.LoginViewModel
import com.example.sistema2.viewmodels.LoginState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        // Validar campos en tiempo real
        binding.etEmail.addTextChangedListener { validateFields() }
        binding.etPassword.addTextChangedListener { validateFields() }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        // ✅ AGREGAR ESTO: Ir a registro
        binding.tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        hideLoading()
                        hideError()
                    }
                    is LoginState.Loading -> {
                        showLoading()
                        hideError()
                    }
                    is LoginState.Success -> {
                        hideLoading()
                        hideError()
                        onLoginSuccess(state.token, state.usuario)
                    }
                    is LoginState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun validateFields() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val isValid = email.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 4

        binding.btnLogin.isEnabled = isValid
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.btnLogin.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        validateFields()
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.isVisible = true
    }

    private fun hideError() {
        binding.tvError.isVisible = false
    }

    private fun onLoginSuccess(token: String, usuario: com.example.sistema2.models.Usuario) {
        // Guardar token y datos de usuario
        saveAuthData(token, usuario)

        // Mostrar mensaje de éxito
        Toast.makeText(this, "¡Bienvenido ${usuario.nombre}!", Toast.LENGTH_SHORT).show()

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

            // 🔴 ANTES:
            // putLong("user_id", usuario.id ?: 0)

            // ✅ AHORA: usar la MISMA clave que en PerfilFragment
            putLong("usuarioId", usuario.id ?: -1L)

            apply()
        }
    }

}