package ru.iu3.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;//импорт класса Hex
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.regex.*;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib"); //подключение библиотек
        System.loadLibrary("mbedcrypto");
        initRng();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        byte[] key = randomBytes(16); //создание массива из 16 случайных байт
//        byte[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}; //исходные данные
//        byte[] encrypted = encrypt(key, data); //здесь мы вызываем функцию из native-lib.cpp
//        byte[] decrypted = decrypt(key, encrypted); // здесь то же самое
//        String originalData  = new String(data, StandardCharsets.UTF_8); //массив байт преобразуется в строку в кодировке UTF_8
//        String encryptedData = new String(encrypted, StandardCharsets.UTF_8);
//        String decryptedData = new String(decrypted, StandardCharsets.UTF_8);

//        System.out.println(originalData); //вывод данных в консоль
//        System.out.println(encryptedData);
//        System.out.println(decryptedData);

//        String output = new String( // формирование общей строки
//                "Original: "  + originalData  + "\n" +
//                        "Encrypted: " + encryptedData + "\n" +
//                        "Decrypted: " + decryptedData + "\0"
//        );

//        System.out.println("Original: "  + originalData); //опять вывод в консоль
//        System.out.println("Encrypted: " + encryptedData);
//        System.out.println("Decrypted: " + decryptedData);

        // Example of a call to a native method
        //TextView tv = findViewById(R.id.sample_text); //
        //tv.setText(stringFromJNI()); //добавить строку в центр окна.
        //tv.setText(output);
    }

    public void onButtonClick(View v) throws DecoderException {
        //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        /*
        byte[] key = StringToHex("0123456789ABCDEF0123456789ABCDE0");
        byte[] enc = encrypt(key, StringToHex("000000000000000102"));
        byte[] dec = decrypt(key, enc);
        String s = new String(Hex.encodeHex(dec)).toUpperCase();//переводит hex в символы
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();//выводит всплывающее сообщение
        */
        //Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);

        //Intent it = new Intent(this, PinpadActivity.class);
        //startActivityForResult(it,0);
        TestHttpClient();
    }

    public static byte[] StringToHex(String s) throws DecoderException {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());//для импорта Hex из apache-codec нажать alt+enter
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK || data != null) {
                String pin = data.getStringExtra("pin");
                Toast.makeText(this, pin, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void TestHttpClient()
    {
        new Thread(()->{
            try {
                HttpURLConnection uc = (HttpURLConnection) (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                //HttpURLConnection uc = (HttpURLConnection) (new URL("https://www.wikipedia.org").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(()->{
                    Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

    protected String getPageTitle(String html)
    {
        Pattern pattern = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String p;
        if (matcher.find())
            p = matcher.group(1);
        else
            p = "Not found";
        return p;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    public native String stringFromJNI(); //объявляем методы из native-lib.cpp
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}