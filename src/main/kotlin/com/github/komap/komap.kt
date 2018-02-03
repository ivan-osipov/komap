package com.github.komap

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

typealias Mappings = MutableMap<KClass<*>, MutableMap<KClass<*>, TypeMapping<*, *>>>

fun mapping(init: Mapper.() -> Unit) = Mapper().apply { init() }

class Mapping(val mappings: Mappings = HashMap()) {
    fun merge(otherMapping: Mapping) = mappings.putAll(otherMapping.mappings)
}

open class Mapper(val mapping: Mapping = Mapping()) {

    fun expand(vararg extensions: Mapper) = extensions.forEach { this.mapping.merge(it.mapping) }

    inline fun <reified T : Any, reified R : Any> plain(init: TypeMapping<T, R>.() -> Unit = {}) {
        val typeMapping = TypeMapping<T, R>()
        typeMapping.init()
        mapping.mappings[T::class, R::class] = typeMapping
    }

    inline fun <reified T : Any, reified R : Any> smart(noinline mapping: T.() -> R) {
        this.mapping.mappings[T::class, R::class] = TypeMapping(mapping)
    }

    inline fun <reified T: Any, reified R: Any> mapTo(source: T): R {
        return mapTo(T::class, R::class, source)
    }

    fun <T: Any, R: Any> mapTo(classT: KClass<T>, classR: KClass<R>, source: T): R {
        return this.mapping.mappings[classT, classR]?.let { typeMapping ->
            typeMapping.customMapping?.let { customMapping ->
                @Suppress("UNCHECKED_CAST")
                val mapping = customMapping as T.() -> R
                return source.mapping()
            }
            val result = classR.java.getConstructor().newInstance()
            @Suppress("UNCHECKED_CAST")
            val fieldsMappings = typeMapping.fieldsMappings as List<TypeMapping<T, R>.FieldMapping<*, *>>
            fieldsMappings.forEach { fieldMapping ->
                processField(fieldMapping, source, result)
            }
            result
        }.let {
            if(classR.java.isAssignableFrom(classT.java)) {
                @Suppress("UNCHECKED_CAST") //reason: outer `if` statement
                return source as R
            }
            return@let it
        } ?: throw IllegalStateException("Mapping from ${classT.java.name} to ${classR.java.name} doesn't exist")
    }

    private fun <T : Any, R: Any, A: Any, B: Any> processField(fieldMapping: TypeMapping<T, R>.FieldMapping<A, B>, source: T, target: R) {
        val sourceFieldGetter = fieldMapping.sourceFieldGetter
        val destinationFieldSetter = fieldMapping.destinationFieldSetter

        val nullableFieldValue = sourceFieldGetter.invoke(source)
        nullableFieldValue?.let { fieldValue ->
            val setterParam: B = mapTo(fieldMapping.fieldAType, fieldMapping.fieldBType, fieldValue)
            target.destinationFieldSetter(setterParam)
        }
    }

    inline fun <reified T: Any, reified R: Any> mapTo(source: Collection<T>): List<R> {
        return source.asSequence().filterNotNull().map {
            mapTo(T::class, R::class, it)
        }.toList()
    }

}

class TypeMapping<A : Any, B : Any>(val customMapping: (A.() -> B)? = null) {
    val fieldsMappings: MutableList<FieldMapping<*, *>> = ArrayList()

    fun <X: Any, Y: Any> field(fieldAType: KClass<X>, fieldBType: KClass<Y>,
                                       fieldA: A.() -> X, fieldB: B.(Y) -> Unit) {
        fieldsMappings.add(FieldMapping(fieldAType, fieldBType, fieldA, fieldB))
    }

    inline fun <reified X: Any, reified Y: Any> flexible(noinline fieldA: A.() -> X, noinline fieldB: B.(Y) -> Unit) {
        field(X::class, Y::class, fieldA, fieldB)
    }

    inline fun <reified X: Any, reified Y: Any> flexible(noinline fieldA: A.() -> X, fieldB: KMutableProperty1<B, Y>)
            = flexible(fieldA, fieldB::set)

    inline fun <reified X: Any> strict(noinline fieldA: A.() -> X, noinline fieldB: B.(X) -> Unit) {
        flexible(fieldA, fieldB)
    }

    inline fun <reified X: Any> strict(noinline fieldA: A.() -> X, fieldB: KMutableProperty1<B, X>) {
        flexible(fieldA, fieldB)
    }

    inner class FieldMapping<X: Any, Y:Any>(val fieldAType: KClass<X>,
                                            val fieldBType: KClass<Y>,
                                            val sourceFieldGetter: A.() -> X?,
                                            val destinationFieldSetter: B.(Y) -> Unit)

}


operator fun <T : Any, R : Any> Mappings.set(typeA: KClass<T>, typeB: KClass<R>, mapping: TypeMapping<T, R>) {
    this.computeIfAbsent(typeA) {
        HashMap()
    }[typeB] = mapping
}

operator fun <T : Any, R : Any> Mappings.get(typeA: KClass<T>, typeB: KClass<R>) = this[typeA]?.get(typeB)

