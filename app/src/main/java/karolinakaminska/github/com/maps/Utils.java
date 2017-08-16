package karolinakaminska.github.com.maps;


import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void showToast(String str, Context context) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.show();
    }


}
