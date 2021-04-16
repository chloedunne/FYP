package com.example.fyp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.SearchView;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp.adapters.RecyclerAdapter;
import com.example.fyp.objects.Product;
import com.example.fyp.objects.Shade;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class BrowseProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseUser user;
    private ArrayList<Product> productList = new ArrayList<Product>();
    private ArrayList<Product> filteredProducts = new ArrayList<>();
    private RecyclerAdapter.RecyclerViewClickListener clickListener;
    private RecyclerAdapter adapter;
    private SearchView searchView;
    private ArrayList<String> categories = new ArrayList<String>();
    private Button blush, bronzer, eyebrow, eyeliner, eyeshadow, foundation, lip_liner, lipstick, mascara;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_products);
        Intent intent = getIntent();

        categories.add("blush");
        categories.add("bronzer");
        categories.add("eyebrow");
        categories.add("eyeliner");
        categories.add("eyeshadow");
        categories.add("foundation");
        categories.add("lip_liner");
        categories.add("lipstick");
        categories.add("mascara");

        blush = findViewById(R.id.blushButton);
        bronzer = findViewById(R.id.bronzerButton);
        eyebrow = findViewById(R.id.eyebrowButton);
        eyeliner = findViewById(R.id.eyelinerButton);
        eyeshadow = findViewById(R.id.eyeshadowButton);
        foundation = findViewById(R.id.foundationButton);
        lip_liner = findViewById(R.id.liplinerButton);
        lipstick = findViewById(R.id.lipstickButton);
        mascara = findViewById(R.id.mascaraButton);
        searchView = findViewById(R.id.searchProducts);
        recyclerView = findViewById(R.id.rcv);

        setOnClickListener();
        adapter = new RecyclerAdapter(productList, clickListener);
        recyclerView.setLayoutManager(new GridLayoutManager(BrowseProductsActivity.this, 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        loadProducts();

        blush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("blush");
            }
        });
        bronzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("bronzer");
            }
        });
        eyebrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("eyebrow");
            }
        });
        eyeliner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("eyeliner");
            }
        });
        eyeshadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("eyeshadow");
            }
        });
        foundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("foundation");
            }
        });
        lip_liner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("lip_liner");
            }
        });
        lipstick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("lipstick");
            }
        });
        mascara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts("mascara");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<Product> filteredProducts = new ArrayList<Product>();

                for(Product p : productList){
                    if(p.getName().toLowerCase().contains(newText.toLowerCase()) || p.getProductType().toLowerCase().contains(newText.toLowerCase())){
                        filteredProducts.add(p);
                    }

                    RecyclerAdapter filteredAdapter = new RecyclerAdapter(filteredProducts, clickListener);
                    recyclerView.setAdapter(filteredAdapter);
                }
               return false;
            }
        });
    }

    public void filterProducts(String productType){
        filteredProducts.clear();
        for(Product p : productList){
            if(p.getProductType().equalsIgnoreCase(productType)){
                filteredProducts.add(p);
            }
            RecyclerAdapter filteredAdapter = new RecyclerAdapter(filteredProducts, clickListener);
            recyclerView.setAdapter(filteredAdapter);
        }
    }

    public void loadProducts() {

        RequestQueue queue = Volley.newRequestQueue(BrowseProductsActivity.this);

        for (String s : categories) {
            String stringUrl = "https://makeup-api.herokuapp.com/api/v1/products.json?product_type=" + s;
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringUrl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            ArrayList<Shade> shadesArray = new ArrayList<Shade>();
                            int id = jsonObject.getInt("id");
                            String brand = jsonObject.getString("brand");
                            String name = jsonObject.getString("name");
                            String description = jsonObject.getString("description");
                            String productType = jsonObject.getString("product_type");
                            String image = jsonObject.getString("image_link");
                            String stringPrice = jsonObject.getString("price");
                            double price = 0;
                            if(stringPrice != null && !stringPrice.equals("null")){
                                price = Double.parseDouble(stringPrice);
                            }
                            JSONArray shades = jsonObject.getJSONArray("product_colors");
                            for (int x = 0; x < shades.length(); x++) {
                                JSONObject shadeDetails = shades.getJSONObject(x);
                                String shadeName = shadeDetails.getString("colour_name");
                                String colour = shadeDetails.getString("hex_value");
                                Shade shade = new Shade(shadeName, colour);
                                shadesArray.add(shade);
                            }
                            Product product = new Product(brand, name, description, productType, image, shadesArray, id, price);
                            productList.add(product);
                            adapter.notifyDataSetChanged();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        }
    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Product product;
                if(filteredProducts.isEmpty())
                    product = productList.get(position);
                else
                    product = filteredProducts.get(position);

                Intent i = new Intent(BrowseProductsActivity.this, ProductActivity.class);
                i.putExtra("product", (Serializable) product);
                startActivity(i);

            }
        };
    }
}