package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecogniseActivity extends AppCompatActivity {

    private TextView copyText;
    private EditText textRecog;
    ClipboardManager clipboardManager;

    Button pdfbtn,viewpdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognise);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        copyText = (TextView) findViewById(R.id.copyText);
        textRecog = (EditText) findViewById(R.id.textRecog);
        pdfbtn = (Button)findViewById(R.id.pdfbtn);
        viewpdf = (Button) findViewById(R.id.viewpdf);

        Bitmap bitmap = BitmapHelper.getInstance().getBitmap();

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                TextBlock myitem = items.valueAt(i);
                sb.append(myitem.getValue());
                sb.append("\n");
            }

            textRecog.setText(sb.toString());

            copyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipData clipData = ClipData.newPlainText("ocrText", textRecog.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(RecogniseActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
                }
            });
        }

        pdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textRecog.getText().toString();
                String path = getExternalFilesDir(null).toString()+"/user.pdf";
                File file = new File(path);

                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Document doc = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(doc,new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                doc.open();

                Font myfont= new Font(Font.FontFamily.TIMES_ROMAN,24, Font.BOLD);

                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph(text,myfont));

                try {
                    doc.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                doc.close();

                Toast.makeText(RecogniseActivity.this,"PDF created successfully",Toast.LENGTH_SHORT);
            }
        });

        viewpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewActivity.class);
                startActivity(intent);
            }
        });
    }
}
