package com.akaxin.common.executor.chain.handler;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-28 18:47:49
 * @param <T>
 *            执行参数的类型
 * @param <R>
 *            返回类型的参数
 */
public interface IHandler<T, R> {
	public R handle(T t);
}
