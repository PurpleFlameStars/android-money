package com.cashLoan.money.upload.option;

public class LemonUploadOptions {

    private IUploadCancel cancelListener;

    public LemonUploadOptions(IUploadCancel cancel) {
        cancelListener = cancel;
    }

    public IUploadCancel getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(IUploadCancel cancelListener) {
        this.cancelListener = cancelListener;
    }
}
