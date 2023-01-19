package com.example.focusstartproject

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.focusstartproject.databinding.ActivityMainBinding
import org.json.JSONObject
import android.Manifest;
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGet.setOnClickListener {
            clearTextViews()
            getData(binding.biniintv.text.toString())
        }

        binding.btnHistory.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.biniintv.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = binding.biniintv.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }



        binding.bankphonetv.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL);
            intent.data = Uri.parse("tel:${binding.bankphonetv.text}")
            startActivity(intent)
        }

        binding.bankurltv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            var url = binding.bankurltv.text
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            intent.data = Uri.parse("${url}")
            startActivity(intent)
        }

        binding.bankcitytv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("https://www.google.com/maps/place/${binding.bankcitytv.text}")
            startActivity(intent)
        }

        binding.countrynametv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("https://www.google.com/maps/place/${binding.countrynametv.text}")
            startActivity(intent)
        }
        binding.countrynameshorttv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("https://www.google.com/maps/place/${binding.countrynametv.text}")
            startActivity(intent)
        }

        binding.latitudetv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("http://maps.google.com/maps?q=${binding.latitudetvhidden.text},${binding.longitudetvhidden.text}")
            startActivity(intent)
        }
        binding.longitudetv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("http://maps.google.com/maps?q=${binding.latitudetvhidden.text},${binding.longitudetvhidden.text}")
            startActivity(intent)
        }


        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.CALL_PHONE) !== PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }

    }//end of onCreate

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CALL_PHONE) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Разрешение принято, необходимое для набора номера телефона банка", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Разрешение отказано", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }



    private fun getData(biniin: String){
        val url = "https://lookup.binlist.net/$biniin"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET,url,
            {
                response->
                val obj = JSONObject(response)


                val scheme = obj.optString("scheme")
                val type = obj.optString("type")
                val brand = obj.optString("brand")
                val prepaid = obj.optString("prepaid")
                if (obj.isNull("scheme"))else binding.schemetv.text = "$scheme"
                if (obj.isNull("type"))else binding.typetv.text = "$type"
                if (obj.isNull("brand"))else binding.brandtv.text = "$brand"
                if (obj.isNull("prepaid"))else if(prepaid.toString().equals("true"))binding.prepaidtv.text = "Yes" else binding.prepaidtv.text = "No"

                if (obj.isNull("number"))else {
                    val numberlength = obj.optJSONObject("number").optString("length")
                    val numberluhn = obj.optJSONObject("number").optString("luhn")
                    binding.lengthtv.text = "$numberlength"
                    if(numberluhn.toString().equals("true"))binding.luhntv.text = "Yes" else binding.luhntv.text = "No"
                }

                if (obj.isNull("country"))else {
                    val countryname = obj.optJSONObject("country").optString("name")
                    val countryalpha2 = obj.optJSONObject("country").optString("alpha2")
                    val countrylatitude = obj.optJSONObject("country").optString("latitude")
                    val countrylongitude = obj.optJSONObject("country").optString("longitude")
                    binding.countrynametv.text = "$countryname"
                    binding.countrynameshorttv.text = "$countryalpha2"
                    binding.latitudetv.text = "Latitude: $countrylatitude"
                    binding.longitudetv.text = "Longitude: $countrylongitude"
                    binding.latitudetvhidden.text ="$countrylatitude"
                    binding.longitudetvhidden.text="$countrylongitude"
                }

                if (obj.isNull("bank")) else {
                    val bankname = obj.optJSONObject("bank").optString("name")
                    val bankurl = obj.optJSONObject("bank").optString("url")
                    val bankphone = obj.optJSONObject("bank").optString("phone")
                    val bankcity = obj.optJSONObject("bank").optString("city")
                    binding.banknametv.text = "$bankname"
                    binding.bankurltv.text = "$bankurl"
                    binding.bankphonetv.text = "$bankphone"
                    binding.bankcitytv.text = "$bankcity"
                }

                //parseData(response)
                Log.d("MyLog", "Response: $response")
            },
            {
                Log.d("MyLog", "Volley Error: $it")
            }
            )
        queue.add(stringRequest)
    }

    private fun clearTextViews(){
        binding.schemetv.text = ""
        binding.typetv.text = ""
        binding.brandtv.text = ""
        binding.prepaidtv.text = ""
        binding.lengthtv.text = ""
        binding.luhntv.text = ""
        binding.countrynametv.text = ""
        binding.countrynameshorttv.text = ""
        binding.latitudetv.text = ""
        binding.longitudetv.text = ""
        binding.latitudetvhidden.text = ""
        binding.longitudetvhidden.text = ""
        binding.banknametv.text = ""
        binding.bankurltv.text = ""
        binding.bankphonetv.text = ""
        binding.bankcitytv.text = ""
    }


}

