package com.douglas.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.douglas.practica3.ui.home.DtoUsuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUserName, edtPassword;
    private Button btnSing;
    ArrayList<String> lista = null;
    ArrayList<DtoUsuario> listaCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserName = findViewById(R.id.usernameLog);
        edtPassword = findViewById(R.id.passwordLog);
        btnSing = findViewById(R.id.loginLog);

        btnSing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String nom = edtUserName.getText().toString();
        String pass = edtPassword.getText().toString();

        recibirLog(this, nom, pass);
    }

    private void recibirLog(final Context context, final String idCat, final String nombreCat){
        StringRequest request = new StringRequest(Request.Method.POST, Setting_VAR.URL_LOGIN_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                    if (response.length() > 3 ){
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Usuario y contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                btnSing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Error 404 Vuelva Mañana", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("usuario", idCat);
                map.put("clave", nombreCat);
                return map;
            }
        };

        //tiempo de respuesta, establece politica de reintentos
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}