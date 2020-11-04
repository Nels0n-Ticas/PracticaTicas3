package com.douglas.practica3.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.douglas.practica3.MySingleton;
import com.douglas.practica3.R;
import com.douglas.practica3.Setting_VAR;
import com.douglas.practica3.ui.home.DetalleCategoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private ListView lst;
    ArrayList<String> lista = null;
    ArrayList<DtoCategoria> listaCategoria;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final LinearLayoutCompat frameLayout2 = root.findViewById(R.id.fm2);
        lst = root.findViewById(R.id.lstCategoria);
        recibirAllCat();
        return root;
    }

    private void recibirAllCat(){
        listaCategoria = new ArrayList<DtoCategoria>();
        lista = new ArrayList<String>();
        String urlConsultaCategoria = Setting_VAR.URL_CONSULTAR_CATEGORIAS;
        StringRequest request = new StringRequest(Request.Method.POST, urlConsultaCategoria, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray respuestaJSOn = new JSONArray(response);
                    int totalEnct = respuestaJSOn.length();

                    DtoCategoria objCategorias = null;
                    for (int i = 0; i < respuestaJSOn.length(); i++){
                        JSONObject categoriaObj = respuestaJSOn.getJSONObject(i);
                        int idC = categoriaObj.getInt("id_categoria");
                        String name = categoriaObj.getString("nom_categoria");
                        int stado = categoriaObj.getInt("estado_categoria");

                        objCategorias = new DtoCategoria(idC, name, stado);

                        listaCategoria.add(objCategorias);

                        lista.add(listaCategoria.get(i).getIdCategoria() + " - " + listaCategoria.get(i).getNombre());

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lista);
                        lst.setAdapter(adapter);

                        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String codigo = "" + listaCategoria.get(i).getIdCategoria();
                                String nombre = "" + listaCategoria.get(i).getNombre();
                                String estado = "" + listaCategoria.get(i).getEstado();


                                Activity activity = new Activity();
                                Intent intent = new Intent(getActivity(), DetalleCategoria.class);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                            }
                        });

                        Log.i("Id Categoria:    ", String.valueOf(objCategorias.getIdCategoria()));
                        Log.i("Nombre:    ", String.valueOf(objCategorias.getNombre()));

                    }
                    //resultado.setText("Datos: " + response.toString());
                    //Toast.makeText(getContext(), "Id: " + idCategori + "\nNombre: " + nombreCat + "\nEstado: " + estadoCat, Toast.LENGTH_SHORT).show();

                } catch (JSONException ex){
                    String none = ex.toString();
                    Log.i("NO consulta ***** ", none);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String err = volleyError.toString();
                Log.i("No se pudo **********", err);
            }
        });
        //tiempo de respuesta, establece politica de reintentos
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}