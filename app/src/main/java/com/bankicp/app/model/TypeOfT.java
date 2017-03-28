package com.bankicp.app.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class TypeOfT<X> implements ParameterizedType {
	private Class<?> wrapped;

	public TypeOfT(Class<X> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return new Type[] { wrapped };
	}

	@Override
	public Type getRawType() {
		return List.class;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}
}
