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
import com.android.volley.toolbox.Volley;
import com.example.fyp.adapters.RecyclerAdapterShadeMatches;
import com.example.fyp.objects.Shade;
import com.example.fyp.objects.ShadeMatch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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
    private ArrayList<ShadeMatch> shadeMatchList = new ArrayList<ShadeMatch>();
    private PointF leftPoint = null;
    private PointF rightPoint = null;
    private String rightColour = null;
    private String leftColour = null;
    private String closestId = null;
    private RecyclerAdapterShadeMatches adapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shade_match);
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.faceImageView);
        recyclerView = findViewById(R.id.matchRCV);

        adapter = new RecyclerAdapterShadeMatches(shadeMatchList);
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
                                                    leftColour = findColour(leftPoint);
                                                    String leftShade = closestShade(leftPoint);
                                                    getProductByShade(leftShade);
                                                }
                                                if (rightCheek != null) {
                                                    rightPoint = rightCheek.getPosition();
                                                    rightColour = findColour(rightPoint);
                                                    String rightShade = closestShade(rightPoint);
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

    public String findColour(PointF point) {
        int x = (int) point.x;
        int y = (int) point.y;
        int pixel = bitmap.getPixel(x, y);
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        String hex = String.format("#%02x%02x%02x", red, green, blue);
        return hex;
    }

    public void getProductByShade(String shade){

        String shadeHex = shade.substring(1);

        RequestQueue queue = Volley.newRequestQueue(ShadeMatchActivity.this);

        String stringUrl = "https://makeup-shades-api.herokuapp.com/shades/hex/" + shadeHex;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String brand = obj.getString("brand");
                        String product = obj.getString("product");
                        String hex = obj.getString("hex");
                        hex = "#" + hex;
                        ShadeMatch shadeMatch = new ShadeMatch(brand, product, hex);
                        shadeMatchList.add(shadeMatch);
                        adapter.notifyDataSetChanged();
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

    public void readData() {
        RequestQueue queue = Volley.newRequestQueue(ShadeMatchActivity.this);

        String stringUrl = "https://makeup-shades-api.herokuapp.com/shades";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String id = obj.getString("_id");
                        String hex = obj.getString("hex");
                        if (!hex.equalsIgnoreCase("null") && hex.length() ==6) {
                            hex = "#" + hex;
                            Shade shade = new Shade(id, hex);
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

    public String closestShade(PointF point) {
        double distance = 0;
        double closestDist = 100000;

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
                closestId = s.getColour();
            }
        }
        return closestId;
    }

}