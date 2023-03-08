package br.com.example.listadecompras.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import br.com.example.listadecompras.R
import br.com.example.listadecompras.constantes.Constantes
import br.com.example.listadecompras.databinding.ActivityMainBinding
import br.com.example.listadecompras.model.item.Item
import br.com.example.listadecompras.repository.ItemRepository
import br.com.example.listadecompras.ui.cart.CartActivity
import br.com.example.listadecompras.ui.main.MainFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val repo by inject<ItemRepository>()

    private val cartIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    navController.navigate(R.id.MainFragment)

                    val item: Item? = result.data?.getParcelableExtra("item")
                    item?.category?.let {
                        navController.navigate(
                            MainFragmentDirections.actionMainFragmentToFormularioFragmentWithItem(
                                it, item
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        configuraClickShoppingCart()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun configuraClickShoppingCart() {
        binding.fabShoppingCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                val array = arrayListOf<Item>()
                array.addAll(repo.getAllItems())



                withContext(Dispatchers.Main) {
                    val bundle = Bundle().apply {
                        putParcelableArrayList(Constantes.CART_BUNDLE_ITENS, array)
                    }

                    intent.putExtras(bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    cartIntentLauncher.launch(intent)
                }

            }


        }
    }
}