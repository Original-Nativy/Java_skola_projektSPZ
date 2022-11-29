package com.example.new_project_java;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Activity_games extends CameraActivity implements AdapterView.OnItemSelectedListener{
    int Pocitadlo = 7;
    String[] country = { "Default ", "Binar Otsu", "Grayscale", "Cmyk", "Binar adaptative"};
    ImageView SPZimgV;
    Bitmap bitmap;
    private static String LOGTAG = "OpenCV_log";
    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:{
                    Log.v(LOGTAG, "OpenCV loaded");
                    mOpenCvCameraView.enableView();
                }break;
                default:{
                    super.onManagerConnected(status);
                }break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_with_pic);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        mOpenCvCameraView= (CameraBridgeViewBase) findViewById(R.id.opensur);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(cvCameraViewListener);

        SPZimgV= (ImageView) findViewById(R.id.SPZsaimgV);
        bitmap = Bitmap.createBitmap(720, 960, Bitmap.Config.ARGB_8888);
        SPZimgV.setImageBitmap(bitmap);
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {
        Mat input_rgba;
        Mat input_gray;
        Mat img_rectangle;
        Mat img_binar_helper;
        Mat img_binar;
        Mat img_grayscale;
        Mat binImage;
        Mat pomocInput;
        Mat sourceImage;
        Mat sedaaa;
        Mat grayImage;
        Mat plate;
        Mat plateImg;
        Mat imgToTess;
        Mat Pomoc2,blacke,imgout;
        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        public void cistic(){
            if(input_rgba != null)
                input_rgba.release();
            if(pomocInput != null)
                pomocInput.release();
            if(binImage != null)
                binImage.release();
            if(input_gray != null)
                input_rgba.release();
            if(img_rectangle!= null)
                img_rectangle.release();
            if(img_binar_helper!=null)
                img_binar_helper.release();
            if(img_binar!=null)
                img_binar.release();
            if(img_grayscale!=null)
                img_grayscale.release();
            if(sourceImage!=null)
                sourceImage.release();
            if(sedaaa!=null)
                sedaaa.release();
            if(grayImage!=null)
                grayImage.release();
            if(plate!=null)
                plate.release();
            if(plateImg!=null)
                plateImg.release();
            if(imgToTess != null)
                imgToTess.release();
            if(blacke != null)
                blacke.release();
            if(Pomoc2 != null)
                Pomoc2.release();
            if(imgout != null)
                imgout.release();
        }

        public int changeColorOfImg(){
            int x=0;
            if(Pocitadlo == 0){
                binImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                binImage = sourceImage;}

            if(Pocitadlo == 2){
                binImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                Imgproc.cvtColor(sourceImage,binImage,Imgproc.COLOR_BGRA2GRAY);}

            if(Pocitadlo == 1){
                grayImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                Imgproc.cvtColor(sourceImage,grayImage,Imgproc.COLOR_BGRA2GRAY);
                binImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                Imgproc.threshold(grayImage,binImage,100,255,Imgproc.THRESH_OTSU);//prevod binar otsu metoda
            }
            if(Pocitadlo == 4){
                grayImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                Imgproc.cvtColor(sourceImage,grayImage,Imgproc.COLOR_BGRA2GRAY);
                binImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                Imgproc.adaptiveThreshold(grayImage,binImage,255,
                        Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,31,0);}
            if(Pocitadlo == 3){
                sedaaa = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
                binImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC3);
                Imgproc.cvtColor(sourceImage,binImage,Imgproc.COLOR_BGRA2YUV_I420);}

            if (binImage.width() != 0 && binImage.height()!=0 ){
            runOnUiThread(()->{
                bitmap = Bitmap.createBitmap(binImage.width(),binImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(binImage, bitmap);
                SPZimgV.setImageBitmap(bitmap);
                SPZimgV.invalidate();
            });}
            return x;
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            cistic();

            input_rgba = inputFrame.rgba();
            sourceImage = input_rgba.clone().t();
            Core.flip(sourceImage,sourceImage,1);
            pomocInput = sourceImage.clone();
            if(Pocitadlo < 7)
                 changeColorOfImg();
            else {
                binImage = new Mat(sourceImage.width(), sourceImage.height(), CvType.CV_8UC1);
            binImage=sourceImage;
            }

            runOnUiThread(()->{
                bitmap = Bitmap.createBitmap(binImage.width(),binImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(binImage, bitmap);
                SPZimgV.setImageBitmap(bitmap);
                SPZimgV.invalidate();
            });
            //bitmap.reconfigure(plate.width(),plate.height(), Bitmap.Config.ARGB_8888);
            //Log.d("sirka", " bitmapa sirka vyska: " + bitmap.getWidth() + bitmap.getHeight());
            return input_rgba;//vraci image zobrazeny v FrameLay
        }
    };
    @Override
    public void onPause() {
        super.onPause();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(LOGTAG, "OpenCV not found");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }else{
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(),country[i] , Toast.LENGTH_LONG).show();
        Pocitadlo = i ;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

