package com.wangbin.lpr_opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by dell-pc on 2018/1/19.
 */

public class ImageProcess {
    private Mat src = null;
    private Mat dst = null;
    private Mat licenseplate = null;

    public ImageProcess(Mat src) {
        this.src = src;
    }

    public void setSrc(Mat src) {
        this.src = src;
    }

    public void setDst(Mat dst) {
        this.dst = dst;
    }

    public void setLicenseplate(Mat licenseplate) {
        this.licenseplate = licenseplate;
    }

    public Mat getSrc() {
        return src;
    }

    public Mat getDst() {

        return dst;
    }

    public Mat getLicenseplate() {
        return licenseplate;
    }

    public void Pre_treatment(){
        //调整图像大小
        resize(src);
        Mat img_pro = new Mat();
        //高斯模糊
        Size gaussiansize = new Size(3, 3);
        Imgproc.GaussianBlur(src,img_pro, gaussiansize,0);

        //灰度化
        if(src.channels() == 3)
            Imgproc.cvtColor(img_pro,img_pro,Imgproc.COLOR_RGB2GRAY);
        else if(src.channels() == 4)
            Imgproc.cvtColor(img_pro,img_pro,Imgproc.COLOR_RGBA2GRAY);

        //Sobel 水平边缘提取
        Imgproc.Sobel(img_pro, img_pro, CvType.CV_8UC1, 1, 0);

        //二值化
        Imgproc.threshold(img_pro, img_pro, 0, 255, Imgproc.THRESH_OTSU);

        //TODO：寻找开闭运算的最佳内核以及最佳处理顺序
        Size ksize_open = new Size(9, 9);
        Size ksize_close = new Size(19, 7);
        Point anchor = new Point(-1,-1);
        Mat element_open = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_open);
        Mat element_close = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_close);
        Imgproc.morphologyEx(img_pro,img_pro,Imgproc.MORPH_CLOSE,element_close,anchor,1);
        Imgproc.morphologyEx(img_pro,img_pro,Imgproc.MORPH_OPEN,element_open,anchor,1);
        this.dst = img_pro;
    }

    //调整输入图像的大小
    //针对手机虚假分辨率，将调整从手机直接拍摄获得的车牌图片
    //一般手机采集图像尺寸比例为4:3; 16:9和1:1三种
    //所以根据这三种尺寸的图像进行缩放至宽度为1080
    public void resize(Mat input){
        //如果宽度大于1080
        if(input.cols()>1080){
            Mat dst = new Mat();
            double rows = input.rows();
            double ratio = rows / input.cols();
            if(ratio>=1.77 && ratio<=1.78 || ratio == 1.0 || ratio>= 1.33 && ratio <= 1.34){
                Size smallsize = new Size(1080, 1080 * dst.rows() / dst.cols());
                Imgproc.resize(input, dst, smallsize, 0, 0, Imgproc.INTER_LINEAR);
                this.setSrc(dst);
            }
        }else {
            return;
        }
    }

}
