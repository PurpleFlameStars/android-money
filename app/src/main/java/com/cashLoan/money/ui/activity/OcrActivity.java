package com.cashLoan.money.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ai.ocr.identity.IdentityActivity;
import com.ai.ocr.identity.bean.DetectorResultInfo;
import com.ai.ocr.identity.config.DetectionConfig;
import com.cashLoan.money.JumpHelper;
import com.cashLoan.money.R;
import com.cashLoan.money.base.activity.BunBaseActivity;
import com.cashLoan.money.ui.bean.DetectorInfo;
import com.cashLoan.money.upload.IUploadStatusListener;
import com.cashLoan.money.upload.UploaderExecutor;
import com.cashLoan.money.utils.SingleClickUtil;
import com.dzfd.gids.baselibs.fresco.FrescoImageLoaderHelper;
import com.dzfd.gids.baselibs.utils.BunToast;
import com.dzfd.gids.baselibs.utils.DeviceUtils;
import com.dzfd.gids.baselibs.utils.ImageUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.io.File;

public class OcrActivity extends BunBaseActivity implements View.OnClickListener {
    private static final String TAG = "OcrActivity";
    private int OCR_CODE=200;
    private SimpleDraweeView frontImg;
    private SimpleDraweeView rearImg;
    private DetectionConfig.Builder builder;

    private boolean isFront=true;

    private TextView frontHint;
    private TextView rearHint;
    private ImageView frontCamera;
    private ImageView rearCamera;
    private DetectorInfo ocrData;

