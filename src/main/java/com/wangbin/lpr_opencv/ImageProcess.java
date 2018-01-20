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

    public void Pre_treatment(Mat src){
        dst = new Mat();
        if(src.channels() == 3)
            Imgproc.cvtColor(src,dst,Imgproc.COLOR_RGB2GRAY);
        else if(src.channels() == 4)
            Imgproc.cvtColor(src,dst,Imgproc.COLOR_RGBA2GRAY);

        //TODO：适配各种图片，resize合适的大小
        if(dst.cols() >= 1500 && dst.rows() >= 2000) {
            Size smallsize = new Size(1000, 1000 * dst.rows() / dst.cols());
            Imgproc.resize(dst, dst, smallsize, 0, 0, Imgproc.INTER_LINEAR);
        }
        Size gaussiansize = new Size(3, 3);
        Imgproc.GaussianBlur(dst,dst, gaussiansize,0);
        Imgproc.Sobel(dst,dst,CvType.CV_8UC1,1,0);

        //TODO：寻找开闭运算的最佳内核以及最佳处理顺序
        Size ksize_open = new Size(3, 3);
        Size ksize_close = new Size(7, 7);
        Point anchor = new Point(-1,-1);
        Mat element_open = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_open);
        Mat element_close = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_close);
        Imgproc.morphologyEx(dst,dst,Imgproc.MORPH_OPEN,element_open,anchor,1);
        Imgproc.morphologyEx(dst,dst,Imgproc.MORPH_CLOSE,element_close,anchor,2);
        this.dst = dst;
    }

}
