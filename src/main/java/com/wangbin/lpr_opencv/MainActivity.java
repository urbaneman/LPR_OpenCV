package com.wangbin.lpr_opencv;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton historybt = null;
    private ImageButton scanningbt = null;
    private ImageButton camerabt = null;
    private ImageButton filesbt = null;
    private Bitmap inputImage = null;
    private Bitmap background = null;
    private Mat inputImage_Mat = null;
    private Mat inputImage_Gray = null;
    private ImageView imageView = null;
    private String image_path = null;
    private Bitmap imageshow = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historybt = (ImageButton)findViewById(R.id.button_history);
        scanningbt = (ImageButton)findViewById(R.id.button_recog);
        camerabt = (ImageButton)findViewById(R.id.button_takephoto);
        filesbt = (ImageButton)findViewById(R.id.button_localfiles);
        imageView = (ImageView)findViewById(R.id.imageView);

        historybt.setOnClickListener(this);
        scanningbt.setOnClickListener(this);
        camerabt.setOnClickListener(this);
        filesbt.setOnClickListener(this);

        imageView.setDrawingCacheEnabled(true);
        background = imageView.getDrawingCache();
        imageView.setDrawingCacheEnabled(false);


    }

    private boolean getImage()
    {
        imageView.setDrawingCacheEnabled(true);
        inputImage = imageView.getDrawingCache();
        imageView.setDrawingCacheEnabled(false);
        if(inputImage.sameAs(background))
            return false;
        else
            return true;
    }

    //按键监听
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_history:
                break;
            case R.id.button_recog:
                /*if(getImage()){
                    Toast.makeText(MainActivity.this,"请打开一张图片!",Toast.LENGTH_LONG).show();
                    break;
                }*/
                inputImage_Mat =  new Mat();
                inputImage_Gray = new Mat();
                imageshow = Bitmap.createBitmap(inputImage.getWidth(),inputImage.getHeight(), Bitmap.Config.ARGB_8888);
                Utils.bitmapToMat(inputImage, inputImage_Mat);
                Imgproc.cvtColor(inputImage_Mat, inputImage_Gray, Imgproc.COLOR_RGB2GRAY);
                Utils.matToBitmap(inputImage_Gray, imageshow);
                imageView.setDrawingCacheEnabled(true);
                imageView.setImageBitmap(imageshow);
                imageView.setDrawingCacheEnabled(false);
                break;
            case R.id.button_takephoto:
                //startActivity(new Intent(MainActivity.this, CameraActivity.class));
                break;
            case R.id.button_localfiles:
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    //接收图片管理器返回的信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                image_path = getRealFilePath(this,uri);

                Toast.makeText(this, "path："+image_path, Toast.LENGTH_LONG).show();

                inputImage = BitmapFactory.decodeFile(image_path);
                //Utils.matToBitmap(inputImage_Mat, inputImage);
                imageView.setDrawingCacheEnabled(true);
                imageView.setImageBitmap(inputImage);
                imageView.setDrawingCacheEnabled(false);
            }
        }
    }

    public static Bitmap getLoacalBitmap(String fileUrl) {
        try {
            FileInputStream fis = new FileInputStream(fileUrl);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    //获取文件真实的绝对路径
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //OpenCV 动态加载
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            //Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    //mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void SJJ() {

    }

}
