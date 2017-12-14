package com.zhang.editview.ex;

/**
 * Created by zhang on 2017/11/1.
 */

public class ImportEmptyException extends Exception {
    public ImportEmptyException() {
        super("内容为空的异常");
    }

}
