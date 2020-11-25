package cn.altira.android.rongu2.util;

import cn.altira.android.rongu2.pojo.Result;

public class ResultUtil {
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(200);
        result.setMsg("success");
        result.setData(object);
        return result;
    }

    public static Result success(){
        Result result = new Result();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

    public static Result success(Integer code,Object object){
        Result result = new Result();
        result.setCode(code);
        result.setMsg("success");
        result.setData(object);
        return result;
    }

    public static Result success(Integer code){
        Result result = new Result();
        result.setCode(code);
        result.setMsg("success");
        return result;
    }

    public static Result error(Integer code, String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
