package com.example.sistema2.ui.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.sistema2.R
import com.example.sistema2.databinding.FragmentPerfilBinding
import com.example.sistema2.ui.login.LoginActivity
import com.example.sistema2.ui.perfil.EditarPerfilActivity
import com.example.sistema2.ui.misproductos.MisProductosFragment // ¡Asegúrate de tener esta importación!
import com.example.sistema2.viewmodels.UsuarioState
import com.example.sistema2.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsuarioViewModel by viewModels()

    // Guardamos token e id para reutilizarlos
    private var token: String? = null
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OJO: usa las MISMAS claves que cuando guardas los datos
        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("token", null)
        userId = prefs.getLong("usuarioId", -1L)   // <-- corregido, antes tenías "user_id"

        cargarPerfil()

        // 🆕 LÓGICA AÑADIDA: Abrir Mis Productos
        binding.btnMisProductos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MisProductosFragment())
                .addToBackStack(null)
                .commit()
        }
        // ------------------------------------

        // Editar perfil
        binding.btnEditarPerfil.setOnClickListener {
            startActivity(Intent(requireContext(), EditarPerfilActivity::class.java))
        }

        // Cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            val editor = prefs.edit()
            editor.clear()          // borra token, usuarioId, etc.
            editor.apply()

            // Ir al login y limpiar el backstack
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            requireActivity().finish()
        }

        // Observar el estado del ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.estado.collect { state ->
                when (state) {
                    is UsuarioState.Exito -> {
                        val u = state.usuario

                        binding.tvNombre.text =
                            "${u.nombre ?: ""} ${u.apellido ?: ""}"

                        binding.tvEmail.text =
                            "Correo: ${u.email ?: ""}"

                        binding.tvTelefono.text =
                            "Teléfono: ${u.telefono ?: ""}"

                        binding.tvDni.text =
                            "DNI: ${u.dni ?: ""}"

                        binding.tvDireccion.text =
                            "Dirección: ${u.direccion ?: ""}"

                        binding.tvCiudad.text =
                            "Ciudad: ${u.ciudad ?: ""}"

                        binding.tvFechaNacimiento.text =
                            "Fecha nacimiento: ${formatearFecha(u.fechaNacimiento)}"

                        binding.tvFechaRegistro.text =
                            "Fecha registro: ${formatearFecha(u.fechaRegistro)}"

                        binding.tvReputacion.text =
                            "Reputación: ${u.reputacion ?: 5.0}"

                        Glide.with(requireContext())
                            .load(u.fotoPerfil)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(binding.ivPerfil)
                    }

                    is UsuarioState.Error -> {
                        // Aquí puedes mostrar un Toast o un mensaje de error en pantalla si quieres
                    }

                    else -> { /* Loading / Inicial / etc. */ }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPerfil()
    }

    private fun cargarPerfil() {
        if (token != null && userId != -1L) {
            viewModel.cargarPerfil(token!!, userId)
        }
    }

    // Formatear fecha "bonito"
    private fun formatearFecha(fecha: String?): String {
        if (fecha.isNullOrBlank()) return ""
        return try {
            val odt = OffsetDateTime.parse(fecha)
            odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            // Si no es un OffsetDateTime válido, recorta los primeros 10 caracteres (yyyy-MM-dd)
            fecha.take(10)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}