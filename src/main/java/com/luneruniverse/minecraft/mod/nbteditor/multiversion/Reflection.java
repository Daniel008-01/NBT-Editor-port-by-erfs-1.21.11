package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class Reflection {
	
	public static final MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
	
	
	private static final Cache<String, Class<?>> classCache = CacheBuilder.newBuilder().build();
	public static Class<?> getClass(String name) {
		try {
			return classCache.get(name, () -> Class.forName(mappings.mapClassName("intermediary", name)));
		} catch (ExecutionException | UncheckedExecutionException e) {
			throw new RuntimeException("Error getting class", e);
		}
	}
	public static Supplier<Class<?>> getOptionalClass(String name) {
		return jitSupplier(() -> getClass(name));
	}
	
	
	public static <T> T newInstance(Class<T> clazz, Class<?>[] parameters, Object... args) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor(parameters);
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (Exception e) {
			throw new RuntimeException("Error creating new instance of class", e);
		}
	}
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String clazz, Class<?>[] parameters, Object... args) {
		return (T) newInstance(getClass(clazz), parameters, args);
	}
	public static <E extends Throwable, T> T newInstanceThrowable(Class<E> possibleException, Class<T> clazz, Class<?>[] parameters, Object... args) throws E {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor(parameters);
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (Exception e) {
			if (e instanceof InvocationTargetException thrownE && possibleException.isInstance(thrownE.getCause()))
				throw possibleException.cast(thrownE.getCause());
			throw new RuntimeException("Error creating new instance of class", e);
		}
	}
	@SuppressWarnings("unchecked")
	public static <E extends Throwable, T> T newInstanceThrowable(Class<E> possibleException, String clazz, Class<?>[] parameters, Object... args) throws E {
		return (T) newInstanceThrowable(possibleException, getClass(clazz), parameters, args);
	}
	
	
	public static class FieldReference {
		private final Field field;
		public FieldReference(Field field) {
			this.field = field;
		}
		public void set(Object obj, Object value) {
			try {
				field.set(obj, value);
			} catch (Exception e) {
				throw new RuntimeException("Error setting field", e);
			}
		}
		@SuppressWarnings("unchecked")
		public <T> T get(Object obj) {
			try {
				return (T) field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("Error getting field value", e);
			}
		}
	}
	
	private static String getFieldName(Class<?> clazz, String field, String descriptor) {
		return mappings.mapFieldName("intermediary", mappings.unmapClassName("intermediary", clazz.getName()), field, descriptor);
	}
	public static FieldReference getField(Class<?> clazz, String field, String descriptor) {
		try {
			Field fieldObj = clazz.getDeclaredField(getFieldName(clazz, field, descriptor));
			fieldObj.setAccessible(true);
			return new FieldReference(fieldObj);
		} catch (Exception e) {
			throw new RuntimeException("Error getting field", e);
		}
	}
	public static Supplier<FieldReference> getOptionalField(Supplier<Class<?>> clazz, Supplier<String> field, Supplier<String> descriptor) {
		return jitSupplier(() -> getField(clazz.get(), field.get(), descriptor.get()));
	}
	public static Supplier<FieldReference> getOptionalField(Class<?> clazz, String field, String descriptor) {
		return jitSupplier(() -> getField(clazz, field, descriptor));
	}
	
	
	public static class MethodInvoker {
		private final Method method;
		public MethodInvoker(Class<?> clazz, String method, MethodType type) throws Exception {
			Method resolved = null;
			Class<?>[] params = type.parameterArray();
			try {
				resolved = clazz.getDeclaredMethod(method, params);
			} catch (NoSuchMethodException e) {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getParameterCount() != params.length) continue;
					Class<?>[] mParams = m.getParameterTypes();
					boolean paramsMatch = true;
					for (int i = 0; i < params.length; i++) {
						if (!(mParams[i].isAssignableFrom(params[i]) || params[i].isAssignableFrom(mParams[i]))) {
							paramsMatch = false;
							break;
						}
					}
					if (!paramsMatch) continue;
					if (!type.returnType().isAssignableFrom(m.getReturnType())) continue;
					resolved = m;
					break;
				}
			}
			if (resolved == null) {
				throw new NoSuchMethodException("Could not find method " + method + " with descriptor " + getIntermediaryDescriptor(type) + " in " + clazz.getName());
			}
			this.method = resolved;
			this.method.setAccessible(true);
		}
		@SuppressWarnings("unchecked")
		public <T> T invoke(Object obj, Object... args) {
			try {
				return (T) method.invoke(obj, args);
			} catch (Exception e) {
				throw new RuntimeException("Error invoking method", e);
			}
		}
		@SuppressWarnings("unchecked")
		public <E extends Throwable, T> T invokeThrowable(Class<E> possibleException, Object obj, Object... args) throws E {
			try {
				return (T) method.invoke(obj, args);
			} catch (Exception e) {
				if (e instanceof InvocationTargetException thrownE && possibleException.isInstance(thrownE.getCause()))
					throw possibleException.cast(thrownE.getCause());
				throw new RuntimeException("Error invoking method", e);
			}
		}
	}
	
	private static String getIntermediaryDescriptor(MethodType type) {
		StringBuilder output = new StringBuilder("(");
		for (Class<?> param : type.parameterArray())
			output.append(getIntermediaryDescriptor(param));
		output.append(")");
		output.append(getIntermediaryDescriptor(type.returnType()));
		return output.toString();
	}
	private static String getIntermediaryDescriptor(Class<?> clazz) {
		String descriptor = clazz.descriptorString();
		StringBuilder arrays = new StringBuilder();
		int typeStart = 0;
		while (descriptor.charAt(typeStart) == '[') {
			arrays.append('[');
			clazz = clazz.componentType();
			typeStart++;
		}
		if (descriptor.charAt(typeStart) == 'L')
			return arrays + "L" + mappings.unmapClassName("intermediary", clazz.getName()).replace('.', '/') + ";";
		else
			return descriptor;
	}
	
	public static String getMethodName(Class<?> clazz, String method, MethodType type) {
		return mappings.mapMethodName("intermediary", mappings.unmapClassName("intermediary", clazz.getName()), method, getIntermediaryDescriptor(type));
	}
	public static MethodInvoker getMethod(Class<?> clazz, String method, MethodType type) {
		try {
			return new MethodInvoker(clazz, getMethodName(clazz, method, type), type);
		} catch (Exception e) {
			throw new RuntimeException("Error getting method", e);
		}
	}
	public static Supplier<MethodInvoker> getOptionalMethod(Supplier<Class<?>> clazz, Supplier<String> method, Supplier<MethodType> type) {
		return jitSupplier(() -> getMethod(clazz.get(), method.get(), type.get()));
	}
	public static Supplier<MethodInvoker> getOptionalMethod(Class<?> clazz, String method, MethodType type) {
		return jitSupplier(() -> getMethod(clazz, method, type));
	}
	
	
	private static <T> Supplier<T> jitSupplier(Supplier<T> supplier) {
		return new Supplier<>() {
			private T value;
			@Override
			public T get() {
				if (value == null)
					value = supplier.get();
				return value;
			}
		};
	}
	
}