    private File frontImgPath;
    private File backImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
       View head = findViewById(R.id.head);
        head.findViewById(R.id.lift_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView middleTitle = head.findViewById(R.id.middle_title);
        middleTitle.setText(getString(R.string.ine_ife_verfication));
        int widthPixels= DeviceUtils.getDisplayMetrics(this).widthPixels;
        RelativeLayout frontLy = findViewById(R.id.kyc_front_ly);
        frontLy.setOnClickListener(this);
         frontImg = findViewById(R.id.kyc_front_img);

        RelativeLayout rearLy = findViewById(R.id.kyc_rear_ly);
        rearLy.setOnClickListener(this);
        rearImg = findViewById(R.id.kyc_rear_img);

        frontHint = findViewById(R.id.kyc_front_hint);
        rearHint = findViewById(R.id.kyc_rear_hint);
        frontCamera = findViewById(R.id.kyc_front_camera);
        rearCamera = findViewById(R.id.kyc_rear_camera);

        TextView nextOk = findViewById(R.id.kyc_next_ok);
        nextOk.setOnClickListener(this);
    }
    private void SetViewSizeByRadio(View view,int width,int height,int targetwidth){
        if(view == null || height==0 || width==0){
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width=targetwidth;
        layoutParams.height=(height*targetwidth)/width;
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OCR_CODE && resultCode == RESULT_OK) {
            DetectorResultInfo infos = (DetectorResultInfo)
            data.getSerializableExtra("detector_result");
            if (infos==null) return;
            Gson gson = new Gson();
            String infoJson = gson.toJson(infos);
            DetectorInfo info = gson.fromJson(infoJson, DetectorInfo.class);
            if (info != null) {
                if (isFront){
                    if (info.isSuccess()) {
                        ocrData = info;
                        String imagePath = info.getImageFilePath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            FrescoImageLoaderHelper.setImageByFilePath(frontImg,imagePath);
                            if (saveImage(imagePath,1)){
                                if (frontImgPath!=null){
                                    ocrData.setFrontImgFilePath(frontImgPath.getPath());
                                }
                            }
                            //frontImgPath=imagePath;
                            //uploadPic(imagePath,1);

                            frontHint.setVisibility(View.GONE);
                            frontCamera.setVisibility(View.GONE);
                        }
                    } else {
                        BunToast.showShort(OcrActivity.this,getString(R.string.identification_of_failure));
                    }

                }else {
                    if (info.isSuccess()){
                        BunToast.showShort(this,getString(R.string.ine_ife_rear_hint));
                        return;
                    }
                    String imagePath = info.getImageFilePath();
                    if (!TextUtils.isEmpty(imagePath)) {
                        FrescoImageLoaderHelper.setImageByFilePath(rearImg,imagePath);
                        if (saveImage(imagePath,2)){
                            if (backImgPath!=null){
                                ocrData.setBackImgFilePath(backImgPath.getPath());
                            }
                        }
                        //backImgPath=imagePath;
                        //uploadPic(imagePath,2);
                        rearHint.setVisibility(View.GONE);
                        rearCamera.setVisibility(View.GONE);
                    }else {
                        BunToast.showShort(OcrActivity.this,getString(R.string.identification_of_failure));
                    }
                }
            } else {
                BunToast.showShort(OcrActivity.this,getString(R.string.identification_of_failure));
            }
        }
    }




    @Override
    public void onClick(View v) {
        if (SingleClickUtil.isFastDoubleClick(2000)) {
            return;
        }
        switch (v.getId()){
            case R.id.kyc_front_ly:{
                isFront=true;
                startOcr();
                break;
            }
            case R.id.kyc_rear_ly:{
                isFront=false;
                startOcr();
                break;
            }
            case R.id.kyc_next_ok:{
                if (ocrData!=null && !TextUtils.isEmpty(ocrData.getFrontImgFilePath()) && !TextUtils.isEmpty(ocrData.getBackImgFilePath())){
                    JumpHelper.jumpOcrNextActivity(this,ocrData);
//                    if (!TextUtils.isEmpty(ocrData.getFrontImgUrl()) && !TextUtils.isEmpty(ocrData.getBackImgUrl())){
//                        JumpHelper.jumpOcrNextActivity(this,ocrData);
//                    }else {
//                        if (TextUtils.isEmpty(ocrData.getFrontImgUrl())){
//                            if (!TextUtils.isEmpty(frontImgPath)){
//                                uploadPic(frontImgPath,1);
//                            }else {
//                                BunToast.showShort(this,getString(R.string.ine_ife_front_hint));
//                            }
//                        }
//                        if (TextUtils.isEmpty(ocrData.getBackImgUrl())){
//                            if (!TextUtils.isEmpty(backImgPath)){
//                                uploadPic(backImgPath,1);
//                            }else {
//                                BunToast.showShort(this,getString(R.string.ine_ife_rear_hint));
//                            }
//                        }
//
//                        return;
//                    }

                }else {
                    BunToast.showShort(OcrActivity.this,getString(R.string.please_information));
                }
                break;
            }
        }
    }

    private void startOcr(){

            builder = new DetectionConfig.Builder();
            //builder.setLanguage("es"); //设置语言，语言和国家默认跟随手机设置
            //builder.setCountry("MX");//设置国家，语言和国家默认跟随手机设置
            //设置状态栏颜色
            builder.setStatusBarColor(getResources().getColor(R.color.black_50));
            IdentityActivity.startIdentityDetection(this,
                    "5764621b-0477-4485-a1bc-ae09570741ff",//商户id
                    "2X5WxLEQaMegJO1FECcZKlqoSkWgNzSe",//商户密钥
                    builder.build(),OCR_CODE);

    }


    private void uploadPic(String path,int type){
        if(TextUtils.isEmpty(path)) return;

        UploaderExecutor.getInstance().uploadPic("money", new File(path), new IUploadStatusListener() {
            @Override
            public void uploadSuccess(String resultUrl) {
             if (ocrData==null || TextUtils.isEmpty(resultUrl)) return;
            if (1==type){
                ocrData.setFrontImgUrl(resultUrl);
            }else {
                ocrData.setBackImgUrl(resultUrl);
            }
            }

            @Override
            public void uploadFail(int code, String msg) {

            }
        });
    }

    public boolean saveImage(String path,int type) {
        String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/photo";
        File rootFile = new File(imgPath);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        boolean result;
        if (1==type){
            frontImgPath = new File(rootFile, "front.jpg");
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return false;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
             result = ImageUtils.save(bitmap, frontImgPath, Bitmap.CompressFormat.JPEG, true);
        }else {
            backImgPath = new File(rootFile, "back.jpg");
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return false;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            result = ImageUtils.save(bitmap, backImgPath, Bitmap.CompressFormat.JPEG, true);
        }


        return result;
    }


    @Override
    public int getFragmentcontainerViewId() {
        return 0;
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_ocr;
    }

    @Override
    public Fragment getMainFragment() {
        return null;
    }

    @Override
    public boolean isShowImmerseLayout() {
        return false;
    }


}
