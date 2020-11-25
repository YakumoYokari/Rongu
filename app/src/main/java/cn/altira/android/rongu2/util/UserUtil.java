package cn.altira.android.rongu2.util;

import java.util.regex.Pattern;

public class UserUtil {
    public static boolean isOnlyCharAndNum(String Pwd){   //判断只能包含字母和数字
        for(int i=0; i<Pwd.length(); i++){
            if(!Character.isLetter(Pwd.charAt(i)) &&   //字符串中的i对应字符判断是否是字母
                    !Character.isDigit(Pwd.charAt(i))){  //字符串中的i对应字符判断是否是数字
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    public static boolean isLegalUserName(String username){    //判断用户名只能为字母、数字和汉字
        String all = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matches(all,username);
    }

    public static boolean isThanSixCharacter(String Pwd){  //判断是否少于8个字符
        if(Pwd.length() < 6){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isLowerSixthCharacter(String Pwd){  //判断是否少于8个字符
        if(Pwd.length() > 16){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isLowerTenCharacter(String Pwd){  //判断是否少于8个字符
        if(Pwd.length() > 10){
            return false;
        }else{
            return true;
        }
    }
}
