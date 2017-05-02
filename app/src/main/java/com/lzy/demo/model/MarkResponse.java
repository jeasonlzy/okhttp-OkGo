package com.lzy.demo.model;

import java.io.Serializable;

/**
 * Description：
 * Author：AstroGypsophila
 * GitHub：https://github.com/AstroGypsophila
 * Date：2017/5/2
 */
public class MarkResponse<T> implements Serializable {

    private static final long serialVersionUID = -6883067139985941173L;
    public boolean error;
    public T results;
}