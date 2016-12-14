import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
/**
 * Android平台工具类
 * @author JebySun
 *
 */
public class AndroidUtil {
	
	private AndroidUtil() {}
	
	/**
	 * dp转换为px
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {  
	    float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dp * scale + 0.5f);
	}  
	
	/**
	 * px转换为dp
	 * @param context
	 * @param px
	 * @return
	 */
	public static int px2dp(Context context, float px) {  
	    float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (px / scale + 0.5f);  
	}  
	

	/**
	 * drawable 转换成 bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int width = drawable.getIntrinsicWidth(); 
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); 
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap的画布
		drawable.setBounds(0, 0, width, height);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas); 
		return bitmap;
	}

	/**
	 * drawable缩放
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		Bitmap oldbmp = drawableToBitmap(drawable);
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Matrix matrix = new Matrix(); 
		// 计算缩放比例
		float scaleWidth = ((float) w / width); 
		float scaleHeight = ((float) h / height);
		// 设置缩放比例
		matrix.postScale(scaleWidth, scaleHeight); 
		// 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
		if (null != oldbmp) {
			oldbmp.recycle();
		}
		// 把 bitmap 转换成 drawable 并返回
		return new BitmapDrawable(newbmp); 
	}
	
	
	/**
	 * 将毫秒转换为时分秒
	 * @param msecond
	 * @return
	 */
    public static String timeFormat(int msecond) {
        String timeStr = null;
        
        int hour = 0;
        int minute = 0;
        int second = 0;
        msecond /= 1000;
        if (msecond <= 0) {
        	return "00:00";
        } else {
            minute = msecond / 60;
            if (minute < 60) {
                second = msecond % 60;
                timeStr = formatIntStartZero(minute) + ":" + formatIntStartZero(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                	return "99:59:59";
                }
                minute = minute % 60;
                second = msecond - hour * 3600 - minute * 60;
                timeStr = formatIntStartZero(hour) + ":" + formatIntStartZero(minute) + ":" + formatIntStartZero(second);
            }
        }
        return timeStr;
    }

    /**
     * 将10一下的数字转换为补零字符串
     * @param i
     * @return
     */
    public static String formatIntStartZero(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
    
    /**
     * 给视图设置模糊图片背景
     * android自带renderscript实现
     * @param context
     * @param bmap
     * @param view
     */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void blur(Context context, Bitmap bmap, View view) {
	    float radius = 20;
	    Bitmap overlay = Bitmap.createBitmap((int)(view.getMeasuredWidth()), (int)(view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);  
	    Canvas canvas = new Canvas(overlay);
	    canvas.drawBitmap(bmap, 0, 0, null); 
	    RenderScript rs = RenderScript.create(context);  
	  
	    Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);  
	    ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());  
	    blur.setInput(overlayAlloc);  
	    blur.setRadius(radius);  
	    blur.forEach(overlayAlloc);  
	    overlayAlloc.copyTo(overlay);  
	    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), overlay);
	    
	    if (VERSION.SDK_INT < 16) {
	    	view.setBackgroundDrawable(drawable);
	    } else {
	    	view.setBackground(drawable);  
	    }
	    rs.destroy(); 
	}
	
	
	/**
	 * 给视图设置模糊图片背景
	 * 纯Java代码算法实现
	 * @param context
	 * @param bkg
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void fastBlur(Context context, Bitmap bkg, View view) {
		//原图缩小多少倍
		float scale = 20;
		float radius = 10;

		Bitmap overlay = Bitmap.createBitmap(
				(int) (view.getMeasuredWidth() / scale),
				(int) (view.getMeasuredHeight() / scale),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft() / scale, -view.getTop() / scale);
		canvas.scale(1 / scale, 1 / scale);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bkg, 0, 0, paint);

		overlay = FastBlur.doBlur(overlay, (int) radius, true);
	    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), overlay);
	    if (VERSION.SDK_INT < 16) {
	    	view.setBackgroundDrawable(drawable);
	    } else {
	    	view.setBackground(drawable);  
	    }
	    
	    saveMyBitmap("/storage/emulated/0/1024/1.png", overlay);
	}
	
	/**
	 * 将Bitmap保存到手机指定位置。
	 * @param fileName
	 * @param mBitmap
	 */
	public static void saveMyBitmap(String fileName, Bitmap mBitmap) {
		File f = new File(fileName);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	



    /**
     * 判断是否安装某个应用
     */
    public static boolean hasInstalledApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pkgInfoList = packageManager.getInstalledPackages(0);
        if (pkgInfoList != null) {
            for (PackageInfo pkgInfo : pkgInfoList) {
                if (packageName.equals(pkgInfo.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 调用迅雷客户端下载
     */
    public static void downloadByThunder(String fileUrl) {
        Uri uri = Uri.parse(fileUrl);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.addCategory(Intent.CATEGORY_BROWSABLE);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            Log.e("===Acitivity没有找到====", "e");
            e.printStackTrace();
        }
    }

    /**
     * 调用迅雷V5.17下载
     */
    public static void downloadByThunderV5_17(String fileUrl) {
        Uri uri = Uri.parse(fileUrl);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.xunlei.downloadprovider", "com.xunlei.downloadprovider.bho.ScanCodeResultActivity");
        it.setComponent(cn);
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            Log.e("===Acitivity没有找到====", "e");
            e.printStackTrace();
        }
    }
    /**
     * 调用迅雷V5.28下载
     */
    public static void downloadByThunderV5_28(String fileUrl) {
        Uri uri = Uri.parse(fileUrl);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.xunlei.downloadprovider", "com.xunlei.downloadprovider.launch.dispatch.mocklink.LinkScanCodeResultActivity");
        it.setComponent(cn);
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            Log.e("===Acitivity没有找到====", "e");
            e.printStackTrace();
        }
    }
	
}







