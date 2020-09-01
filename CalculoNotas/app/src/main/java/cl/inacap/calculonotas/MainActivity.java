package cl.inacap.calculonotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import cl.inacap.calculonotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    private EditText notaTxt;
    private EditText porcentajeTxt;
    private ListView notasLv;
    private int porcentajeTotal = 0;
    private Button calcularBtn;
    private TextView promedioTxt;
    private List<Nota> notas = new ArrayList<>();
    private ArrayAdapter<Nota> notasAdapter;
    private LinearLayout layoutPromedio;
    private Button limpiarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.calcularBtn = findViewById(R.id.calcularBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.notasLv = findViewById(R.id.notasLv);
        notasAdapter = new ArrayAdapter<Nota>(this, android.R.layout.simple_list_item_1, notas);
        this.notasLv.setAdapter(notasAdapter);
        this.layoutPromedio = findViewById(R.id.layoutPromedio);
        this.calcularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> errores = new ArrayList<>();
                int porcentajeIngresado = 0;
                try {
                    porcentajeIngresado = Integer.parseInt(porcentajeTxt.getText().toString());
                    if (porcentajeIngresado < 1 || porcentajeIngresado > 100) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("Porcentaje debe ser un número entre 1 y 100");
                }


                double nota = 0;

                try {
                    nota = Double.parseDouble(notaTxt.getText().toString());
                    if (nota < 1.0 || nota > 7.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("Las notas deben ser numericas con decimales entre 1.0 y 7.0");
                }

                if (errores.isEmpty()) {
                    if (porcentajeIngresado + porcentajeTotal > 100) {
                        Toast.makeText(MainActivity.this, "El total no puede exceder 100"
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        porcentajeTotal += porcentajeIngresado;
                        Nota n = new Nota();
                        n.setPorcentaje(porcentajeIngresado);
                        n.setValor(nota);
                        notas.add(n);
                        notasAdapter.notifyDataSetChanged();
                        mostrarPromedio();
                    }
                } else{
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    String mensaje = "";
                    for(String error: errores){
                        mensaje+= "- " + error + "\n";
                    }
                    alertBuilder.setTitle(R.string.error_validacion)
                            .setMessage(mensaje)
                            .setPositiveButton("Aceptar",null)
                            .create().show();
                }
            }
        });
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPromedio.setVisibility(View.INVISIBLE);
                notas.clear();
                notasAdapter.notifyDataSetChanged();
                notaTxt.setText("");
                porcentajeTxt.setText("");
                porcentajeTotal = 0;
            }
        });
    }

    private void mostrarPromedio() {
        double promedio = 0;
        this.layoutPromedio.setVisibility(View.VISIBLE);
        for (Nota n : notas) {
            promedio += (n.getValor() * n.getPorcentaje() / 100);
        }
        this.promedioTxt.setText(String.format("%.2f",promedio));
        if (promedio < 4.0) {
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        } else {
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorVerde));
        }
    }

}