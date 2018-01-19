package com.wangbin.lpr_opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
        Imgproc.cvtColor(src,dst,Imgproc.COLOR_RGB2GRAY);
        Size gaussiansize = new Size(3,3);
        Imgproc.GaussianBlur(dst,dst, gaussiansize,0);
        if(dst.cols() >= 1000 && dst.rows() >= 1000) {
            Size smallsize = new Size(dst.cols()/2,dst.rows()/2);
            Imgproc.resize(dst,dst,smallsize,0,0,Imgproc.INTER_LINEAR);
        }
        Imgproc.Sobel(dst,dst,CvType.CV_8UC1,1,0);
        this.dst = dst;
    }

}
