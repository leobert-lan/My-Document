package com.lht.cloudjob.annotation.antideadcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package</b> com.lht.cloudjob.annotation
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TempVersion
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/22.
 */
//1.CONSTRUCTOR:用于描述构造器
//2.FIELD:用于描述域
//        　　　　3.LOCAL_VARIABLE:用于描述局部变量
//        　　　　4.METHOD:用于描述方法
//        　　　　5.PACKAGE:用于描述包
//        　　　　6.PARAMETER:用于描述参数
//        　　　　7.TYPE:用于描述类、接口(包括注解类型) 或enum声明
@Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface TempVersion {
    TempVersionEnum  value() default TempVersionEnum.UNKNOWN;
}
