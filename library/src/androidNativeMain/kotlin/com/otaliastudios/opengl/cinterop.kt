@file:Suppress("NOTHING_TO_INLINE")

package com.otaliastudios.opengl

import kotlinx.cinterop.*

/**
 * C raw data: encapsulated in [CPointed]. It has three subclasses:
 * - [CFunction], representing a C function (that is, C code)
 * - [CVariable], representing a C variable located in memory
 * - [COpaque], representing something that is not a function or a variable.
 *
 * The [CVariable] is the most interesting one. It has a few direct subclasses:
 * - [CPrimitiveVar] for primitive types (byte, int, ...)
 * - [CStructVar] for C structs
 * - [CPointerVar] for C pointers. When a pointer is a long, this class would be similar to a [LongVar].
 *   It represents a pointer value located in memory (which means that we could get a pointer to the pointer).
 *
 * The [CPrimitiveVar] is the most interesting [CVariable]. It has many implementation:
 * [BooleanVar], [IntVar], [UIntVar], [FloatVar] and so on. So when we have a *Var, we are
 * managing a direct reference to that object's memory. We can simply use [CVariable.getRawPointer]
 * to access the raw memory address of this object.
 *
 * ----
 *
 * C pointers: encapsulated in [CValuesRef]. As per docs, "Represents a reference to sequence of C values."
 * This class has 2 important direct subclasses:
 * - [CPointer] is simply a C pointer to a C var. In this case, to access the raw value (a [CPointed]:
 *   that is, [CFunction], [CPrimitiveVar], [CStructVar], ...) one can simply call [CPointer.pointed].
 *   - Being a C Pointer, it also represents an array fo vars, so it has a "get" operator!
 *   - Moreover, one can use the overloaded "plus" operator to move the pointer up or down.
 * - [CValues] is an immutable sequence of C values.
 *   Passing CValues to native methods will make a copy of it and modifications will not be available
 *   to the caller once the function returns.
 *
 * This means that [CPointer] is the friendly reference type, while [CValues] is less frequently used.
 * To obtain a [CPointer] out of a ref, one can simply use [CPointed.ptr] to create one.
 * On the other hand, to obtain the pointed value, as said, there is [CPointer.pointed].
 *
 * ----
 *
 * Anytime we have to deal with cinterop types as the ones described above, we must deal with C memory
 * as well. It seems to me that there are two options:
 * - use [memScoped] blocks: it offers allocation functions and will automatically release them
 * - use [nativeHeap] methods to allocate and, once done, release memory
 * Outside of cinterop world, I believe that Kotlin memory is automatically managed and collected
 * by the Kotlin Native runtime.
 */
private inline fun comments() = Unit