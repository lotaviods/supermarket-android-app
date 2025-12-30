package br.com.lotaviods.listadecompras.ui

import android.app.AlertDialog
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lotaviods.listadecompras.BuildConfig
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constantes
import br.com.lotaviods.listadecompras.databinding.ActivityMainBinding
import br.com.lotaviods.listadecompras.databinding.DialogListManagementBinding
import br.com.lotaviods.listadecompras.helper.LanguageHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.ui.cart.CartActivity
import br.com.lotaviods.listadecompras.ui.list.ShoppingListAdapter
import br.com.lotaviods.listadecompras.ui.main.MainFragmentDirections
import com.google.android.material.color.MaterialColors
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
    private val cartRepo by inject<CartRepository>()

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
        LanguageHelper.applyLanguage(this)
        
        enableEdgeToEdge()

        val statusBarColor = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, statusBarColor, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        configuraClickShoppingCart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manage_lists -> {
                showListManagementDialog()
                true
            }
            R.id.action_language -> {
                showLanguageDialog()
                true
            }
            R.id.action_support -> {
                openSupportLink()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun showListManagementDialog() {
        val dialogBinding = DialogListManagementBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        val adapter = ShoppingListAdapter(
            onListClick = { list ->
                cartRepo.setCurrentListId(list.id)
                dialog.dismiss()
                recreate() // Refresh to show new list
            },
            onDeleteClick = { list ->
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_list_title))
                    .setMessage(getString(R.string.delete_list_message, list.name))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            cartRepo.deleteList(list)
                            if (cartRepo.getCurrentListId() == list.id) {
                                cartRepo.setCurrentListId(1) // Switch to main list
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        )

        dialogBinding.recyclerViewLists.layoutManager = LinearLayoutManager(this)
        dialogBinding.recyclerViewLists.adapter = adapter

        lifecycleScope.launch {
            cartRepo.getAllLists().collect { lists ->
                adapter.updateLists(lists, cartRepo.getCurrentListId())
            }
        }

        dialogBinding.buttonCreateList.setOnClickListener {
            val name = dialogBinding.editTextNewList.text.toString().trim()
            if (name.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    cartRepo.createList(name)
                    withContext(Dispatchers.Main) {
                        dialogBinding.editTextNewList.text?.clear()
                    }
                }
            }
        }

        dialogBinding.buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    
    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.portuguese),
            getString(R.string.english)
        )
        val languageCodes = arrayOf("pt", "en")
        val currentLanguage = LanguageHelper.getLanguage(this)
        val selectedIndex = languageCodes.indexOf(currentLanguage)
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages, selectedIndex) { dialog, which ->
                val selectedLanguage = languageCodes[which]
                if (selectedLanguage != currentLanguage) {
                    LanguageHelper.setLanguage(this, selectedLanguage)
                    recreate() // Restart activity to apply language change
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun openSupportLink() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.SUPPORT_URL))
        startActivity(intent)
    }
}