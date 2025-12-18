package com.example.sistema2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sistema2.databinding.ActivityMainBinding
import com.example.sistema2.ui.agregar.AgregarFragment
import com.example.sistema2.ui.categorias.CategoriaFragment
import com.example.sistema2.ui.favoritos.FavoritosFragment
import com.example.sistema2.ui.perfil.PerfilFragment
import com.example.sistema2.ui.productos.ProductoFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ⬇️ Comprobar si venimos desde DetalleProductoActivity
        val destino = intent.getStringExtra("destino")

        if (destino != null) {
            when (destino) {
                "productos" -> replaceFragment(ProductoFragment())
                "categorias" -> replaceFragment(CategoriaFragment())
                "agregar" -> replaceFragment(AgregarFragment())
                "favoritos" -> replaceFragment(FavoritosFragment())
                "perfil" -> replaceFragment(PerfilFragment())
            }

            marcarSeleccion(destino)
        } else {
            // Cuando abre la app normalmente → carga Productos
            replaceFragment(ProductoFragment())
        }

        // ⬇️ Listener normal del BottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_productos -> {
                    replaceFragment(ProductoFragment())
                    true
                }

                R.id.nav_categorias -> {
                    replaceFragment(CategoriaFragment())
                    true
                }

                R.id.nav_agregar -> {
                    replaceFragment(AgregarFragment())
                    true
                }

                R.id.nav_favoritos -> {
                    replaceFragment(FavoritosFragment())
                    true
                }

                R.id.nav_perfil -> {
                    replaceFragment(PerfilFragment())
                    true
                }

                else -> false
            }
        }
    }

    // ⬇️ Función que reemplaza fragments
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ⬇️ Marca el ícono del menú según lo recibido desde DetalleProductoActivity
    private fun marcarSeleccion(destino: String) {
        when (destino) {
            "productos" -> binding.bottomNavigation.selectedItemId = R.id.nav_productos
            "categorias" -> binding.bottomNavigation.selectedItemId = R.id.nav_categorias
            "agregar" -> binding.bottomNavigation.selectedItemId = R.id.nav_agregar
            "favoritos" -> binding.bottomNavigation.selectedItemId = R.id.nav_favoritos
            "perfil" -> binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        }
    }
}
