package com.wangbin.lpr_opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

    //输入图像的预处理过程
    public void Pre_treatment(){
        //调整图像大小
        resize(src);
        Mat img_pro = new Mat();
        //高斯模糊
        Size gaussiansize = new Size(ValueOfImgproc.gaussiansize_width, ValueOfImgproc.gaussiansize_height);
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
        Size ksize_open = new Size(ValueOfImgproc.element_open_width, ValueOfImgproc.element_open_height);
        Size ksize_close = new Size(ValueOfImgproc.element_close_width, ValueOfImgproc.element_close_height);
        Point anchor = new Point(-1,-1);
        Mat element_open = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_open);
        Mat element_close = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,ksize_close);
        Imgproc.morphologyEx(img_pro,img_pro,Imgproc.MORPH_CLOSE,element_close,anchor,ValueOfImgproc.morphologyEx_close_iterations);
        Imgproc.morphologyEx(img_pro,img_pro,Imgproc.MORPH_OPEN,element_open,anchor,ValueOfImgproc.morphologyEx_open_iterations);
        this.dst = img_pro;
    }

    //调整输入图像的大小
    //针对手机虚假分辨率，将调整从手机直接拍摄获得的车牌图片
    //一般手机采集图像尺寸比例为4:3; 16:9和1:1三种
    //所以根据这三种尺寸的图像进行缩放至宽度为1080
    public void resize(Mat input){
        //如果宽度大于1080
        if(input.cols()>ValueOfImgproc.inputImg_maxWidth){
            Mat img_resize = new Mat();
            double rows = input.rows();
            double ratio = rows / input.cols();
            if(ratio>=ValueOfImgproc.ratio_img_4_3_min && ratio<=ValueOfImgproc.ratio_img_4_3_max
                    || ratio == 1.0
                    || ratio>= ValueOfImgproc.ratio_img_16_9_min && ratio <= ValueOfImgproc.ratio_img_16_9_max){
                Size smallsize = new Size(540, 540 * input.rows() / input.cols());
                Imgproc.resize(input, img_resize, smallsize, 0, 0, Imgproc.INTER_LINEAR);
                this.setSrc(img_resize);
            }
        }else {
            return;
        }
    }

    //车牌区域检索
    public void findAreaofLP(){
        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint> contours_like = new ArrayList<>();
        Mat dst_copy = new Mat();
        dst_copy = dst.clone();
        Imgproc.findContours(dst_copy, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Vector<RotatedRect> rects = new Vector<RotatedRect>();
        //TODO: 进一步进行轮廓剔除
        for(int idx = 0;idx < contours.size(); ++idx) {
            Mat contour = contours.get(idx);
            MatOfPoint2f contour2f = new MatOfPoint2f();
            contour.convertTo(contour2f, CvType.CV_32F);
            double contoursArea = Imgproc.contourArea(contour);
            RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);
            double ratio_size = rotatedRect.size.width / rotatedRect.size.height;
            if (contoursArea > ValueOfImgproc.contour_minArea) {
                System.out.print(rotatedRect.angle);
                if (ratio_size > ValueOfImgproc.contour_minSizeRatio_small &&
                        ratio_size < ValueOfImgproc.contour_maxSizeRatio_samll &&
                        rotatedRect.angle > ValueOfImgproc.contour_minAngle &&
                        rotatedRect.angle < ValueOfImgproc.contour_maxAngle) {
                    rects.add(rotatedRect);
                    contours_like.add(contours.get(idx));
                }
            }
        }
        contours.clear();
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_GRAY2RGB);
        Scalar green = new Scalar(0,255,0);
        //Imgproc.drawContours(src, contours_like, -1, green, 2);
        Imgproc.drawContours(dst, contours_like, -1, green, 2);
    }
}
