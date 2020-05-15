package com.cashLoan.money.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cashLoan.money.R;
import com.cashLoan.money.base.activity.BunBaseActivity;
import com.cashLoan.money.ui.bean.DetectorInfo;
import com.cashLoan.money.utils.MoneyConfig;
import com.dzfd.gids.baselibs.fresco.FrescoImageLoaderHelper;
import com.dzfd.gids.baselibs.utils.BunToast;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.Serializable;

public class OcrNextActivity extends BunBaseActivity implements View.OnClickListener {
    private static final String TAG = "OcrNextActivity";
    private SimpleDraweeView frontImg;

    private DetectorInfo ocrData;
    private EditText kfcNombre ;
    private EditText kfcPaterno ;
    private EditText kfcMaterno ;
    private EditText kfcCurp ;
    private EditText kfcRfc ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra(MoneyConfig.KFC_DATA);
        if (data instanceof DetectorInfo){
            ocrData= (DetectorInfo) data;
        }
        if (ocrData==null) return;



        View head = findViewById(R.id.head);
        head.findViewById(R.id.lift_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView middleTitle = head.findViewById(R.id.middle_title);
        middleTitle.setText(getString(R.string.ine_ife_verfication));
         frontImg = findViewById(R.id.kyc_front_img);
        TextView nextOk = findViewById(R.id.kyc_next_ok);
        nextOk.setOnClickListener(this);

        kfcNombre = findViewById(R.id.kfc_next_nombre);
        kfcPaterno = findViewById(R.id.kfc_next_apellido_paterno);
        kfcMaterno = findViewById(R.id.kfc_next_apellido_materno);
        kfcCurp = findViewById(R.id.kfc_next_curp);
        kfcRfc = findViewById(R.id.kfc_next_rfc);


        String imagePath = ocrData.getFrontImgFilePath();
        if (!TextUtils.isEmpty(imagePath)){
            FrescoImageLoaderHelper.setImageByFilePath(frontImg,imagePath);
        }
        String name = ocrData.getName();
        if (!TextUtils.isEmpty(name)){
            kfcNombre.setText(name);
        }
        String paterno = ocrData.getName1();//名
        if (!TextUtils.isEmpty(paterno)){
            kfcPaterno.setText(paterno);
        }
        String materno = ocrData.getName2();//姓
        if (!TextUtils.isEmpty(materno)){
            kfcMaterno.setText(materno);
        }
        String curp = ocrData.getIdnum();
        if (!TextUtils.isEmpty(curp)){
            kfcCurp.setText(curp);
        }
        String rfc = ocrData.getClave();
        if (!TextUtils.isEmpty(rfc)){
            kfcRfc.setText(rfc);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.kyc_next_ok:{
                if (ocrData!=null){
                    if (submitCheckInput()){

                    }
                }else {
                    BunToast.showShort(OcrNextActivity.this,getString(R.string.please_information));
                }
                break;
            }
        }
    }

    /**
     * 点击提交的时候的提示
     */
    private boolean submitCheckInput() {
        //昵称
        String nick = kfcNombre.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            BunToast.showShort(getApplicationContext(), getString(R.string.please_input_nombre));
            return false;
        }

        String paterno = kfcPaterno.getText().toString().trim();
        if (TextUtils.isEmpty(paterno)) {
            BunToast.showShort(getApplicationContext(), getString(R.string.please_input_apellido_paterno));
            return false;
        }
        String materno = kfcMaterno.getText().toString().trim();
        if (TextUtils.isEmpty(materno)) {
            BunToast.showShort(getApplicationContext(), getString(R.string.please_input_apellido_materno));
            return false;
        }
        String curp = kfcCurp.getText().toString().trim();
        if (TextUtils.isEmpty(curp)) {
            BunToast.showShort(getApplicationContext(), getString(R.string.please_input_curp));
            return false;
        }
        String rfc = kfcRfc.getText().toString().trim();
        if (TextUtils.isEmpty(rfc)) {
            BunToast.showShort(getApplicationContext(), getString(R.string.please_input_rfc));
            return false;
        }
        return true;
    }

    @Override
    public int getFragmentcontainerViewId() {
        return 0;
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_ocr_next;
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
