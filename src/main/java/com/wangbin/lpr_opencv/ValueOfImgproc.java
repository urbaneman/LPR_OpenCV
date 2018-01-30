package com.wangbin.lpr_opencv;

import org.opencv.core.CvType;

/**
 * Created by dell-pc on 2018/1/29.
 */

public final class ValueOfImgproc {
    public static final int gaussiansize_width = 3, gaussiansize_height = 3;

    public static final int element_open_width = 9, element_open_height = 9;

    public static final int morphologyEx_open_iterations = 1, morphologyEx_close_iterations = 1;

    public static final int element_close_width = 19, element_close_height = 7;

    public static final int inputImg_maxWidth = 1080, inputImg_maxHeight = 1080;

    public static final int resizeImg_width = 720, resizeImg_height = 1080;

    public static final double ratio_img_4_3_min = 1.33, ratio_img_4_3_max = 1.34,
                               ratio_img_16_9_min = 1.77,ratio_img_16_9_max = 1.78;

    public static final int contour_minArea = 1000;


    //public static final double contour_minAngle = -Math.PI/6, contour_maxAngle = Math.PI/6;

    public static final double contour_LayDown_minAngle = -90, contour_LayDown_maxAngle = -60,
                                contour_LayUp_minAngle = -30, contour_LayUp_maxAngle = 0;

    public static final double contour_minSizeRatio_small = 3.14, contour_maxSizeRatio_samll = 6.0,
                                contour_minSizeRatio_big = 2.0,contour_maxSizeRatio_big = 3.0;

}
