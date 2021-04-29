package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp.adapters.RecyclerAdapterShadeMatches;
import com.example.fyp.objects.Product;
import com.example.fyp.objects.Shade;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShadeMatchActivity extends AppCompatActivity {

    private ImageView imageView;
    private InputImage image;
    private Uri imageUri;
    private Paint rectPaint;
    private Bitmap bitmap, tempBitmap;
    private Canvas canvas;
    private FirebaseAuth mAuth;
    private ArrayList<Shade> shadeList = new ArrayList<Shade>();
    private PointF leftPoint = null;
    private PointF rightPoint = null;
    private Shade closest = null;
    private RecyclerAdapterShadeMatches adapter;
    private RecyclerView recyclerView;
    private ArrayList<Product> shadeMatches = new ArrayList<Product>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shade_match);
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.faceImageView);
        recyclerView = findViewById(R.id.matchRCV);

        adapter = new RecyclerAdapterShadeMatches(shadeMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShadeMatchActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        readData();
        setUpRectangle();
        selectPicture();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                canvas = new Canvas(tempBitmap);
                canvas.drawBitmap(bitmap, 0, 0, null);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                image = InputImage.fromFilePath(getApplicationContext(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FaceDetectorOptions highAccuracyOpts = new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .build();


            FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
            Task<List<Face>> result =
                    detector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            if (faces.isEmpty()) {
                                                Toast.makeText(ShadeMatchActivity.this, "No faces detected", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Face f = faces.get(0);
                                                Rect rect = f.getBoundingBox();
                                                FaceLandmark leftCheek = f.getLandmark(FaceLandmark.LEFT_CHEEK);
                                                FaceLandmark rightCheek = f.getLandmark(FaceLandmark.RIGHT_CHEEK);
                                                if (leftCheek != null) {
                                                    leftPoint = leftCheek.getPosition();
                                                    Shade leftShade = closestShade(leftPoint);
                                                     getProductByShade(leftShade);
                                                }
                                                if (rightCheek != null) {
                                                    rightPoint = rightCheek.getPosition();
                                                    Shade rightShade = closestShade(rightPoint);
                                                     getProductByShade(rightShade);
                                                }
                                                canvas.drawRect(rect, rectPaint);
                                                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                                                Toast.makeText(ShadeMatchActivity.this, "Face detected", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("error", e.getLocalizedMessage());
                                            Toast.makeText(ShadeMatchActivity.this, "Face not detected", Toast.LENGTH_SHORT).show();
                                        }
                                    });
        }
    }

    public void getProductByShade(Shade shadeMatch) {

        int productId = shadeMatch.getId();

        RequestQueue queue = Volley.newRequestQueue(ShadeMatchActivity.this);

        String stringUrl = "https://makeup-api.herokuapp.com/api/v1/products/" + productId + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    int id = obj.getInt("id");
                    String brand = obj.getString("brand");
                    String name = obj.getString("name");
                    String description = obj.getString("description");
                    String productType = obj.getString("product_type");
                    String image = obj.getString("image_link");
                    String stringPrice = obj.getString("price");
                    double price = 10.75;
                    if (stringPrice != null && !stringPrice.equals("null")) {
                        price = Double.parseDouble(stringPrice);
                    }
                    JSONArray shades = obj.getJSONArray("product_colors");
                    for (int x = 0; x < shades.length(); x++) {
                        JSONObject shadeDetails = shades.getJSONObject(x);
                        if (shadeDetails.getString("hex_value").equalsIgnoreCase(shadeMatch.getColour())) {
                            String shadeName = shadeDetails.getString("colour_name");
                            String colour = shadeDetails.getString("hex_value");
                            Shade shade = new Shade(shadeName, colour);
                            Product product = new Product(brand, name, description, productType, image, shade, id, price);
                            shadeMatches.add(product);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ShadeMatchActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void readData() {

        RequestQueue queue = Volley.newRequestQueue(ShadeMatchActivity.this);

        String stringUrl = "https://makeup-api.herokuapp.com/api/v1/products.json?product_type=foundation";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        JSONArray shades = obj.getJSONArray("product_colors");
                        for (int x = 0; x < shades.length(); x++) {
                            JSONObject shadeDetails = shades.getJSONObject(x);
                            String shadeName = shadeDetails.getString("colour_name");
                            String hex = shadeDetails.getString("hex_value");
                            Shade shade = new Shade(shadeName, hex, id);
                            shadeList.add(shade);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ShadeMatchActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }


    public void selectPicture() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    public void setUpRectangle() {
        rectPaint = new Paint();
        rectPaint.setStrokeWidth(10);
        rectPaint.setColor(Color.parseColor("#92FC6161"));
        rectPaint.setStyle(Paint.Style.STROKE);
    }

    public Shade closestShade(PointF point) {
        double distance = 0;
        double closestDist = 999999;

        int x = (int) point.x;
        int y = (int) point.y;
        int pixel = bitmap.getPixel(x, y);
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);

        for (Shade s : shadeList) {
            String colour = s.getColour();
            int r = Integer.valueOf(colour.substring(1, 3), 16);
            int g = Integer.valueOf(colour.substring(3, 5), 16);
            int b = Integer.valueOf(colour.substring(5, 7), 16);

            distance = Math.sqrt(((r - red) * (r - red)) + ((b - blue) * (b - blue)) + ((g - green) * (g - green)));

            if (distance < closestDist) {
                closestDist = distance;
                closest = s;
            }
        }
        return closest;
    }

}