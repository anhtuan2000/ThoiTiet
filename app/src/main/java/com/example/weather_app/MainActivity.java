package com.example.weather_app;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText edtTenThanhPho;
    TextView tvTenTp, tvTenQg, tvNhietDo, tvDoAm, tvGio, tvMay, tvNgayThang, tvTrangThai;
    Button btnChon, btnTiepTheo;
    ImageView imgIcon;

    static final String DEFAULT_CITY = "Hanoi";
    static final String API_KEY = "52e0e35b5a56281bf882f8316a4c4ac8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        getCurrentWeatherData(DEFAULT_CITY);
        clickButton();
    }

    private void getCurrentWeatherData(final String city) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("KetQua", response);

                        // Xử lý phản hồi từ API
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Loi", error.toString());
                        // Hiển thị thông báo lỗi nếu có lỗi xảy ra
                        Toast.makeText(MainActivity.this, "Có lỗi xảy ra. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    private void init() {
        edtTenThanhPho = findViewById(R.id.edtTenTp);
        tvTenTp = findViewById(R.id.tvThanhPho);
        tvTenQg = findViewById(R.id.tvQuocGia);
        tvDoAm = findViewById(R.id.tvDoAm);
        tvGio = findViewById(R.id.tvGio);
        tvMay = findViewById(R.id.tvMay);
        tvNgayThang = findViewById(R.id.tvNgayThang);
        btnChon = findViewById(R.id.btnThanhPho);
        btnTiepTheo = findViewById(R.id.btnNgayTiepTheo);
        imgIcon = findViewById(R.id.imgThoiTiet);
        tvNhietDo = findViewById(R.id.tvNhietDo);
        tvTrangThai = findViewById(R.id.tvTrangThai);
    }

    private void processResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Kiểm tra xem API có trả về thông tin thành phố không
            if (jsonObject.has("name")) {
                String name = jsonObject.getString("name");
                tvTenTp.setText(name);

                JSONObject sysObject = jsonObject.getJSONObject("sys");
                String quocGia = sysObject.getString("country");
                tvTenQg.setText(quocGia);

                long timestamp = jsonObject.getLong("dt") * 1000; // Convert giây thành mili giây
                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
                String strDay = sdf.format(new Date(timestamp));
                tvNgayThang.setText(strDay);

                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String status = weatherObject.getString("main");
                String icon = weatherObject.getString("icon");
                tvTrangThai.setText(status);
                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + icon + ".png").into(imgIcon);

                JSONObject mainObject = jsonObject.getJSONObject("main");
                double nhietDo = mainObject.getDouble("temp");
                tvNhietDo.setText(String.format("%.1f", nhietDo) + "°C");

                int doAm = mainObject.getInt("humidity");
                tvDoAm.setText(doAm + "%");

                JSONObject windObject = jsonObject.getJSONObject("wind");
                double gio = windObject.getDouble("speed");
                tvGio.setText(gio + " m/s");

                JSONObject cloudObject = jsonObject.getJSONObject("clouds");
                int may = cloudObject.getInt("all");
                tvMay.setText(may + "%");
            } else {
                // Thông báo khi không tìm thấy thành phố
                Toast.makeText(MainActivity.this, "Không tìm thấy thông tin cho thành phố này. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clickButton() {
        btnChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTenThanhPho.getText().toString();
                getCurrentWeatherData(city);
            }
        });

        btnTiepTheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtTenThanhPho.getText().toString();
                Intent intent = new Intent(MainActivity.this, DetailWeatherActivity.class);
                intent.putExtra("name", city);
                startActivity(intent);
            }
        });
    }
}
