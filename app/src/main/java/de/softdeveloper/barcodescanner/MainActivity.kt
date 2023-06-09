package de.softdeveloper.barcodescanner

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import de.softdeveloper.barcodescanner.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        val COLUMN_ID = "_id"
        val COLUMN_QUANTITY = "quantity"
        val COLUMN_PRODUCT = "product"
        val COLUMN_ISSELECTED = "selected"

        val CONTENT_URI =
            Uri.parse("content://de.softdeveloper.shoppinglist.ShoppingMemoContentProvider/shopping_list")
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnScan.isEnabled = false

        if (ActivityCompat.checkSelfPermission(
                this,
                "de.softdeveloper.shoppinglist.permissions.USE_SHOPPINGLIST"
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf("de.softdeveloper.shoppinglist.permissions.USE_SHOPPINGLIST"),
                123
            )
        }else{
            enableButton()
        }

            binding.btnScan.setOnClickListener {
                val intent = Intent("com.google.zxing.client.android.SCAN")
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE")

                try {
                    activityResultLauncher.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Scanner nicht gefunden", Toast.LENGTH_SHORT).show()
                }
            }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                runBlocking {
                    if (it.resultCode == RESULT_OK && it.data != null) {
                        var value = getProduct(it.data!!.getStringExtra("SCAN_RESULT") ?: "")
                        binding.tvResult.text = value
                        val values = ContentValues().apply {
                            put(COLUMN_QUANTITY, 1)
                            put(COLUMN_PRODUCT, binding.tvResult.text.toString())
                        }
                        contentResolver.insert(CONTENT_URI, values)
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            enableButton()
        }
    }

    fun enableButton() {
        binding.btnScan.isEnabled = true
    }

    private suspend fun getProduct(scanResult: String): String {
        var result: String? = null

        val job = CoroutineScope(Dispatchers.IO).launch {
            result = getFromServer(scanResult)
        }
        job.join()
        Log.d(TAG, "getProduct: JsonString  ist $result")
        val rootObject = JSONObject(result)
        if (rootObject.has("product")) {
            val product = rootObject.getJSONObject("product")
            if (product.has("product_name")) {
                return product.getString(("product_name"))
            }
        }
        return "Artikel nicht gefunden"
    }

    private fun getFromServer(scanResult: String): String {
        val baseUrl = "https://world.openfoodfacts.org/api/v0/product/"
        val requestUrl = "$baseUrl$scanResult.json"
        var url: URL? = null
        val result = java.lang.StringBuilder()
        try {
            url = URL(requestUrl)
        } catch (e: MalformedURLException) {
            Log.e(TAG, "getFromServer: Url konnte nicht erstellt werden", e)
        }
        try {
            BufferedReader(InputStreamReader(url?.openConnection()?.getInputStream())).use {
                var line: String

                result.append(it.readText())

//                while(it.readLine().also { line = it } != null){
//                    result.append(line)
//                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "getFromServer: Fehler beim Lesen der Daten", e)
        }
//        Log.d(TAG, "getFromServer: JsonString: $result")
        return result.toString()
    }
}