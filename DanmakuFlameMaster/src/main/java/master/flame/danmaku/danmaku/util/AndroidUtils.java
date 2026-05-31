
package master.flame.danmaku.danmaku.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class AndroidUtils {

    public static int getMemoryClass(final Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
    }

    public static long getIdOfThread(Thread thread) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            return thread.threadId();
        } else {
            //noinspection deprecation
            return thread.getId();
        }
    }
}
