package com.yolanda.uasyolan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class DetailPembelian extends AppCompatActivity {
    public  DatePickerDialog datePickerDialog;
    private EditText editnama, editalamat;
    private TextView dateSelect, txtbanyakjumlahBlack, txtbanyakjumlahWhite;
    private CheckBox cbBlack, cbWhite;
    private Button  btnincrementBlack, btndecrementBlack, btnincrementWhite, btndecrementWhite,btnbayar;
    private ImageButton btnsubmit, btnclear;
    private RadioButton rbsendiri, rbkurir;

    Spinner spinner;
    String kota;

    int quantityBlack = 0;
    int quantitywhite=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pembelian);
        initDatePicker();
        dateSelect=findViewById(R.id.edittanggal);
        editnama=findViewById(R.id.editnama);
        editalamat=findViewById(R.id.editalamat);
        cbBlack=findViewById(R.id.cbBlack);
        cbWhite=findViewById(R.id.cbWhite);
        btnincrementBlack=findViewById(R.id.btnIncrementBlack);
        btndecrementBlack=findViewById(R.id.btnDecrementBlack);
        btnincrementWhite=findViewById(R.id.btnIncrementWhite);
        btndecrementWhite=findViewById(R.id.btnDecrementWhite);
        txtbanyakjumlahBlack=findViewById(R.id.txtbanyakjumlahBlack);
        txtbanyakjumlahWhite=findViewById(R.id.txtbanyakjumlahWhite);
        rbsendiri=findViewById(R.id.rbsendiri);
        rbkurir=findViewById(R.id.rbkurir);
        btnsubmit=findViewById(R.id.btnsubmit);
        btnbayar=findViewById(R.id.btnbayar);
        btnclear=findViewById(R.id.btnclear);
        spinner=findViewById(R.id.spkota);

        btnbayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bayarz = new Intent(DetailPembelian.this,BayarActivity.class);
                startActivity(bayarz);
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDatePicker();
                String nama=editnama.getText().toString();
                String kota=spinner.getSelectedItem().toString();
                String alamat=editalamat.getText().toString();
                String tanggal=dateSelect.getText().toString();
                boolean hasBlack = cbBlack.isChecked();
                boolean hasWHite = cbWhite.isChecked();
                int harga = BlackSkirt(hasBlack)+WhiteSkirt(hasWHite);
                int totalBlack = quantityBlack;
                int totalWhite = quantitywhite;


                String pengambilan="";
                if (rbsendiri.isChecked()){
                    pengambilan = "Diambil sendiri";
                }
                if (rbkurir.isChecked()){
                    pengambilan = "Diantar kurir";
                }
                Log.v("MainActivity","Nama: "+nama);
                Log.v("MainActivity","Alamat pemesan: "+alamat);
                Log.v("MainActivity","Asal Kota: "+kota);
                Log.v("MainActivity","tanggal pemesan : "+tanggal);
                Log.v("MainActivity","Tambahan Yang Di Pilih : "+hasBlack);
                Log.v("MainActivity","tambahan Yang Di Pilih : "+hasWHite);
                Log.v("MainActivity","Jumlah Pemesanan Black Cage SKirt: "+totalBlack);
                Log.v("MainActivity","Jumlah Pemesanan White Cage SKirt : "+totalWhite);
                Log.v("MainActivity","Metode Pengambilan" + pengambilan);
                Log.v("MainActivity","Total Tambahan Dress : "+harga);
                String Name=hasil(nama,kota,alamat, tanggal,hasBlack,hasWHite,totalBlack,totalWhite,pengambilan,harga);
                displayMessage(Name);


//
//                 Toast.makeText(getApplicationContext(), "Nama : "+nama+"\n"+ "Kota Pemesan : "+kota+"\n"+"Alamat Pemesan : "+alamat+"\n"+"Tanggal Pemesanan: "+tanggal+"\n"+ "Menu yang dipilih: "+menu+"\n"+"Tambahan: "+tambahan+"\n"+"Pengambilan: "+pengambilan+"\n"+"Total Harga : "+harga, Toast.LENGTH_LONG).show();
            }
        });

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                editnama.requestFocus();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kota=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private String hasil(String nama,String kota,String alamat, String tanggal,boolean hasBlack, boolean hasWhite,int totalBLack,int totalWhite,String pengambilan, int harga){
        String Name = " Nama Pemesan= "+nama;
        Name+="\n Alamat Pemesan=" +alamat;
        Name+="\n Kota Pemesan=" +kota;
        Name+="\n Tanggal Pemesanan=" +tanggal;
        Name+="\n Tambahan Black Cage Skirt=" +hasBlack;
        Name+="\n Pemesanan Black Cage SKirt=" +totalBLack;
        Name+="\n Tambahan White Cage SKirt=" +hasWhite;
        Name+="\n Pemesanan White Cage Skirt=" +totalWhite;
        Name+="\n Pengambilan=" +pengambilan;
        Name+="\n Total Tambahan Dress=" +harga;
        return Name;
    }

    public void incrementBlack(View view){//perintah tombol tambah
        if(quantityBlack==100){
            Toast.makeText(this,"pesanan maximal 100",Toast.LENGTH_SHORT).show();
            return;
        }
        quantityBlack = quantityBlack+1 ;
        displaychoco(quantityBlack);
    }


    public void decrementBlack(View view){//perintah tombol kurang
        if (quantityBlack==1){
            Toast.makeText(this,"pesanan minimal 1",Toast.LENGTH_SHORT).show();
            return;
        }
        quantityBlack = quantityBlack -1;
        displaychoco(quantityBlack);
    }

    public void incrementWhite(View view){//perintah tombol tambah
        if(quantitywhite==100){
            Toast.makeText(this,"pesanan maximal 100",Toast.LENGTH_SHORT).show();
            return;
        }
        quantitywhite = quantitywhite+1 ;
        displaycheese(quantitywhite);
    }
    public void decrementWhite(View view){//perintah tombol kurang
        if (quantitywhite==1){
            Toast.makeText(this,"pesanan minimal 1",Toast.LENGTH_SHORT).show();
            return;
        }
        quantitywhite = quantitywhite -1;
        displaycheese(quantitywhite);
    }

    private void displaychoco(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.txtbanyakjumlahBlack);
        quantityTextView.setText("" + number);
    }

    private void displaycheese(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.txtbanyakjumlahWhite);
        quantityTextView.setText("" + number);
    }

    private int BlackSkirt(boolean addBlack){
        int Black = 500000;

        return quantityBlack * Black;
    }

    private int WhiteSkirt(boolean addWhite){
        int White = 800000;
        return  quantitywhite * White;
    }


    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void  onDateSet (DatePicker datePicker,int year, int month, int day)
            {
                month=month+1;
                String date =makeDataString (day,month,year);
                dateSelect.setText(date);
            }
        };
        Calendar cal =Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style,dateSetListener,year,month,day);
    }

    private String makeDataString (int day, int month, int year)
    {
        return day +"/" + month +"/"+ year;
    }
    public  void openDatePicker(View view) { datePickerDialog.show();}

    private void displayMessage(String message) {
        TextView hasilkito = (TextView) findViewById(R.id.Hasil);
        hasilkito.setText(message);
    }

}