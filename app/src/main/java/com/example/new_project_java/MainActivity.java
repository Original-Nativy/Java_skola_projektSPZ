package com.example.new_project_java;
import static org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceView;
import android.widget.ImageView;
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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum Method{
    Otsu,
    Adaptative,
    Jina
}
public class MainActivity extends CameraActivity {
    ImageView imgSPZ_view;
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
        setContentView(R.layout.activity_main);
        mOpenCvCameraView= (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(cvCameraViewListener);

        imgSPZ_view= (ImageView) findViewById(R.id.SPZ_imgV);
        bitmap = Bitmap.createBitmap(720, 960, Bitmap.Config.ARGB_8888);
        imgSPZ_view.setImageBitmap(bitmap);
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }
    int i= 0;
    int k =0;

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
        Rect obdelnik;
        Mat plate;
        Mat plateImg;

        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }
        public Rect GetPlateLocation(Mat sourceImage, int contourCount,int koef,int type)
        {
            grayImage = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
            Imgproc.cvtColor(sourceImage,grayImage,Imgproc.COLOR_BGRA2GRAY);

            int kern = 9;

            if(type == 8)
            {
                binImage = new Mat(grayImage.width(),grayImage.height(), CvType.CV_8UC1);
                Imgproc.threshold(grayImage,binImage,100,255,Imgproc.THRESH_OTSU);//prevod binar otsu metoda
            }
            else if(type == 0)
            {
                Imgproc.adaptiveThreshold(grayImage,binImage,255,
                        Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,kern,0);
            }
            else{}

            ArrayList<MatOfPoint> contours = FindContour(binImage,3);
            ArrayList<Pair<Integer, Double>> contoitAreas = new ArrayList<Pair<Integer, Double>>();
            //Map<Integer, Double>  = new HashMap<Integer, Double>();

            for(int i = 0; i<contours.size();i++)
            {
                double area = Imgproc.contourArea(contours.get(i));
                Rect rect = Imgproc.boundingRect(contours.get(i));
                float aspectRatio = (float) rect.height /(float) rect.width;
                float reactArea = (float) rect.height* (float) rect.width;

                //if(rect.width>30 && rect.height>10 && aspectRatio > 1 &&
                        //area <1.3 *reactArea && koef*area >reactArea && aspectRatio<11);

                if((int)rect.height>100 && rect.width>50 && rect.height>rect.width && aspectRatio > 1 && area <1.3 *reactArea && koef*area >reactArea && aspectRatio<11 && reactArea>200)
                {
                    contoitAreas.add(new Pair(i,area));
                }
            }


            Collections.sort(contoitAreas, Comparator.comparing(p ->  -p.second));

            List<Pair<Integer, Double>> bestContours;
            if(contoitAreas.stream().count()>1) {
                bestContours = contoitAreas.subList(0, 1);
            }
            else if(contoitAreas.stream().count()==1)
            {
                bestContours = contoitAreas.subList(0, 1);
            }
            else{ bestContours= null;}

            //bestContours.removeIf(e -> {
                //Rect r = Imgproc.boundingRect(contours.get(e.first));
              //  return r.x < 3 || r.width > binImage.height() - 5;
            //} );
            if(bestContours != null)
                bestContours = bestContours.subList(0, 1);

            Rect nic= new Rect();
            if(bestContours != null)
            {
                int key = bestContours.get(0).first;
                Rect rec = Imgproc.boundingRect(contours.get(key));
                return rec;
            }

            return nic;
        }
        public ArrayList<MatOfPoint> FindContour(Mat _letter,int stromcek)//tree=3
        {
            ArrayList<MatOfPoint> contoury = new ArrayList<MatOfPoint>();
            Mat hier= new Mat();
            Imgproc.findContours(_letter,contoury,hier,stromcek,Imgproc.CHAIN_APPROX_SIMPLE);
            return contoury;
        }
        public Mat CropImage(Mat grayOriginal, Rect rec){
            plateImg = new Mat(grayOriginal.height(),grayOriginal.width(), CvType.CV_8UC1);
            plateImg= plateImg.setTo(new Scalar(255));
            if(rec.width==0 ||rec.height==0)
                rec = new Rect(1,1,1,1);
            grayOriginal= grayOriginal.submat(rec);
            grayOriginal.copyTo(plateImg);
            plateImg= plateImg.t();
            Log.d("test", "sirka vyska: " +plateImg.width());
            return plateImg;
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
        }
        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            cistic();

            input_rgba = inputFrame.rgba();
            sourceImage = input_rgba.clone();
            pomocInput = sourceImage.clone();
            sedaaa = new Mat(sourceImage.width(),sourceImage.height(), CvType.CV_8UC1);
            Imgproc.cvtColor(sourceImage,sedaaa,Imgproc.COLOR_BGRA2GRAY);

            img_rectangle = sourceImage.clone();
            //img_rectangle.setTo(new Scalar(255,0,0,255));
            int sirkaStred  = 1*sourceImage.width()/2;
            int vyskaStred = 1*(sourceImage.height()/2);//stredy obrazu +-1px
            int koefSir  =sirkaStred/5;
            int koefVys = 6*vyskaStred/8;
            int x = sirkaStred;//sirkaStred-koefSir;
            int y = vyskaStred;//vyskaStred-koefVys;

            i++;//pocitadlo snimku
            Imgproc.rectangle(img_rectangle,new Rect(x ,y,koefSir/koefSir,koefVys/koefVys),new Scalar(255,0,255,220),10);//vykresli orientacni obdelnik[tecku]
            if(i%10 ==0) {//co x snimku aplikuje vyhledavac spz
                for(int i = 0; i<1; i++)
                {
                    Rect plateLoc = i !=2 ? GetPlateLocation(sourceImage,1,2,8)
                            : GetPlateLocation(sourceImage,1,2,8) ;
                    obdelnik= plateLoc;
                    float pomocsirka=plateLoc.height;
                    float pomocvyska=plateLoc.width;
                    Log.d("sirka", "sirka vyska: " + pomocsirka + pomocvyska);
                    Imgproc.rectangle(img_rectangle,plateLoc,new Scalar(255,0,0,220),10);

                    plate = i !=2 ? CropImage(binImage,plateLoc) : CropImage(sedaaa,plateLoc);
                }

                float pomocsirka=plate.width();
                float pomocvyska=plate.height();
                Log.d("sirka", "sirka vyska: " + pomocsirka + pomocvyska);

                if(pomocsirka == 0.0 || pomocvyska ==0){
                    plate =new Mat(10,10,CvType.CV_8UC1);
                    plate = plate.setTo(new Scalar(0));
                }

                //plate = plate.t();

                //Core.flip(plate,plate,0);

                //bitmap.reconfigure(plate.width(),plate.height(), Bitmap.Config.ARGB_8888);
                Log.d("sirka", " bitmapa sirka vyska: " + bitmap.getWidth() + bitmap.getHeight());
                //Utils.matToBitmap(plate, bitmap);//konverze z mat do Bitmapy a uloz do promenne bitmap
                //imgSPZ_view.invalidate();//aktualizace imageview
                runOnUiThread(()->{
                    bitmap = Bitmap.createBitmap(plate.width(),plate.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(plate, bitmap);
                    imgSPZ_view.setImageBitmap(bitmap);
                    imgSPZ_view.invalidate();
                });
            }
            if (obdelnik!= null && i%10<10){
                Imgproc.rectangle(img_rectangle,obdelnik,new Scalar(255,0,0,220),10);
            }
            return img_rectangle;//vraci image zobrazeny v FrameLay
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
}