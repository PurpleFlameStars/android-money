package com.cashLoan.money.upload;

public interface UploadConst {
    public interface Code {
        int SUCCESS = 1;
        int FILE_NOT_EXIST = -1;
        int TOKEN_ERROR = -2;
        int UPLOAD_QINNIU_FAIL = -3;
        int UPLOAD_MANAGER_NULL = -4;
    }
}
