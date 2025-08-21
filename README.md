# Dependency Injection Container

At work, I’ve been involved in a project that defines its own services using the **Context pattern**.  
It got me curious about what frameworks like **Spring** do under the hood.

That question led me down the path of exploring **Dependency Injection** in depth.  
To really understand it, I decided to build my own **Dependency Injection container** from scratch.  
Along the way, I learned not only how Spring might be doing things internally, but also the trade-offs between different approaches.

## 🧩 About
A small Dependency Injection container written in Java that supports:

- 🔍 **Automatic component scanning** by package
- 🧩 **Interface-to-implementation registration**
- 🪄 **Constructor-based dependency injection**
- ⚡ **Eager initialization** for fast runtime access
- 🌀 **Circular dependency detection**

This project was built as a learning exercise to dive deeper into concepts that large frameworks usually abstract away.  
It doesn’t provide all the features of something like Spring, but it demonstrates the core building blocks of a Dependency Injection system.

## ✨ Features
- 📦 Scans packages for classes annotated with `@Component`
- 🔗 Resolves dependencies using constructor injection
- 🚨 Detects circular dependencies
- 🏗️ Caches and reuses created instances
- ⚡ Initializes all components eagerly at startup

## 🛠️ Tech Stack
- ☕ Java 21
