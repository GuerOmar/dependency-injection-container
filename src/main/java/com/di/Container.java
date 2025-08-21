package com.di;

import com.di.annotation.component.Component;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.lang.reflect.*;

/**
 * A lightweight Dependency Injection container that provides automatic component scanning
 * and eager initialization of services.
 *
 * <p>This container scans packages for classes annotated with {@code @Component} and
 * automatically registers them with their implemented interfaces. All discovered
 * components are eagerly initialized after the scanning phase.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic component scanning by package</li>
 *   <li>Interface-based service registration</li>
 *   <li>Constructor dependency injection</li>
 *   <li>Eager initialization of all services</li>
 *   <li>Circular dependency detection</li>
 * </ul>
 *
 */
public class Container {

    // interface -> implementation mappings
    private final Map<Class<?>, Class<?>> implementationPerInterface = new HashMap<>();

    // Cache created instances
    private final Map<Class<?>, Object> createdInstances = new HashMap<>();

    // Track what we're currently creating
    private final Set<Class<?>> creationStack = new HashSet<>();

    /**
     * Scans the specified package for classes annotated with {@code @Component}
     * and automatically registers them with their implemented interfaces.
     *
     * <p>
     * After scanning, all discovered components are eagerly initialized
     * to ensure fast runtime access and early error detection.
     * </p>
     *
     * @param packageName the package to scan
     * @throws RuntimeException if the package cannot be found or scanning fails
     */
    public void scanPackage(String packageName) {
        try {
            System.out.println("[CONTAINER] Starting component scan for package: " + packageName);
            // Convert package name to file path
            String path = packageName.replace('.', '/');

            // Get the directory URL
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                throw new RuntimeException("Package not found: " + packageName);
            }

            File directory = new File(resource.getFile());
            scanDirectory(directory, packageName);
            // We create all the instances eagerly
            initializeAll();
            System.out.println("[CONTAINER] Component scan completed for package: " + packageName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }
    }

    /**
     * Retrieves a previously initialized instance of the specified type.
     *
     * <p>
     * This method only returns instances that were discovered during
     * package scanning and have been eagerly initialized.
     * </p>
     *
     * @param <T> the type of the instance to retrieve
     * @param type the class representing the interface
     * @return the instance of the specified type
     * @throws IllegalArgumentException if type is null or not registered
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        if (!createdInstances.containsKey(type)) {
            throw new IllegalArgumentException("Type isn't registered: " + type.getSimpleName());
        }

        return (T) createdInstances.get(type);
    }

    /**
     * Recursively scans a directory for .class files and registers
     * any classes annotated with {@code @Component}.
     *
     * @param directory the directory to scan
     * @param packageName the current package name being scanned
     * @throws ClassNotFoundException if a class file cannot be loaded
     */
    private void scanDirectory(File directory, String packageName) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // Found a class file, check if it's a component
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    // Get all interfaces this class implements
                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> interfaceClass : interfaces) {
                        register(interfaceClass, clazz);
                        System.out.println("[CONTAINER] Registered: " + interfaceClass.getSimpleName() + " -> " + clazz.getSimpleName());
                    }
                }
            }
        }
    }

    /**
     * Registers an interface-implementation mapping in the container.
     *
     * @param interfaceType the interface class
     * @param implementationType the implementation class
     * @throws IllegalArgumentException if either parameter is null
     */
    private void register(Class<?> interfaceType, Class<?> implementationType) {
        if (interfaceType == null || implementationType == null) {
            throw new IllegalArgumentException("Interface and implementation types cannot be null");
        }
        implementationPerInterface.put(interfaceType, implementationType);
    }

    /**
     * Eagerly initializes all registered interface mappings by creating
     * instances of their corresponding implementation classes.
     *
     * <p>
     * This method is called automatically after package scanning
     * to ensure all services are ready for use.
     * </p>
     */
    private void initializeAll() {
        for (Class<?> type : implementationPerInterface.keySet()) {
            initializeInterface(type);
        }
    }

    /**
     * Initializes a single interface by creating an instance of its
     * registered implementation class, including all required dependencies.
     *
     * @param <T> the interface type
     * @param type the interface class to initialize
     * @throws RuntimeException if circular dependency is detected
     * @throws IllegalArgumentException if the type is not registered
     *                         or if circular dependency detected
     *
     */
    private <T> void initializeInterface(Class<T> type) {
        // Check if we already have an instance
        if (createdInstances.containsKey(type)) {
            return;
        }

        // Circular dependency check
        if (creationStack.contains(type)) {
            throw new RuntimeException("Circular dependency detected for: " + type.getSimpleName());
        }

        // Determine which class to instantiate
        Class<?> implementationClass = implementationPerInterface.get(type);

        if (implementationClass == null) {
            throw new IllegalArgumentException("Type isn't registered: " + type.getSimpleName());
        }

        creationStack.add(implementationClass);
        T instance = createInstance(implementationClass);
        createdInstances.put(type, instance);
        creationStack.remove(implementationClass);
    }

    /**
     * Creates an instance of the specified implementation class using
     * constructor injection to resolve dependencies.
     *
     * @param <T> the return type
     * @param implementationClass the implementation class to instantiate type
     * @return the created instance with all dependencies injected
     * @throws RuntimeException if the class is not annotated with @Component
     *                         or if instance creation fails
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<?> implementationClass) {
        if (!implementationClass.isAnnotationPresent(Component.class)) {
            throw new RuntimeException(implementationClass.getSimpleName() + " is not annotated with @Component");
        }

        try {
            // We suppose that there is only one constructor
            Constructor<?> constructor = implementationClass.getDeclaredConstructors()[0];

            // Resolve constructor parameters
            Object[] parameters = resolveParameters(constructor);

            // Create the instance
            return (T) constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + implementationClass.getSimpleName(), e);
        }
    }

    /**
     * Resolves constructor parameters by recursively initializing
     * the required dependency types.
     *
     * @param constructor the constructor whose parameters need to be resolved
     * @return an array of initialized parameter instances
     */
    private Object[] resolveParameters(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            initializeInterface(paramType);
            parameters[i] = getInstance(paramType);
        }

        return parameters;
    }
}